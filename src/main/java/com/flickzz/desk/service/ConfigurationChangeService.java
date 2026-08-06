package com.flickzz.desk.service;

import com.flickzz.desk.config.RemarkType;
import com.flickzz.desk.model.*;
import com.flickzz.desk.repo.BPConfigurationChangeRequestRemarkRepository;
import com.flickzz.desk.repo.BPConfigurationChangeRequestRepository;
import com.flickzz.desk.repo.CompanyApproverRepository;
import com.flickzz.desk.service.notification.ConfigNotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import static com.flickzz.desk.config.FlickzzDeskConstants.DRAFTED;

@Service
public class ConfigurationChangeService {

    private static final Logger log = LoggerFactory.getLogger(ConfigurationChangeService.class);

    @Autowired
    ConfigNotificationService configNotificationService;

    @Autowired
    BPConfigurationChangeRequestRepository bpConfigurationChangeRequestRepository;

    @Autowired
    BPConfigurationChangeRequestRemarkRepository bpConfigurationChangeRequestRemarkRepository;

    @Autowired
    CompanyApproverRepository companyApproverRepository;

    public void addConfigurationChangeRequest(BPConfiguration configuration, Long changedRequestId, Long sourceChangeId,
                                               Boolean isBpPriority, Boolean isBpSla, Boolean isCategory, Boolean isSupportGroup, Boolean isAssignment,
                                               String operation, CompanyMaster requestedByOrg, Long requestedByUserId, CompanyMaster approvalOrg,
                                               Boolean isCreatorAdmin, String remarks) {
        try {
            BPConfigurationChangeRequest changeRequest = BPConfigurationChangeRequest.builder()
                    .configuration(configuration)
                    .changedRequestId(changedRequestId)
                    .sourceChangeId(sourceChangeId)
                    .bpPriority(isBpPriority != null ? isBpPriority : Boolean.FALSE)
                    .bpSla(isBpSla != null ? isBpSla : Boolean.FALSE)
                    .category(isCategory != null ? isCategory : Boolean.FALSE)
                    .supportGroup(isSupportGroup != null ? isSupportGroup : Boolean.FALSE)
                    .assignment(isAssignment != null ? isAssignment : Boolean.FALSE)
                    .operation(operation)
                    .requestedByOrg(requestedByOrg)
                    .requestedByUserId(requestedByUserId)
                    .approvalOrg(approvalOrg)
                    .status(DRAFTED)
                    .createdBy(requestedByUserId)
                    .isCreatorAdmin(isCreatorAdmin)
                    .requestedOn(LocalDateTime.now())
                    .createdOn(LocalDateTime.now())
                    .build();

            int totalInternalApprovals = (int) companyApproverRepository
                    .findAll()
                    .stream()
                    .filter(approver -> approver.getCompany() != null && approver.getCompany().getCompanyId().equals(requestedByOrg.getCompanyId())
                            && approver.getIsActive())
                    .map(CompanyApprover::getLevel)
                    .distinct()
                    .count();

            int totalBpApprovals = (int) companyApproverRepository
                    .findAll()
                    .stream()
                    .filter(approver -> approver.getCompany() != null && approver.getCompany().getCompanyId().equals(approvalOrg.getCompanyId())
                            && approver.getIsActive())
                    .map(CompanyApprover::getLevel)
                    .distinct()
                    .count();

            changeRequest.setTotalInternalApprovalLevels(totalInternalApprovals > 0 ? totalInternalApprovals : 0);
            changeRequest.setTotalBpApprovalLevels(totalBpApprovals > 0 ? totalBpApprovals : 0);
            changeRequest.setCurrentInternalApprovalLevel(0);
            changeRequest.setCurrentBpApprovalLevel(0);
            bpConfigurationChangeRequestRepository.save(changeRequest);

            BPConfigurationChangeRequestRemark remark = BPConfigurationChangeRequestRemark.builder()
                    .configurationChangeRequest(changeRequest).remarkType(String.valueOf(RemarkType.valueOf(operation)))
                    .approverLevel(0).approvalStatus(DRAFTED).userId(requestedByUserId).organizationId(requestedByOrg.getCompanyId())
                    .remark(remarks).createdOn(LocalDateTime.now())
                    .build();
            bpConfigurationChangeRequestRemarkRepository.save(remark);

            configNotificationService.notifyConfigChange(changeRequest, DRAFTED);
        } catch (Exception e) {
            log.error("Error adding configuration change request", e);
        }
    }
}
