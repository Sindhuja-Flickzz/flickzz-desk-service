package com.flickzz.desk.service;

import com.flickzz.desk.exception.FlickzzDeskException;
import com.flickzz.desk.mapper.CommonMapper;
import com.flickzz.desk.model.ApprovalMaster;
import com.flickzz.desk.model.BPConfigurationChangeRequest;
import com.flickzz.desk.model.BPConfigurationChangeRequestRemark;
import com.flickzz.desk.model.CompanyApprover;
import com.flickzz.desk.repo.*;
import com.flickzz.desk.service.notification.NotificationService;
import com.flickzz.desk.vo.ApprovalMasterVO;
import com.flickzz.desk.vo.BPConfigurationChangeRequestRemarkVO;
import com.flickzz.desk.vo.request.BpConfigRequestVO;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static com.flickzz.desk.config.FlickzzDeskConstants.*;
import static com.flickzz.desk.config.FlickzzDeskUtility.generateLog;
import static com.flickzz.desk.config.FlickzzDeskUtility.getDescription;
import static com.flickzz.desk.exception.FlickzzDeskErrorCodes.*;

@Service
@SuppressWarnings("unused")
public class ApprovalService {

    private static final Logger log = LoggerFactory.getLogger(ApprovalService.class);

    @Autowired
    BPPriorityRepository bPPriorityRepository;

    @Autowired
    BPSlaRepository bpSlaRepository;

    @Autowired
    BPCategoryRepository bpCategoryRepository;

    @Autowired
    BPSupportGroupRepository bpSupportGroupRepository;

    @Autowired
    BPAssignmentRepository bpAssignmentRepository;

    @Autowired
    CommonMapper mapper;
    @Autowired
    BPConfigurationChangeRequestRepository bpConfigurationChangeRequestRepository;
    @Autowired
    BPConfigurationChangeRequestRemarkRepository bpConfigurationChangeRequestRemarkRepository;
    @Autowired
    CompanyApproverRepository companyApproverRepository;
    @Autowired
    ApprovalRepository approvalRepository;
    @Autowired
    NotificationService notificationService;

    public List<ApprovalMasterVO> getBusinessPartnerApprovalList(Long userId) {
        log.info(generateLog(ENTRY, this.getClass().getName()));
        try {
            if (userId == null) {
                throw new FlickzzDeskException(INVALID_FIELD,
                        getDescription(INVALID_FIELD.getDescription(), "User ID"));
            }

            List<ApprovalMaster> approvals = approvalRepository.findByApproverUserId(userId);
            log.info(generateLog(EXIT, this.getClass().getName()));
            return approvals.stream().map(mapper::toConfigChangeApprovalVO).toList();
        } catch (FlickzzDeskException e) {
            throw e;
        } catch (Exception e) {
            log.error("Exception in getBusinessPartnerApprovalList method in BusinessPartnerService");
            throw new FlickzzDeskException(DEFAULT_ERROR_CODE);
        }
    }

