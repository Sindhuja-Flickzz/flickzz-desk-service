package com.flickzz.desk.service;

import com.flickzz.desk.exception.FlickzzDeskException;
import com.flickzz.desk.mapper.CommonMapper;
import com.flickzz.desk.model.CompanyMaster;
import com.flickzz.desk.model.EffortType;
import com.flickzz.desk.repo.CompanyMasterRepository;
import com.flickzz.desk.repo.EffortTypeRepository;
import com.flickzz.desk.vo.EffortTypeVO;
import com.flickzz.desk.vo.request.EffortTypeRequestVO;
import com.flickzz.desk.vo.request.EffortTypeStatusRequestVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.flickzz.desk.config.FlickzzDeskUtility.getDescription;
import static com.flickzz.desk.exception.FlickzzDeskErrorCodes.DOES_NOT_EXIST;
import static com.flickzz.desk.exception.FlickzzDeskErrorCodes.INVALID_REQUEST;

@Service
public class EffortTypeService {

    private static final Logger log = LoggerFactory.getLogger(EffortTypeService.class);

    private final EffortTypeRepository effortTypeRepository;
    private final CompanyMasterRepository companyMasterRepository;
    private final CommonMapper mapper;
    private final AuditService auditService;
    private final ObjectMapper objectMapper;

