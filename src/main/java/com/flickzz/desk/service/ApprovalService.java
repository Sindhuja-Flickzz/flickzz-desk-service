package com.flickzz.desk.service;

import com.flickzz.desk.exception.FlickzzDeskException;
import com.flickzz.desk.mapper.CommonMapper;
import com.flickzz.desk.model.*;
import com.flickzz.desk.repo.*;
import com.flickzz.desk.service.notification.NotificationService;
import com.flickzz.desk.vo.ApprovalMasterVO;
import com.flickzz.desk.vo.BPConfigurationChangeRequestRemarkVO;
import com.flickzz.desk.vo.request.BpConfigRequestVO;
import com.flickzz.desk.vo.request.SystemAuditRequest;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
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
    @Autowired
    TicketApproverRepository ticketApproverRepository;
    @Autowired
    TicketApproverRemarkRepository ticketApproverRemarkRepository;
    @Autowired
    TicketMasterRepository ticketMasterRepository;
    @Autowired
    StatusMasterRepository statusMasterRepository;
    @Autowired
    TicketAuditRepository ticketAuditRepository;
    @Autowired
    AuditService auditService;

    public List<ApprovalMasterVO> getBusinessPartnerApprovalList(Long userId) {
        log.info(generateLog(ENTRY, this.getClass().getName()));
        try {
            if (userId == null) {
                throw new FlickzzDeskException(INVALID_FIELD,
                        getDescription(INVALID_FIELD.getDescription(), "User ID"));
            }

            List<ApprovalMaster> approvals = approvalRepository.findByApproverUserIdAndActiveTrue(userId);
            log.info(generateLog(EXIT, this.getClass().getName()));
            return approvals.stream().map(mapper::toConfigChangeApprovalVO).toList();
        } catch (FlickzzDeskException e) {
            throw e;
        } catch (Exception e) {
            log.error("Exception in getBusinessPartnerApprovalList method in BusinessPartnerService");
            throw new FlickzzDeskException(DEFAULT_ERROR_CODE);
        }
    }

    @Transactional
    public ApprovalMasterVO actionOnConfigApproval(BpConfigRequestVO request) {
        log.info(generateLog(ENTRY, this.getClass().getName()));
        try {
            if (request == null || StringUtils.isBlank(request.getAction())) {
                throw new FlickzzDeskException(INVALID_TEXT,
                        getDescription(INVALID_TEXT.getDescription(), "Request"));
            }
            if (request.getApprovalId() == null) {
                throw new FlickzzDeskException(INVALID_FIELD,
                        getDescription(INVALID_FIELD.getDescription(), "Approval Id"));
            }

            ApprovalMaster approval = approvalRepository.findById(request.getApprovalId())
                    .orElseThrow(() -> new FlickzzDeskException(DOES_NOT_EXIST,
                            getDescription(DOES_NOT_EXIST.getDescription(), "Config Change Approval")));

            if ("RITM".equalsIgnoreCase(approval.getRequestType())) {
                return actionOnRitmApproval(approval, request);
            }

            Optional<CompanyApprover> approver = companyApproverRepository.findByAgentUserUserId(request.getUpdatedBy());
            if (approver.isEmpty()) {
                throw new FlickzzDeskException(DOES_NOT_EXIST,
                        getDescription(DOES_NOT_EXIST.getDescription(), "Company Approver"));
            }

            CompanyApprover companyApprover = approver.get();
            if (companyApprover.getIsActive() == null || !companyApprover.getIsActive()) {
                throw new FlickzzDeskException(INVALID_FIELD, "Your privilege to approve change request is not valid");
            }

            if (companyApprover.getLevel() == null || companyApprover.getLevel() <= 0) {
                throw new FlickzzDeskException(INVALID_FIELD, "Your privilege to approve change request is not valid");
            }

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

    private ApprovalMasterVO actionOnRitmApproval(ApprovalMaster approval, BpConfigRequestVO request) {
        if (request.getUpdatedBy() == null || !Boolean.TRUE.equals(approval.getActive())
                || !PENDING.equalsIgnoreCase(approval.getStatus())
                || !Objects.equals(approval.getApproverUserId(), request.getUpdatedBy())) {
            throw new FlickzzDeskException(INVALID_FIELD, "Approval does not belong to current approver");
        }
        TicketApprover ticketApprover = ticketApproverRepository.findById(approval.getRequestId())
                .filter(value -> Boolean.TRUE.equals(value.getIsActive()))
                .orElseThrow(() -> new FlickzzDeskException(DOES_NOT_EXIST,
                        getDescription(DOES_NOT_EXIST.getDescription(), "Active RITM approver with ID " + approval.getRequestId())));
        AgentMaster actingAgent = ticketApprover.getApproverAgent();
        if (actingAgent == null || actingAgent.getUser() == null
                || !Objects.equals(actingAgent.getUser().getUserId(), request.getUpdatedBy())) {
            throw new FlickzzDeskException(INVALID_FIELD, "Approval does not belong to current approver");
        }
        TicketMaster ritm = ticketMasterRepository.findById(ticketApprover.getTicket().getTicketId())
                .orElseThrow(() -> new FlickzzDeskException(DOES_NOT_EXIST,
                        getDescription(DOES_NOT_EXIST.getDescription(), "RITM")));

        String previousApprovalStatus = approval.getStatus();
        String approvalStatus = approvalStatusForAction(request.getAction());
        LocalDateTime now = LocalDateTime.now();
        approval.setStatus(approvalStatus);
        approval.setUpdatedBy(request.getUpdatedBy());
        approval.setDescription(request.getRemarks() != null ? request.getRemarks() : approval.getDescription());
        approval.setUpdatedOn(now);
        if (APPROVE.equalsIgnoreCase(request.getAction())) {
            approval.setApprovedOn(now);
        }
        ticketApprover.setApprovalStatus(approvalStatus);
        ticketApprover.setUpdatedBy(actingAgent.getAgentId());
        if (APPROVE.equalsIgnoreCase(request.getAction())) {
            ticketApprover.setApprovedOn(now);
        }
        updateRitmApprovalRemark(ticketApprover, ritm, request, now);
        approvalRepository.saveAndFlush(approval);
        ticketApproverRepository.saveAndFlush(ticketApprover);

        boolean completed = false;
        if (DECLINE.equalsIgnoreCase(request.getAction())) {
            deactivateAllRitmApprovals(ritm, actingAgent, request);
            completed = true;
        } else if (REQUEST_CLARIFICATION.equalsIgnoreCase(request.getAction())) {
            List<AgentMaster> otherApprovers = ticketApproverRepository
                    .findByTicket_TicketIdAndIsActiveTrueOrderByApproverSequenceAsc(ritm.getTicketId()).stream()
                    .filter(value -> value.getApproverAgent() != null && value.getApproverAgent().getUser() != null
                            && !Objects.equals(value.getApproverAgent().getUser().getUserId(), request.getUpdatedBy()))
                    .map(value -> value.getApproverAgent())
                    .toList();
            notificationService.notifyRitmApprovalClarification(ritm, request.getUpdatedBy(),
                    request.getIsUpdatedByAdmin(), otherApprovers, request.getRemarks());
        } else if (APPROVE.equalsIgnoreCase(request.getAction())) {
            RequestApproverConfig config = ticketApprover.getApproverConfig();
            if (config != null) {
                if (Boolean.TRUE.equals(config.getFollowSequence())) {
                    completed = advanceRitmApprovalSequence(ritm, ticketApprover, approval, actingAgent, request);
                } else {
                    deactivateOtherRitmApprovals(ritm, ticketApprover, approval, actingAgent, request);
                    completed = true;
                }
            } else if (!Boolean.TRUE.equals(ticketApprover.getIsGroupApprover())) {
                completeRitmApproval(ritm, actingAgent, request.getUpdatedBy(), request.getIsUpdatedByAdmin());
                deactivateOtherRitmApprovals(ritm, ticketApprover, approval, actingAgent, request);
                completed = true;
            } else {
                if (config == null) {
                    throw new FlickzzDeskException(INVALID_FIELD, "RITM group approver config is missing");
                }
                if (Boolean.TRUE.equals(config.getIsAnyApprovalSufficient())) {
                    completeRitmApproval(ritm, actingAgent, request.getUpdatedBy(), request.getIsUpdatedByAdmin());
                    deactivateOtherRitmApprovals(ritm, ticketApprover, approval, actingAgent, request);
                    completed = true;
                } else if (Boolean.TRUE.equals(config.getFollowSequence())) {
                    completed = advanceRitmApprovalSequence(ritm, ticketApprover, approval, actingAgent, request);
                } else {
                    List<TicketApprover> approvers = ticketApproverRepository
                            .findByTicket_TicketIdAndIsActiveTrueOrderByApproverSequenceAsc(ritm.getTicketId());
                    if (!approvers.isEmpty() && approvers.stream()
                            .allMatch(value -> APPROVED.equalsIgnoreCase(value.getApprovalStatus()))) {
                        completeRitmApproval(ritm, actingAgent, request.getUpdatedBy(), request.getIsUpdatedByAdmin());
                        completed = true;
                    }
                }
            }
        }

        String outcome = DECLINE.equalsIgnoreCase(request.getAction()) ? "DECLINED"
                : REQUEST_CLARIFICATION.equalsIgnoreCase(request.getAction()) ? "CLARIFICATION_REQUESTED"
                : completed ? "COMPLETED" : "UPDATED";
        recordRitmApprovalAudit(ritm, approval, actingAgent, request.getUpdatedBy(), previousApprovalStatus,
                approvalStatus, outcome, request.getAction());
        return mapper.toConfigChangeApprovalVO(approval);
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

    private String approvalStatusForAction(String action) {
        if (APPROVE.equalsIgnoreCase(action)) return APPROVED;
        if (DECLINE.equalsIgnoreCase(action)) return DECLINED;
        if (REQUEST_CLARIFICATION.equalsIgnoreCase(action)) return REQUEST_CLARIFICATION;
        return REQUEST_CLARIFICATION;
    }

    private void updateRitmApprovalRemark(TicketApprover ticketApprover, TicketMaster ritm,
                                          BpConfigRequestVO request, LocalDateTime now) {
        TicketApproverRemark remark = new TicketApproverRemark();
        remark.setTicketApprover(ticketApprover);
        remark.setTicket(ritm);
        remark.setRemarkType(request.getAction().equalsIgnoreCase(APPROVE) ? APPROVED : DECLINED);
        remark.setRemark(request.getRemarks() != null ? request.getRemarks() : "");
        remark.setCreatedBy(request.getUpdatedBy());
        remark.setCreatedOn(now);
        remark.setIsActive(true);
        ticketApproverRemarkRepository.saveAndFlush(remark);
    }

    private void deactivateAllRitmApprovals(TicketMaster ritm, AgentMaster actingAgent,
                                            BpConfigRequestVO request) {
        List<TicketApprover> allApprovers = ticketApproverRepository.findByTicket_TicketId(ritm.getTicketId());
        List<TicketApprover> activeApprovers = allApprovers.stream()
                .filter(value -> Boolean.TRUE.equals(value.getIsActive()))
                .toList();
        LocalDateTime now = LocalDateTime.now();

        activeApprovers.forEach(value -> {
            String oldValue = "{\"status\":\"" + value.getApprovalStatus() + "\",\"active\":true}";
            value.setIsActive(false);
            value.setUpdatedBy(actingAgent.getAgentId());
            value.setUpdatedOn(now);
            auditService.recordAudit(SystemAuditRequest.builder().module("RITM").area("RITM Approval")
                    .entityName("RitmApprover").entityId(value.getTicketApproverId()).action("DELETE")
                    .oldValue(oldValue).newValue("{\"status\":\"" + value.getApprovalStatus() + "\",\"active\":false}")
                    .userId(request.getUpdatedBy()).companyId(ritm.getCompany().getCompanyId())
                    .status(SUCCESS).build());
        });
        ticketApproverRepository.saveAllAndFlush(activeApprovers);

        List<Long> approverIds = allApprovers.stream().map(value -> value.getTicketApproverId()).toList();
        List<ApprovalMaster> activeApprovalRecords = approvalRepository
                .findByRequestTypeAndRequestIdIn("RITM", approverIds).stream()
                .filter(value -> Boolean.TRUE.equals(value.getActive()))
                .toList();
        activeApprovalRecords.forEach(value -> {
            String oldValue = "{\"status\":\"" + value.getStatus() + "\",\"active\":true}";
            value.setActive(false);
            value.setUpdatedBy(request.getUpdatedBy());
            value.setUpdatedOn(now);
            auditService.recordAudit(SystemAuditRequest.builder().module("RITM").area("RITM Approval")
                    .entityName("ApprovalMaster").entityId(value.getApprovalId()).action("DELETE")
                    .oldValue(oldValue).newValue("{\"status\":\"" + value.getStatus() + "\",\"active\":false}")
                    .userId(request.getUpdatedBy()).companyId(ritm.getCompany().getCompanyId())
                    .status(SUCCESS).build());
        });
        approvalRepository.saveAllAndFlush(activeApprovalRecords);

        List<AgentMaster> otherApprovers = activeApprovers.stream()
                .filter(value -> value.getApproverAgent() != null && value.getApproverAgent().getUser() != null
                        && !Objects.equals(value.getApproverAgent().getUser().getUserId(), request.getUpdatedBy()))
                .map(value -> value.getApproverAgent())
                .toList();
        notificationService.notifyRitmApprovalDeclined(ritm, request.getUpdatedBy(),
                request.getIsUpdatedByAdmin(), otherApprovers);
        ticketAuditRepository.saveAndFlush(TicketAudit.builder()
                .ticket(ritm)
                .actionType("APPROVAL_CANCEL")
                .description("All RITM approvals were cancelled after an approver declined")
                .changedBy(actingAgent.getAgentId())
                .build());
    }

    private boolean advanceRitmApprovalSequence(TicketMaster ritm, TicketApprover currentApprover,
                                                ApprovalMaster currentApproval, AgentMaster actingAgent,
                                                BpConfigRequestVO request) {
        List<TicketApprover> activeApprovers = ticketApproverRepository
                .findByTicket_TicketIdAndIsActiveTrueOrderByApproverSequenceAsc(ritm.getTicketId());
        Optional<TicketApprover> nextApprover = activeApprovers.stream()
                .filter(value -> value.getApproverSequence() != null
                        && value.getApproverSequence() > currentApprover.getApproverSequence())
                .min(Comparator.comparing(TicketApprover::getApproverSequence));
        if (nextApprover.isEmpty()) {
            deactivateOtherRitmApprovals(ritm, currentApprover, currentApproval, actingAgent, request);
            return true;
        }

        TicketApprover next = nextApprover.get();
        List<ApprovalMaster> existing = approvalRepository.findByRequestTypeAndRequestId("RITM", next.getTicketApproverId());
        if (existing.stream().noneMatch(value -> Boolean.TRUE.equals(value.getActive()))) {
            ApprovalMaster nextApproval = ApprovalMaster.builder()
                    .requestId(next.getTicketApproverId())
                    .requestType("RITM")
                    .approvalType(DRAFTED)
                    .description("Approval for RITM " + ritm.getTicketNumber())
                    .approverType("GROUP")
                    .approverLevel(next.getApproverSequence())
                    .approverUserId(next.getApproverAgent().getUser().getUserId())
                    .approverOrgId(next.getApproverAgent().getOrganization().getCompanyId())
                    .status(PENDING)
                    .mandatory(Boolean.TRUE.equals(next.getIsMainApprover()))
                    .active(true)
                    .createdBy(next.getCreatedBy())
                    .createdOn(LocalDateTime.now())
                    .build();
            nextApproval = approvalRepository.saveAndFlush(nextApproval);
            auditService.recordAudit(SystemAuditRequest.builder().module("RITM").area("RITM Approval")
                    .entityName("ApprovalMaster").entityId(nextApproval.getApprovalId()).action(CREATE)
                    .newValue("{\"ritmApproverId\":" + nextApproval.getRequestId()
                            + ",\"approverUserId\":" + nextApproval.getApproverUserId()
                            + ",\"approverLevel\":" + nextApproval.getApproverLevel()
                            + ",\"status\":\"" + nextApproval.getStatus() + "\",\"active\":true}")
                    .userId(request.getUpdatedBy()).companyId(ritm.getCompany().getCompanyId())
                    .status(SUCCESS).build());
            ticketAuditRepository.saveAndFlush(TicketAudit.builder()
                    .ticket(ritm)
                    .actionType("APPROVAL_NEXT_LEVEL")
                    .description("Approval activated for sequence level " + next.getApproverSequence())
                    .changedBy(actingAgent.getAgentId())
                    .build());
        }
        notificationService.notifyRitmApprovalRequired(ritm, request.getUpdatedBy(),
                request.getIsUpdatedByAdmin(), List.of(next.getApproverAgent()), next.getApproverSequence());
        return false;
    }

    private void deactivateOtherRitmApprovals(TicketMaster ritm, TicketApprover currentApprover,
                                              ApprovalMaster currentApproval, AgentMaster actingAgent,
                                              BpConfigRequestVO request) {
        List<TicketApprover> activeApprovers = ticketApproverRepository
                .findByTicket_TicketIdAndIsActiveTrueOrderByApproverSequenceAsc(ritm.getTicketId());
        List<TicketApprover> others = activeApprovers.stream()
                .filter(value -> !Objects.equals(value.getTicketApproverId(), currentApprover.getTicketApproverId()))
                .toList();
        if (others.isEmpty()) return;

        LocalDateTime now = LocalDateTime.now();
        others.forEach(value -> {
            String oldValue = "{\"status\":\"" + value.getApprovalStatus() + "\",\"active\":true}";
            value.setIsActive(false);
            value.setUpdatedBy(actingAgent.getAgentId());
            value.setUpdatedOn(now);
            auditService.recordAudit(SystemAuditRequest.builder().module("RITM").area("RITM Approval")
                    .entityName("RitmApprover").entityId(value.getTicketApproverId()).action("DELETE")
                    .oldValue(oldValue).newValue("{\"status\":\"" + value.getApprovalStatus() + "\",\"active\":false}")
                    .userId(request.getUpdatedBy()).companyId(ritm.getCompany().getCompanyId())
                    .status(SUCCESS).build());
        });
        ticketApproverRepository.saveAllAndFlush(others);

        List<Long> otherIds = others.stream().map(TicketApprover::getTicketApproverId).toList();
        List<ApprovalMaster> otherApprovals = approvalRepository.findByRequestTypeAndRequestIdIn("RITM", otherIds)
                .stream().filter(value -> !Objects.equals(value.getApprovalId(), currentApproval.getApprovalId())
                        && Boolean.TRUE.equals(value.getActive())).toList();
        otherApprovals.forEach(value -> {
            String oldValue = "{\"status\":\"" + value.getStatus() + "\",\"active\":true}";
            value.setActive(false);
            value.setUpdatedBy(request.getUpdatedBy());
            value.setUpdatedOn(now);
            auditService.recordAudit(SystemAuditRequest.builder().module("RITM").area("RITM Approval")
                    .entityName("ApprovalMaster").entityId(value.getApprovalId()).action("DELETE")
                    .oldValue(oldValue).newValue("{\"status\":\"" + value.getStatus() + "\",\"active\":false}")
                    .userId(request.getUpdatedBy()).companyId(ritm.getCompany().getCompanyId()).status(SUCCESS).build());
        });
        approvalRepository.saveAllAndFlush(otherApprovals);
        notificationService.notifyRitmApprovalCancelled(ritm, request.getUpdatedBy(), request.getIsUpdatedByAdmin(),
                others.stream().map(TicketApprover::getApproverAgent).toList());
        ticketAuditRepository.saveAndFlush(TicketAudit.builder()
                .ticket(ritm)
                .actionType("APPROVAL_CANCEL")
                .description("Other RITM approvals were cancelled after approval completion")
                .changedBy(actingAgent.getAgentId())
                .build());
    }

    private void completeRitmApproval(TicketMaster ritm, AgentMaster actingAgent, Long actorUserId,
                                      Boolean actorIsAdmin) {
        Long companyId = ritm.getCompany().getCompanyId();
        StatusMaster approvedStatus = statusMasterRepository
                .findFirstByCompany_CompanyIdAndSequenceNoAndIsActiveTrue(companyId, 2)
                .orElseThrow(() -> new FlickzzDeskException(DOES_NOT_EXIST,
                        getDescription(DOES_NOT_EXIST.getDescription(), "Active RITM status with sequence 2")));
        String previousStatus = ritm.getStatus() != null ? ritm.getStatus().getStatusCode() : null;
        ritm.setStatus(approvedStatus);
        ritm.setUpdatedBy(actingAgent.getAgentId());
        ritm.setIsUpdaterAdmin(Boolean.TRUE.equals(actorIsAdmin));
        ticketMasterRepository.saveAndFlush(ritm);
        auditService.recordAudit(SystemAuditRequest.builder().module("RITM").area("RITM Approval")
                .entityName("RitmMaster").entityId(ritm.getTicketId()).action("STATUS_UPDATE")
                .oldValue("{\"status\":\"" + previousStatus + "\"}")
                .newValue("{\"statusId\":" + approvedStatus.getStatusId()
                        + ",\"status\":\"" + approvedStatus.getStatusCode() + "\"}")
                .userId(actorUserId).companyId(companyId).status(SUCCESS).build());
        ticketAuditRepository.saveAndFlush(TicketAudit.builder()
                .ticket(ritm)
                .actionType("APPROVAL")
                .description("RITM approval completed; status changed from " + previousStatus
                        + " to " + approvedStatus.getStatusCode())
                .changedBy(actingAgent.getAgentId())
                .build());
    }

    private void recordRitmApprovalAudit(TicketMaster ritm, ApprovalMaster approval, AgentMaster actingAgent,
                                         Long actorUserId, String oldStatus, String newStatus, String outcome,
                                         String action) {
        String oldValue = "{\"status\":\"" + oldStatus + "\"}";
        String newValue = "{\"status\":\"" + newStatus + "\",\"outcome\":\"" + outcome + "\"}";
        auditService.recordAudit(SystemAuditRequest.builder().module("RITM").area("RITM Approval")
                .entityName("ApprovalMaster").entityId(approval.getApprovalId()).action(action)
                .oldValue(oldValue).newValue(newValue).userId(actorUserId)
                .companyId(ritm.getCompany().getCompanyId()).status(SUCCESS).build());
        ticketAuditRepository.saveAndFlush(TicketAudit.builder()
                .ticket(ritm)
                .actionType("APPROVAL")
                .description("RITM approver " + actingAgent.getAgentId() + " changed approval status from "
                        + oldStatus + " to " + newStatus)
                .changedBy(actingAgent.getAgentId())
                .build());
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