    public ApprovalMasterVO actionOnConfigApproval(BpConfigRequestVO request) {
        log.info(generateLog(ENTRY, this.getClass().getName()));
        try {
            if (request == null || StringUtils.isBlank(request.getAction())) {
                throw new FlickzzDeskException(INVALID_TEXT,
                        getDescription(INVALID_TEXT.getDescription(), "Request"));
            }

            Optional<CompanyApprover> approver = companyApproverRepository.findByAgentUserUserId(request.getUpdatedBy());
            if (approver.isEmpty()) {
                throw new FlickzzDeskException(DOES_NOT_EXIST,
                        getDescription(DOES_NOT_EXIST.getDescription(), "Company Approver"));
            }

            CompanyApprover companyApprover = approver.get();
            if (companyApprover.getIsActive() == null || !companyApprover.getIsActive()) {
                throw new FlickzzDeskException(INVALID_FIELD, "Your previlege to approve change request is not valid");
            }

            if (companyApprover.getLevel() == null || companyApprover.getLevel() <= 0) {
                throw new FlickzzDeskException(INVALID_FIELD, "Your previlege to approve change request is not valid");
            }

            if (request.getApprovalId() == null) {
                throw new FlickzzDeskException(INVALID_FIELD,
                        getDescription(INVALID_FIELD.getDescription(), "Approval Id"));
            }

            ApprovalMaster approval = approvalRepository.findById(request.getApprovalId())
                    .orElseThrow(() -> new FlickzzDeskException(DOES_NOT_EXIST,
                            getDescription(DOES_NOT_EXIST.getDescription(), "Config Change Approval")));

            BPConfigurationChangeRequest changeRequest = bpConfigurationChangeRequestRepository.findById(approval.getRequestId()).orElse(null);

            if (!Objects.equals(approval.getApproverUserId(), request.getUpdatedBy())) {
                throw new FlickzzDeskException(INVALID_FIELD, "Approval does not belong to current approver");
            }

            String action = request.getAction();

            approval.setUpdatedBy(request.getUpdatedBy());
            approval.setUpdatedOn(LocalDateTime.now());
            approval.setStatus(request.getAction().equalsIgnoreCase(APPROVE) ? APPROVED : request.getAction().equalsIgnoreCase(DECLINE) ? DECLINED : REQUEST_CLARIFICATION);
            saveChangeRequestRemark(approval, request.getAction(), request.getRemarks());

            if (approval.getApproverLevel().equals(MANDATORY_APPROVER_LEVEL)) {
                if (DECLINE.equalsIgnoreCase(action)) {
                    if ("BP".equalsIgnoreCase(approval.getApproverType())) {
                        applyDeclineApproval(approval, request);
                    } else if ("Internal".equalsIgnoreCase(approval.getApproverType())) {
                        approval.setApprovedOn(LocalDateTime.now());
                        if (changeRequest != null && changeRequest.getChangedRequestId() != null
                                && changeRequest.getSourceChangeId() != null &&
                                changeRequest.getChangedRequestId().equals(changeRequest.getSourceChangeId())) {
                            applyDeclineApproval(approval, request);
                        } else {
                            notificationService.notifyBpApprovalConfigChange(changeRequest, approval.getApprovalType());
                        }
                    }
                } else if (REQUEST_CLARIFICATION.equalsIgnoreCase(action)) {
                    requestClarification(approval, request);
                } else if (APPROVE.equalsIgnoreCase(action) && "BP".equalsIgnoreCase(approval.getApproverType())) {
                    applyApproval(approval);
                    approval.setApprovedOn(LocalDateTime.now());
                } else if (APPROVE.equalsIgnoreCase(action) && "Internal".equalsIgnoreCase(approval.getApproverType())) {
                    approval.setApprovedOn(LocalDateTime.now());
                    if (changeRequest != null && changeRequest.getChangedRequestId() != null
                            && changeRequest.getSourceChangeId() != null &&
                            changeRequest.getChangedRequestId().equals(changeRequest.getSourceChangeId())) {
                        applyApproval(approval);
                    } else {
                        notificationService.notifyBpApprovalConfigChange(changeRequest, approval.getApprovalType());
                    }
                }
            } else {
                approval.setStatus(action);
            }

            updateChangeRequestProgress(approval, action);
            approvalRepository.save(approval);
            return mapper.toConfigChangeApprovalVO(approval);
        } catch (FlickzzDeskException e) {
            throw e;
        } catch (Exception e) {
            log.error("Exception in actionOnConfigApproval method in BusinessPartnerService", e);
            throw new FlickzzDeskException(DEFAULT_ERROR_CODE);
        }
    }

    private void saveChangeRequestRemark(ApprovalMaster approval, String action, String remarkText) {
        BPConfigurationChangeRequest changeRequest = approval.getRequestId() == null ? null : bpConfigurationChangeRequestRepository.findById(approval.getRequestId()).orElse(null);
        if (changeRequest == null) {
            return;
        }
        BPConfigurationChangeRequestRemark remark = BPConfigurationChangeRequestRemark.builder()
                .configurationChangeRequest(changeRequest)
                .remarkType(action)
                .approval(approval)
                .approverLevel(approval.getApproverLevel())
                .approvalStatus(action.equalsIgnoreCase(APPROVE) ? APPROVED : action.equalsIgnoreCase(DECLINE) ? DECLINED : REQUEST_CLARIFICATION)
                .userId(approval.getApproverUserId())
                .organizationId(approval.getApproverOrgId())
                .remark(remarkText != null ? remarkText : "")
                .createdOn(LocalDateTime.now())
                .build();
        bpConfigurationChangeRequestRemarkRepository.save(remark);
    }