    public EffortTypeService(EffortTypeRepository effortTypeRepository,
                             CompanyMasterRepository companyMasterRepository,
                             CommonMapper mapper,
                             AuditService auditService,
                             ObjectMapper objectMapper) {
        this.effortTypeRepository = effortTypeRepository;
        this.companyMasterRepository = companyMasterRepository;
        this.mapper = mapper;
        this.auditService = auditService;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public List<EffortTypeVO> create(List<EffortTypeRequestVO> requests) {
        if (requests == null || requests.isEmpty()) {
            throw new FlickzzDeskException(INVALID_REQUEST,
                    getDescription(INVALID_REQUEST.getDescription(), "At least one effort type is required"));
        }

        return requests.stream().map(request -> {
            validate(request);
            CompanyMaster company = companyMasterRepository.findByCompanyIdAndIsActiveTrue(request.getOrgId())
                    .orElseThrow(() -> new FlickzzDeskException(DOES_NOT_EXIST,
                            getDescription(DOES_NOT_EXIST.getDescription(), "Company with ID " + request.getOrgId())));

            EffortType effortType = EffortType.builder()
                    .typeName(request.getTypeName().trim())
                    .sequence(request.getSequence())
                    .company(company)
                    .createdBy(request.getCreatedBy())
                    .updatedBy(request.getUpdatedBy())
                    .isCreatorAdmin(request.getIsCreatedByAdmin())
                    .isUpdaterAdmin(Boolean.TRUE.equals(request.getIsUpdatedByAdmin()))
                    .isActive(true)
                    .build();
            EffortType saved = effortTypeRepository.save(effortType);
            recordAudit(saved, "CREATE", null, null, request.getCreatedBy());
            return mapper.toEffortTypeVO(saved);
        }).toList();
    }

    private void validate(EffortTypeRequestVO request) {
        if (request == null || request.getTypeName() == null || request.getTypeName().isBlank()
                || request.getTypeName().trim().length() > 100
                || request.getSequence() == null || request.getSequence() < 1
                || request.getOrgId() == null || request.getCreatedBy() == null
                || request.getIsCreatedByAdmin() == null) {
            throw new FlickzzDeskException(INVALID_REQUEST,
                    getDescription(INVALID_REQUEST.getDescription(), "Effort type data is invalid"));
        }
    }

    @Transactional(readOnly = true)
    public List<EffortTypeVO> list(Long orgId, Boolean status) {
        if (status) {
            return mapper.toEffortTypeVOList(
                    effortTypeRepository.findByCompany_CompanyIdAndIsActiveTrueOrderBySequenceAscEffortTypeIdAsc(orgId));
        }
        return mapper.toEffortTypeVOList(
                effortTypeRepository.findByCompany_CompanyIdOrderBySequenceAscEffortTypeIdAsc(orgId));
    }

    @Transactional
    public void delete(Long effortId, Long orgId) {
        EffortType effortType = effortTypeRepository.findByEffortTypeIdAndCompany_CompanyId(effortId, orgId)
                .orElseThrow(() -> new FlickzzDeskException(DOES_NOT_EXIST,
                        getDescription(DOES_NOT_EXIST.getDescription(), "Effort type with ID " + effortId)));
        String oldValue = serializeSnapshot(effortType);
        effortType.setIsActive(false);
        EffortType saved = effortTypeRepository.save(effortType);
        Long userId = saved.getUpdatedBy() != null ? saved.getUpdatedBy() : saved.getCreatedBy();
        recordAudit(saved, "DELETE", oldValue, "[\"isActive\"]", userId);
    }

    @Transactional
    public EffortTypeVO changeStatus(EffortTypeStatusRequestVO request) {
        if (request == null || request.getEffortTypeId() == null || request.getEffortTypeId() <= 0
                || request.getOrgId() == null || request.getOrgId() <= 0
                || request.getIsActive() == null
                || request.getUpdatedBy() == null || request.getUpdatedBy() <= 0
                || request.getIsUpdaterAdmin() == null) {
            throw new FlickzzDeskException(INVALID_REQUEST,
                    getDescription(INVALID_REQUEST.getDescription(), "Effort type status data is invalid"));
        }

        EffortType effortType = effortTypeRepository
                .findByEffortTypeIdAndCompany_CompanyId(request.getEffortTypeId(), request.getOrgId())
                .orElseThrow(() -> new FlickzzDeskException(DOES_NOT_EXIST,
                        getDescription(DOES_NOT_EXIST.getDescription(),
                                "Effort type with ID " + request.getEffortTypeId())));

        String oldValue = serializeSnapshot(effortType);
        effortType.setIsActive(request.getIsActive());
        effortType.setUpdatedBy(request.getUpdatedBy());
        effortType.setIsUpdaterAdmin(request.getIsUpdaterAdmin());
        EffortType saved = effortTypeRepository.save(effortType);
        recordAudit(saved, "UPDATE", oldValue, "[\"isActive\",\"updatedBy\",\"isUpdaterAdmin\"]",
                request.getUpdatedBy());
        return mapper.toEffortTypeVO(saved);
    }

    private void recordAudit(EffortType effortType, String action, String oldValue,
                             String changedFields, Long userId) {
        auditService.recordAudit(mapper.toSystemAuditRequest(
                "Effort Type",
                "Effort Type",
                "EffortType",
                effortType.getEffortTypeId(),
                action,
                serializeSnapshot(effortType),
                oldValue,
                changedFields,
                userId,
                "SYSTEM",
                effortType.getCompany().getCompanyId(),
                "SUCCESS",
                null));
    }

    private String serializeSnapshot(EffortType effortType) {
        Map<String, Object> snapshot = new LinkedHashMap<>();
        snapshot.put("effortTypeId", effortType.getEffortTypeId());
        snapshot.put("typeName", effortType.getTypeName());
        snapshot.put("sequence", effortType.getSequence());
        snapshot.put("companyId", effortType.getCompany().getCompanyId());
        snapshot.put("isActive", effortType.getIsActive());
        snapshot.put("createdBy", effortType.getCreatedBy());
        snapshot.put("isCreatorAdmin", effortType.getIsCreatorAdmin());
        snapshot.put("updatedBy", effortType.getUpdatedBy());
        snapshot.put("isUpdaterAdmin", effortType.getIsUpdaterAdmin());
        try {
            return objectMapper.writeValueAsString(snapshot);
        } catch (Exception e) {
            log.error("Unable to serialize effort type audit snapshot", e);
            throw new IllegalStateException("Unable to serialize effort type audit snapshot", e);
        }
    }
}