    private void applyDeclineApproval(ApprovalMaster approval, BpConfigRequestVO request) {
        BPConfigurationChangeRequest changeRequest = approval.getRequestId() == null ? null : bpConfigurationChangeRequestRepository.findById(approval.getRequestId()).orElse(null);
        if (changeRequest == null || changeRequest.getChangedRequestId() == null) {
            return;
        }

        Long changedId = changeRequest.getChangedRequestId();
        if (approval.getRequestType().equalsIgnoreCase("BP")) {
            if (Boolean.TRUE.equals(changeRequest.getBpPriority())) {
                bPPriorityRepository.findById(changedId).ifPresent(entity -> {
                    entity.setIsActive(INACTIVE);
                    entity.setIsUnderApproval(Boolean.FALSE);
                    bPPriorityRepository.save(entity);
                });
                if (changeRequest.getOperation().equalsIgnoreCase(DELETE)) {
                    bPPriorityRepository.findById(changeRequest.getSourceChangeId()).ifPresent(entity -> {
                        entity.setIsActive(INACTIVE);
                        entity.setIsUnderApproval(Boolean.FALSE);
                        bPPriorityRepository.save(entity);
                    });
                }
            } else if (Boolean.TRUE.equals(changeRequest.getBpSla())) {
                bpSlaRepository.findById(changedId).ifPresent(entity -> {
                    entity.setIsActive(INACTIVE);
                    entity.setIsUnderApproval(Boolean.FALSE);
                    bpSlaRepository.save(entity);
                });
                if (changeRequest.getOperation().equalsIgnoreCase(DELETE)) {
                    bpSlaRepository.findById(changeRequest.getSourceChangeId()).ifPresent(entity -> {
                        entity.setIsActive(INACTIVE);
                        entity.setIsUnderApproval(Boolean.FALSE);
                        bpSlaRepository.save(entity);
                    });
                }
            } else if (Boolean.TRUE.equals(changeRequest.getCategory())) {
                bpCategoryRepository.findById(changedId).ifPresent(entity -> {
                    entity.setIsActive(INACTIVE);
                    entity.setIsUnderApproval(Boolean.FALSE);
                    bpCategoryRepository.save(entity);
                });
                if (changeRequest.getOperation().equalsIgnoreCase(DELETE)) {
                    bpCategoryRepository.findById(changeRequest.getSourceChangeId()).ifPresent(entity -> {
                        entity.setIsActive(INACTIVE);
                        entity.setIsUnderApproval(Boolean.FALSE);
                        bpCategoryRepository.save(entity);
                    });
                }
            } else if (Boolean.TRUE.equals(changeRequest.getSupportGroup())) {
                bpSupportGroupRepository.findById(changedId).ifPresent(entity -> {
                    entity.setIsActive(INACTIVE);
                    entity.setIsUnderApproval(Boolean.FALSE);
                    bpSupportGroupRepository.save(entity);
                });
                if (changeRequest.getOperation().equalsIgnoreCase(DELETE)) {
                    bpSupportGroupRepository.findById(changeRequest.getSourceChangeId()).ifPresent(entity -> {
                        entity.setIsActive(INACTIVE);
                        entity.setIsUnderApproval(Boolean.FALSE);
                        bpSupportGroupRepository.save(entity);
                    });
                }
            } else if (Boolean.TRUE.equals(changeRequest.getAssignment())) {
                bpAssignmentRepository.findById(changedId).ifPresent(entity -> {
                    entity.setIsActive(INACTIVE);
                    entity.setIsUnderApproval(Boolean.FALSE);
                    bpAssignmentRepository.save(entity);
                });
                if (changeRequest.getOperation().equalsIgnoreCase(DELETE)) {
                    bpAssignmentRepository.findById(changeRequest.getSourceChangeId()).ifPresent(entity -> {
                        entity.setIsActive(INACTIVE);
                        entity.setIsUnderApproval(Boolean.FALSE);
                        bpAssignmentRepository.save(entity);
                    });
                }
            }

            changeRequest.setStatus(DECLINED);
            changeRequest.setUpdatedBy(approval.getUpdatedBy());
            changeRequest.setUpdatedOn(LocalDateTime.now());
            changeRequest.setCompletedOn(LocalDateTime.now());
            bpConfigurationChangeRequestRepository.save(changeRequest);
            approval.setStatus(DECLINED);
        }
    }

    private void requestClarification(ApprovalMaster approval, BpConfigRequestVO request) {
        approval.setStatus(REQUEST_CLARIFICATION);
        BPConfigurationChangeRequest changeRequest = approval.getRequestId() == null ? null : bpConfigurationChangeRequestRepository.findById(approval.getRequestId()).orElse(null);
        if (changeRequest == null) {
            return;
        }
        changeRequest.setStatus(REQUEST_CLARIFICATION);
        changeRequest.setUpdatedBy(approval.getUpdatedBy());
        changeRequest.setUpdatedOn(LocalDateTime.now());
        bpConfigurationChangeRequestRepository.save(changeRequest);

    }

    private void applyApproval(ApprovalMaster approval) {
        BPConfigurationChangeRequest changeRequest = approval.getRequestId() == null ? null : bpConfigurationChangeRequestRepository.findById(approval.getRequestId()).orElse(null);
        if (changeRequest == null || changeRequest.getChangedRequestId() == null) {
            return;
        }

        Long changedId = changeRequest.getChangedRequestId();
        if (Boolean.TRUE.equals(changeRequest.getBpPriority())) {
            bPPriorityRepository.findById(changedId).ifPresent(entity -> {
                entity.setIsActive(ACTIVE);
                entity.setIsUnderApproval(Boolean.FALSE);
                bPPriorityRepository.save(entity);
            });
            if (changeRequest.getOperation().equalsIgnoreCase(UPDATE) || changeRequest.getOperation().equalsIgnoreCase(DELETE)) {
                bPPriorityRepository.findById(changeRequest.getSourceChangeId()).ifPresent(entity -> {
                    entity.setIsActive(INACTIVE);
                    entity.setIsUnderApproval(Boolean.FALSE);
                    bPPriorityRepository.save(entity);
                });
            }
        } else if (Boolean.TRUE.equals(changeRequest.getBpSla())) {
            bpSlaRepository.findById(changedId).ifPresent(entity -> {
                entity.setIsActive(ACTIVE);
                entity.setIsUnderApproval(Boolean.FALSE);
                bpSlaRepository.save(entity);
            });
            if (changeRequest.getOperation().equalsIgnoreCase(UPDATE) || changeRequest.getOperation().equalsIgnoreCase(DELETE)) {
                bpSlaRepository.findById(changeRequest.getSourceChangeId()).ifPresent(entity -> {
                    entity.setIsActive(INACTIVE);
                    entity.setIsUnderApproval(Boolean.FALSE);
                    bpSlaRepository.save(entity);
                });
            }
        } else if (Boolean.TRUE.equals(changeRequest.getCategory())) {
            bpCategoryRepository.findById(changedId).ifPresent(entity -> {
                entity.setIsActive(ACTIVE);
                entity.setIsUnderApproval(Boolean.FALSE);
                entity.setSubCategories(entity.getSubCategories().stream().map(subCategory -> {
                    subCategory.setIsActive(ACTIVE);
                    return subCategory;
                }).toList());
                bpCategoryRepository.save(entity);
            });
            if (changeRequest.getOperation().equalsIgnoreCase(UPDATE) || changeRequest.getOperation().equalsIgnoreCase(DELETE)) {
                bpCategoryRepository.findById(changeRequest.getSourceChangeId()).ifPresent(entity -> {
                    entity.setIsActive(INACTIVE);
                    entity.setIsUnderApproval(Boolean.FALSE);
                    bpCategoryRepository.save(entity);
                });
            }
        } else if (Boolean.TRUE.equals(changeRequest.getSupportGroup())) {
            bpSupportGroupRepository.findById(changedId).ifPresent(entity -> {
                entity.setIsActive(ACTIVE);
                entity.setIsUnderApproval(Boolean.FALSE);
                bpSupportGroupRepository.save(entity);
            });
            if (changeRequest.getOperation().equalsIgnoreCase(UPDATE) || changeRequest.getOperation().equalsIgnoreCase(DELETE)) {
                bpSupportGroupRepository.findById(changeRequest.getSourceChangeId()).ifPresent(entity -> {
                    entity.setIsActive(INACTIVE);
                    entity.setIsUnderApproval(Boolean.FALSE);
                    bpSupportGroupRepository.save(entity);
                });
            }
        } else if (Boolean.TRUE.equals(changeRequest.getAssignment())) {
            bpAssignmentRepository.findById(changedId).ifPresent(entity -> {
                entity.setIsActive(ACTIVE);
                entity.setIsUnderApproval(Boolean.FALSE);
                bpAssignmentRepository.save(entity);
            });
            if (changeRequest.getOperation().equalsIgnoreCase(UPDATE) || changeRequest.getOperation().equalsIgnoreCase(DELETE)) {
                bpAssignmentRepository.findById(changeRequest.getSourceChangeId()).ifPresent(entity -> {
                    entity.setIsActive(INACTIVE);
                    entity.setIsUnderApproval(Boolean.FALSE);
                    bpAssignmentRepository.save(entity);
                });
            }
        }
        approval.setStatus(APPROVED);
    }

    private void updateChangeRequestProgress(ApprovalMaster approval, String action) {
        BPConfigurationChangeRequest changeRequest = approval.getRequestId() == null ? null : bpConfigurationChangeRequestRepository.findById(approval.getRequestId()).orElse(null);
        if (changeRequest == null) {
            return;
        }

        boolean saveRequest = false;
        if (APPROVE.equalsIgnoreCase(action) || DECLINE.equalsIgnoreCase(action)) {
            if ("Internal".equalsIgnoreCase(approval.getApproverType())) {
                changeRequest.setInternalApprovalCompleted(Boolean.TRUE);
                changeRequest.setCurrentInternalApprovalLevel(approval.getApproverLevel());
                changeRequest.setStatus(changeRequest.getChangedRequestId().equals(changeRequest.getSourceChangeId()) ? APPROVED : INTERNAL_APPROVED);
                saveRequest = true;
            } else if ("BP".equalsIgnoreCase(approval.getApproverType())) {
                changeRequest.setBpApprovalCompleted(Boolean.TRUE);
                changeRequest.setCurrentBpApprovalLevel(approval.getApproverLevel());
                changeRequest.setStatus(APPROVED);
                changeRequest.setCompletedOn(LocalDateTime.now());
                saveRequest = true;
            }
        } else if (REQUEST_CLARIFICATION.equalsIgnoreCase(action)) {
            changeRequest.setStatus(REQUEST_CLARIFICATION);
            saveRequest = true;
        } else {
            changeRequest.setStatus(action);
            saveRequest = true;
        }

        if (saveRequest) {
            changeRequest.setUpdatedBy(approval.getUpdatedBy());
            changeRequest.setUpdatedOn(LocalDateTime.now());
            bpConfigurationChangeRequestRepository.save(changeRequest);
        }
    }

    public List<BPConfigurationChangeRequestRemarkVO> getApprovalRemarks(Long approvalId) {
        log.info(generateLog(ENTRY, this.getClass().getName()));
        try {
            if (approvalId == null) {
                throw new FlickzzDeskException(INVALID_FIELD,
                        getDescription(INVALID_FIELD.getDescription(), "Approval Id"));
            }

            Optional<ApprovalMaster> approval = approvalRepository.findById(approvalId);
            if (approval.isEmpty()) {
                throw new FlickzzDeskException(DOES_NOT_EXIST,
                        getDescription(DOES_NOT_EXIST.getDescription(), "Config Change Approval"));
            }

            if (approval.get().getRequestId() == null) {
                return List.of();
            }
            BPConfigurationChangeRequest changeRequest = bpConfigurationChangeRequestRepository.findById(approval.get().getRequestId()).orElse(null);
            if (changeRequest == null) {
                return List.of();
            }

            List<BPConfigurationChangeRequestRemark> remarks = bpConfigurationChangeRequestRemarkRepository.findByConfigurationChangeRequest_CcrId(changeRequest.getCcrId());
            return remarks.stream().map(mapper::toBPConfigurationChangeRequestRemarkVO).toList();
        } catch (FlickzzDeskException e) {
            throw e;
        } catch (Exception e) {
            log.error("Exception in getApprovalRemarks method in BusinessPartnerService");
            throw new FlickzzDeskException(DEFAULT_ERROR_CODE);
        }
    }
}
