package com.flickzz.desk.service;

import static com.flickzz.desk.config.FlickzzDeskConstants.*;
import static com.flickzz.desk.config.FlickzzDeskUtility.*;
import static com.flickzz.desk.exception.FlickzzDeskErrorCodes.*;

import java.util.*;
import java.util.stream.Collectors;

import com.flickzz.desk.vo.request.BpConfigRequestVO;
import com.flickzz.desk.vo.request.CompanyMasterRequestVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.flickzz.desk.exception.FlickzzDeskException;
import com.flickzz.desk.mapper.CommonMapper;
import com.flickzz.desk.model.*;
import com.flickzz.desk.repo.*;
import com.flickzz.desk.vo.*;
import tools.jackson.databind.ObjectMapper;

@Service
@SuppressWarnings("unused")
public class BusinessPartnerService {

	private static final Logger log = LoggerFactory.getLogger(BusinessPartnerService.class);

	@Autowired
	CompanyMasterRepository companyMasterRepository;

	@Autowired
	BusinessPartnerRepository businessPartnerRepository;

	@Autowired
	BPConfigurationRepository bPConfigurationRepository;

	@Autowired
	TicketTypeMasterRepository ticketTypeMasterRepository;

	@Autowired
	BPPriorityRepository bPPriorityRepository;

	@Autowired
	BPSlaRepository bpSlaRepository;

	@Autowired
	BPCategoryRepository bpCategoryRepository;

	@Autowired
	BPSubCategoryRepository bpSubCategoryRepository;

	@Autowired
	BPSupportGroupRepository bpSupportGroupRepository;

	@Autowired
	BPSupportGroupMemberRepository bpSupportGroupMemberRepository;

	@Autowired
	BPSupportGroupManagerRepository bpSupportGroupManagerRepository;

	@Autowired
	BPAssignmentRepository bpAssignmentRepository;

	@Autowired
	AgentMasterRepository agentMasterRepository;

	@Autowired
	SystemAuditRepository systemAuditRepository;

	@Autowired
	private UserRepository userRepository; // your DB repo

	@Autowired
	private EnquiryRegistrationRepository enquiryRegistrationRepository;

	@Autowired
	AuditService auditService;

	@Autowired
	CommonMapper mapper;

	@Autowired
	ObjectMapper objectMapper;

	public BusinessPartnerVO createBusinessPartner(CompanyMasterRequestVO request) {
		log.info(generateLog("createBusinessPartner", this.getClass().getName()));
		try {
			if (request == null || request.getCompanyId() == null) {
				throw new FlickzzDeskException(DOES_NOT_EXIST,
						getDescription(DOES_NOT_EXIST.getDescription(), COMPANY));
			}

			if (request == null || request.getBpUid() == null) {
				throw new FlickzzDeskException(DOES_NOT_EXIST,
						"Update Company profile to create configuration");
			}

			if (request.getCompanyId().equals(request.getBpUid())) {
				throw new FlickzzDeskException(INVALID_FIELD,
						getDescription(INVALID_FIELD.getDescription(), "Business Partner"));
			}

			CompanyMaster bpCompany = companyMasterRepository.findByUidAndIsActive(request.getBpUid(), ACTIVE)
					.orElseThrow(() -> new FlickzzDeskException(DOES_NOT_EXIST,
							getDescription(DOES_NOT_EXIST.getDescription(), COMPANY)));

			if (businessPartnerRepository.existsBusinessPartnerMapping(request.getCompanyId(),
					bpCompany.getCompanyId()) > 0) {
				throw new FlickzzDeskException(ALREADY_EXISTS,
						getDescription(ALREADY_EXISTS.getDescription(), "Business Partner mapping"));
			}

			BusinessPartner entity = new BusinessPartner();
			entity.setCompany(companyMasterRepository.findById(request.getCompanyId())
					.orElseThrow(() -> new FlickzzDeskException(DOES_NOT_EXIST,
							getDescription(DOES_NOT_EXIST.getDescription(), COMPANY))));
			entity.setMappedCompany(bpCompany);
			entity.setIsBoth(Boolean.TRUE);
			entity.setCreatedBy(request.getCreatedBy());
			entity.setIsCreatorAdmin(request.getIsCreatedByAdmin());
			entity.setCallHorizon(request.getCallHorizonDays());
			entity.setValidFrom(request.getValidFrom());
			entity.setValidTo(request.getValidTo());
			entity.setRefNo(request.getRefNumber());
			entity.setRefDate(request.getRefDate());
			businessPartnerRepository.save(entity);

			// Record CREATE audit
			String newSnapshot = null;
			try {
				newSnapshot = objectMapper.writeValueAsString(buildBusinessPartnerSnapshot(entity));
			} catch (Exception ignore) {
			}

			java.util.Map<String, Object> changed = new java.util.HashMap<>();
			changed.put("isBoth", entity.getIsBoth());
			changed.put("callHorizon", entity.getCallHorizon());
			changed.put("validFrom", entity.getValidFrom());
			changed.put("validTo", entity.getValidTo());
			String changedStr = null;
			try {
				changedStr = objectMapper.writeValueAsString(changed);
			} catch (Exception ignore) {
			}

			auditService.recordAudit(mapper.toSystemAuditRequest("BusinessPartner", "Partner", "BusinessPartner",
					entity.getBusinessPartnerId(),
					CREATE,
					newSnapshot,
					null,
					changedStr,
					request.getCreatedBy(),
					loadUserNameByUserId(Long.valueOf(request.getCreatedBy())),
					request.getCompanyId(),
					SUCCESS,
					null));

			return mapper.toBusinessPartnerVO(entity);
		} catch (FlickzzDeskException e) {
			// record FlickzzDeskException as ERROR audit and rethrow
			String attempted = null;
			try {
				java.util.Map<String, Object> attemptedMap = new java.util.HashMap<>();
				attemptedMap.put("isBoth", Boolean.TRUE);
				attemptedMap.put("callHorizon", request != null ? request.getCallHorizonDays() : null);
				attemptedMap.put("validFrom", request != null ? request.getValidFrom() : null);
				attemptedMap.put("validTo", request != null ? request.getValidTo() : null);
				attempted = objectMapper.writeValueAsString(attemptedMap);
			} catch (Exception ignore) {
			}
			auditService.recordExceptionAudit(mapper.toSystemAuditRequest("BusinessPartner", "Partner", "BusinessPartner",
					null,
					CREATE,
					attempted,
					null,
					null,
					request != null ? request.getCreatedBy() : null,
					loadUserNameByUserId(Long.valueOf(request.getCreatedBy())),
					request != null ? request.getCompanyId() : null,
					FAILED,
					e.getDescription()), e);
			throw e;
		} catch (Exception e) {
			// record unexpected exception as ERROR audit
			String attempted = null;
			try {
				java.util.Map<String, Object> attemptedMap = new java.util.HashMap<>();
				attemptedMap.put("isBoth", Boolean.TRUE);
				attemptedMap.put("callHorizon", request != null ? request.getCallHorizonDays() : null);
				attemptedMap.put("validFrom", request != null ? request.getValidFrom() : null);
				attemptedMap.put("validTo", request != null ? request.getValidTo() : null);
				attempted = objectMapper.writeValueAsString(attemptedMap);
			} catch (Exception ignore) {
			}
			auditService.recordExceptionAudit(mapper.toSystemAuditRequest("BusinessPartner", "Partner", "BusinessPartner",
					null,
					CREATE,
					attempted,
					null,
					null,
					request != null ? request.getCreatedBy() : null,
					loadUserNameByUserId(Long.valueOf(request.getCreatedBy())),
					request != null ? request.getCompanyId() : null,
					FAILED,
					e.getMessage()), e);
			log.error("Exception in createBusinessPartner method in BusinessPartnerService", e);
			throw new FlickzzDeskException(DEFAULT_ERROR_CODE);
		}
	}

	private Map<String, Object> buildBusinessPartnerSnapshot(BusinessPartner bp) {
		Map<String, Object> snapshot = new HashMap<>();
		if (bp == null) {
			return snapshot;
		}
		try {
			snapshot.put("businessPartnerId", bp.getBusinessPartnerId());
			snapshot.put("isBoth", bp.getIsBoth());
			snapshot.put("isCreatorAdmin", bp.getIsCreatorAdmin());
			snapshot.put("callHorizon", bp.getCallHorizon());
			snapshot.put("validFrom", bp.getValidFrom());
			snapshot.put("validTo", bp.getValidTo());
			snapshot.put("refNo", bp.getRefNo());
			snapshot.put("refDate", bp.getRefDate());
			snapshot.put("createdBy", bp.getCreatedBy());
			snapshot.put("updatedBy", bp.getUpdatedBy());
			snapshot.put("createdAt", bp.getCreatedAt());
			snapshot.put("updatedAt", bp.getUpdatedAt());
			try {
				if (bp.getCompany() != null) {
					snapshot.put("companyId", bp.getCompany().getCompanyId());
				}
			} catch (Exception ignore) {
			}
			try {
				if (bp.getMappedCompany() != null) {
					snapshot.put("mappedCompanyId", bp.getMappedCompany().getCompanyId());
				}
			} catch (Exception ignore) {
			}
		} catch (Exception ignore) {
		}
		return snapshot;
	}

	public BPConfigurationVO getBusinessPartnerConfigurationList(String businessPartnerId) {
		log.info(generateLog("getBusinessPartnerConfigurationList", this.getClass().getName()));
		try {
			Optional<BPConfiguration> configurations = bPConfigurationRepository
					.findByBusinessPartnerBusinessPartnerIdAndIsActive(Long.valueOf(businessPartnerId), ACTIVE);
			return mapper.toBPConfigurationVO(configurations.get());
		} catch (FlickzzDeskException e) {
			throw e;
		} catch (Exception e) {
			log.error("Exception in getBusinessPartnerConfigurationList method in BusinessPartnerService");
			throw new FlickzzDeskException(DEFAULT_ERROR_CODE);
		}
	}

	public BPPriorityVO createBusinessPartnerPriorityConfiguration(BpConfigRequestVO request) {

		log.info(generateLog("createBusinessPartnerPriorityConfiguration", this.getClass().getName()));
		try {
			if (request == null || request.getBusinessPartnerId() == null) {
				throw new FlickzzDeskException(DOES_NOT_EXIST,
						getDescription(DOES_NOT_EXIST.getDescription(), BUSINESS_PARTNER));
			}

			Optional<BusinessPartner> businessPartner = businessPartnerRepository
					.findByBusinessPartnerIdAndIsActive(request.getBusinessPartnerId(), ACTIVE);
			if (!businessPartner.isPresent()) {
				throw new FlickzzDeskException(DOES_NOT_EXIST,
						getDescription(DOES_NOT_EXIST.getDescription(), BUSINESS_PARTNER));
			}

			Optional<TicketTypeMaster> ticketType = ticketTypeMasterRepository
					.findByTicketTypeIdAndIsActiveTrue(request.getTicketTypeId());
			if (ticketType == null) {
				throw new FlickzzDeskException(DOES_NOT_EXIST,
						getDescription(DOES_NOT_EXIST.getDescription(), TICKET_TYPE));
			}

			Optional<BPConfiguration> existingConfig = bPConfigurationRepository
					.findByBusinessPartnerBusinessPartnerIdAndIsActive(request.getBusinessPartnerId(), ACTIVE);

			BPConfiguration config = new BPConfiguration();

			if (!existingConfig.isPresent()) {
				config.setBusinessPartner(businessPartner.get());
				config.setCreatedBy(request.getCreatedBy());
				config.setIsCreatorAdmin(request.getIsCreatedByAdmin());
				bPConfigurationRepository.save(config);
			} else {
				config = existingConfig.get();
			}

			if (bPPriorityRepository.existsByConfigurationConfigurationIdAndTicketTypeTicketTypeIdAndLevel(
					config.getConfigurationId(), request.getTicketTypeId(), request.getLevel())) {
				throw new FlickzzDeskException(ALREADY_EXISTS,
						"Priority Level already exists for the selected Ticket Type.");
			}

			if (bPPriorityRepository.existsByConfigurationConfigurationIdAndTicketTypeTicketTypeIdAndCode(
					config.getConfigurationId(), request.getTicketTypeId(), request.getCode())) {
				throw new FlickzzDeskException(ALREADY_EXISTS,
						"Priority Code already exists for the selected Ticket Type.");
			}

			BPPriority bpPriority = mapper.toBPPriority(request, config, ticketType.get());
			// Prepare new snapshot for audit
			String newSnapshot = null;
			try {
				newSnapshot = objectMapper.writeValueAsString(buildPrioritySnapshot(bpPriority));
			} catch (Exception ignore) {
			}
			bPPriorityRepository.save(bpPriority);

			// Record CREATE audit
			java.util.Map<String, Object> changed = new java.util.HashMap<>();
			changed.put("level", bpPriority.getLevel());
			changed.put("code", bpPriority.getCode());
			changed.put("description", bpPriority.getDescription());
			String changedStr = null;
			try { changedStr = objectMapper.writeValueAsString(changed); } catch (Exception ignore) {}

			auditService.recordAudit(mapper.toSystemAuditRequest("BusinessPartner", "Priority", "BPPriority",
					bpPriority.getPriorityId(),
					CREATE,
					newSnapshot,
					null,
					changedStr,
					request.getCreatedBy(),
					loadUserNameByUserId(Long.valueOf(request.getCreatedBy())),
					request.getOrgId(),
					SUCCESS,
					null));

			return mapper.toBPPriorityVo(bpPriority);
		} catch (FlickzzDeskException e) {
			// record FlickzzDeskException as ERROR audit and rethrow
			String attempted = null;
			try {
				java.util.Map<String, Object> attemptedMap = new java.util.HashMap<>();
				attemptedMap.put("level", request != null ? request.getLevel() : null);
				attemptedMap.put("code", request != null ? request.getCode() : null);
				attemptedMap.put("description", request != null ? request.getDescription() : null);
				attempted = objectMapper.writeValueAsString(attemptedMap);
			} catch (Exception ignore) {
			}
			auditService.recordExceptionAudit(mapper.toSystemAuditRequest("BusinessPartner", "Priority", "BPPriority", null, CREATE,
					attempted, null, null, request != null ? request.getCreatedBy() : null, loadUserNameByUserId(Long.valueOf(request.getCreatedBy())), (request != null ? request.getOrgId() : null), FAILED, e.getDescription()), e);
				throw e;
		} catch (Exception e) {
			String attempted = null;
			try {
				java.util.Map<String, Object> attemptedMap = new java.util.HashMap<>();
				attemptedMap.put("level", request != null ? request.getLevel() : null);
				attemptedMap.put("code", request != null ? request.getCode() : null);
				attemptedMap.put("description", request != null ? request.getDescription() : null);
				attempted = objectMapper.writeValueAsString(attemptedMap);
			} catch (Exception ignore) {
			}
			auditService.recordExceptionAudit(mapper.toSystemAuditRequest("BusinessPartner", "Priority", "BPPriority",
					null,
					CREATE,
					attempted,
					null,
					null,
					request != null ? request.getCreatedBy() : null,
					loadUserNameByUserId(Long.valueOf(request.getCreatedBy())),
					request != null ? request.getOrgId() : null,
					FAILED,
					e.getMessage()), e);
			log.error("Exception in createBusinessPartnerPriorityConfiguration method in BusinessPartnerService", e);
			throw new FlickzzDeskException(DEFAULT_ERROR_CODE);
		}
	}

	public BPPriorityVO updateBusinessPartnerPriorityConfiguration(BpConfigRequestVO request) {
		log.info(generateLog("updateBusinessPartnerPriorityConfiguration", this.getClass().getName()));
		BPPriority bpPriority = null;
		try {
			if (request == null || request.getPriorityId() == null) {
				throw new FlickzzDeskException(DOES_NOT_EXIST,
						getDescription(DOES_NOT_EXIST.getDescription(), PRIORITY));
			}

			Optional<BPPriority> existingPriority = bPPriorityRepository
					.findByPriorityIdAndIsActive(request.getPriorityId(), ACTIVE);

			if (existingPriority.isEmpty()) {
				throw new FlickzzDeskException(DOES_NOT_EXIST,
						getDescription(DOES_NOT_EXIST.getDescription(), PRIORITY));
			}

			bpPriority = existingPriority.get();
			
			// Capture old values before update (use snapshot to avoid serializing full entity graph)
			String oldValue = "";
			try {
				oldValue = objectMapper.writeValueAsString(buildPrioritySnapshot(bpPriority));
			} catch (Exception ignore) {
			}
			
			// Track changed fields
			Map<String, Object> changedFields = new HashMap<>();
			if (!bpPriority.getCode().equals(request.getCode())) {
				changedFields.put("code", true);
			}
			if (!bpPriority.getDescription().equals(request.getDescription())) {
				changedFields.put("description", true);
			}
			if (!bpPriority.getUpdatedBy().equals(request.getUpdatedBy())) {
				changedFields.put("updatedBy", true);
			}
			
			// Update the priority
			bpPriority.setCode(request.getCode());
			bpPriority.setDescription(request.getDescription());
			bpPriority.setUpdatedBy(request.getUpdatedBy());
			bPPriorityRepository.save(bpPriority);
			
			// Save audit record if there are changes
			if (!changedFields.isEmpty()) {
				// New value snapshot
				String newSnapshot = null;
				try {
					newSnapshot = objectMapper.writeValueAsString(buildPrioritySnapshot(bpPriority));
				} catch (Exception ignore) {
				}
				String changedValue = null;
				try {
					changedValue = objectMapper.writeValueAsString(changedFields);
				} catch (Exception ignore) {
					changedValue = null;
				}
				auditService.recordAudit(mapper.toSystemAuditRequest("BusinessPartner", "Priority", "BPPriority", bpPriority.getPriorityId(), UPDATE, newSnapshot, oldValue, changedValue, request.getUpdatedBy(), loadUserNameByUserId(Long.valueOf(request.getUpdatedBy())), request.getOrgId(), SUCCESS, null));
			}
			
			return mapper.toBPPriorityVo(bpPriority);
		} catch (FlickzzDeskException e) {
			String newValue = null;
			try {
				if (request != null) {
					// create a simple representation of attempted new values
					Map<String, Object> attempted = new HashMap<>();
					attempted.put("code", request.getCode());
					attempted.put("description", request.getDescription());
					attempted.put("updatedBy", request.getUpdatedBy());
					try {
						newValue = objectMapper.writeValueAsString(attempted);
					} catch (Exception ignore) {
						newValue = null;
					}
				}
			} catch (Exception ignore) {
			}
			auditService.recordExceptionAudit(mapper.toSystemAuditRequest("BusinessPartner", "Priority", "BPPriority", bpPriority != null ? bpPriority.getPriorityId() : null, UPDATE, newValue, null, null, request != null ? request.getUpdatedBy() : null, loadUserNameByUserId(Long.valueOf(request.getUpdatedBy())), request.getOrgId(), FAILED, e.getMessage()), e);
			throw e;
		} catch (Exception e) {
			// Attempt to save error audit
			try {
				String oldValue = null; String newValue = null;
				try {
					if (bpPriority != null) {
						if (bpPriority.getPriorityId() != null) {
							try {
								oldValue = objectMapper.writeValueAsString(buildPrioritySnapshot(bpPriority));
							} catch (Exception ignore) {
								oldValue = null;
							}
						}
					}
				} catch (Exception ignore) {
				}
				// newValue: attempt to capture requested values
				try {
					if (request != null) {
						// create a simple representation of attempted new values
						Map<String, Object> attempted = new HashMap<>();
						attempted.put("code", request.getCode());
						attempted.put("description", request.getDescription());
						attempted.put("updatedBy", request.getUpdatedBy());
						try {
							newValue = objectMapper.writeValueAsString(attempted);
						} catch (Exception ignore) {
							newValue = null;
						}
					}
				} catch (Exception ignore) {
				}
				try {
					auditService.recordExceptionAudit(mapper.toSystemAuditRequest("BusinessPartner", "Priority", "BPPriority",
							bpPriority != null ? bpPriority.getPriorityId() : null,
							UPDATE,
							newValue,
							oldValue,
							null,
							request != null ? request.getUpdatedBy() : null,
							loadUserNameByUserId(Long.valueOf(request.getCreatedBy())),
							request.getOrgId(),
							FAILED,
							e.getMessage()), e);
				} catch (Exception ignore) {
					log.error("Failed to save error audit for updateBusinessPartnerPriorityConfiguration", ignore);
				}
			} catch (Exception ignore) {
				log.error("Unexpected error while recording audit", ignore);
			}
			log.error("Exception in updateBusinessPartnerPriorityConfiguration method in BusinessPartnerService", e);
			throw new FlickzzDeskException(DEFAULT_ERROR_CODE);
		}
	}

	public void deleteBusinessPartnerPriorityConfiguration(String priorityId, Long userId) {

		log.info(generateLog("deleteBusinessPartnerPriorityConfiguration", this.getClass().getName()));
		BPPriority bpPriority = null;
		try {
			Optional<BPPriority> existingPriority = bPPriorityRepository
					.findByPriorityIdAndIsActive(Long.valueOf(priorityId), ACTIVE);

			if (existingPriority.isEmpty()) {
				throw new FlickzzDeskException(DOES_NOT_EXIST,
						getDescription(DOES_NOT_EXIST.getDescription(), PRIORITY));
			}

			bpPriority = existingPriority.get();
			
			// Capture old snapshot before deletion
			String oldValue = null;
			try {
				oldValue = objectMapper.writeValueAsString(buildPrioritySnapshot(bpPriority));
			} catch (Exception ignore) {
			}

			validatePrioritySlaForDeletion(bpPriority);

			bPPriorityRepository.delete(bpPriority);
			Long companyId = bpPriority.getConfiguration() != null
					&& bpPriority.getConfiguration().getBusinessPartner() != null
					&& bpPriority.getConfiguration().getBusinessPartner().getCompany() != null
						? bpPriority.getConfiguration().getBusinessPartner().getCompany().getCompanyId()
						: null;
			// Record DELETE audit
			auditService.recordAudit(mapper.toSystemAuditRequest("BusinessPartner", "Priority", "BPPriority",
					bpPriority.getPriorityId(),
					DELETE,
					oldValue,
					null,
					null,
					userId,
					loadUserNameByUserId(Long.valueOf(userId)),
					companyId,
					SUCCESS,
					null));
		} catch (FlickzzDeskException e) {
			// record FlickzzDeskException as ERROR audit and rethrow
			try {
				Long entityId = null;
				Long companyId = null;
				try {
					entityId = Long.valueOf(priorityId);
				} catch (Exception ignore) {
				}
				if (bpPriority != null && bpPriority.getConfiguration() != null 
					&& bpPriority.getConfiguration().getBusinessPartner() != null
					&& bpPriority.getConfiguration().getBusinessPartner().getCompany() != null) {
					companyId = bpPriority.getConfiguration().getBusinessPartner().getCompany().getCompanyId();
				}
				auditService.recordExceptionAudit(mapper.toSystemAuditRequest("BusinessPartner", "Priority", "BPPriority",
						entityId, DELETE,
						null, null, null, userId, loadUserNameByUserId(Long.valueOf(userId)), companyId, FAILED, e.getDescription()), e);
			} catch (Exception ignore) {
			}
			throw e;
		} catch (Exception e) {
			// record generic exception as ERROR audit
			try {
				Long entityId = null;
				Long companyId = null;
				try {
					entityId = Long.valueOf(priorityId);
				} catch (Exception ignore) {
				}
				if (bpPriority != null && bpPriority.getConfiguration() != null 
					&& bpPriority.getConfiguration().getBusinessPartner() != null
					&& bpPriority.getConfiguration().getBusinessPartner().getCompany() != null) {
					companyId = bpPriority.getConfiguration().getBusinessPartner().getCompany().getCompanyId();
				}
				auditService.recordExceptionAudit(mapper.toSystemAuditRequest("BusinessPartner", "Priority", "BPPriority",
						entityId, DELETE,
						null, null, null, userId, loadUserNameByUserId(Long.valueOf(userId)), companyId, FAILED, e.getMessage()), e);
			} catch (Exception ignore) {
			}
			log.error("Exception in deleteBusinessPartnerPriorityConfiguration method in BusinessPartnerService");
			throw new FlickzzDeskException(DEFAULT_ERROR_CODE);
		}
	}

	private void validatePrioritySlaForDeletion(BPPriority bpPriority) {
		if (bpPriority == null) {
			return;
		}
		boolean hasSla = bpSlaRepository.existsByConfigurationConfigurationIdAndPriorityPriorityIdAndIsActive(
				bpPriority.getConfiguration() != null ? bpPriority.getConfiguration().getConfigurationId() : null,
				bpPriority.getPriorityId(),
				ACTIVE);
		if (hasSla) {
			throw new FlickzzDeskException(INVALID_FIELD,
					bpPriority.getCode() + " cannot be be deleted, as it has SLA configured");
		}
	}

	public BPPriorityVO getBusinessPartnerPriorityConfigurationById(Long valueOf) {

		log.info(generateLog("getBusinessPartnerPriorityConfigurationById", this.getClass().getName()));
		try {
			Optional<BPPriority> existingPriority = bPPriorityRepository.findByPriorityIdAndIsActive(valueOf, ACTIVE);

			if (existingPriority.isEmpty()) {
				throw new FlickzzDeskException(DOES_NOT_EXIST,
						getDescription(DOES_NOT_EXIST.getDescription(), PRIORITY));
			}

			return mapper.toBPPriorityVo(existingPriority.get());
		} catch (FlickzzDeskException e) {
			throw e;
		} catch (Exception e) {
			log.error("Exception in getBusinessPartnerPriorityConfigurationById method in BusinessPartnerService");
			throw new FlickzzDeskException(DEFAULT_ERROR_CODE);
		}
	}

	public List<BPPriorityVO> getBusinessPartnerPriorityConfiguration(Long businessPartnerId) {
		log.info(generateLog("getBusinessPartnerPriorityConfiguration", this.getClass().getName()));
		try {

			Optional<BusinessPartner> businessPartner = businessPartnerRepository
					.findByBusinessPartnerIdAndIsActive(businessPartnerId, ACTIVE);
			if (!businessPartner.isPresent()) {
				throw new FlickzzDeskException(DOES_NOT_EXIST,
						getDescription(DOES_NOT_EXIST.getDescription(), BUSINESS_PARTNER));
			}

			Optional<BPConfiguration> existingConfig = bPConfigurationRepository
					.findByBusinessPartnerBusinessPartnerIdAndIsActive(businessPartnerId, ACTIVE);

			if (!existingConfig.isPresent()) {
				return Collections.emptyList();
			}

			List<BPPriority> priorities = bPPriorityRepository
					.findByConfigurationConfigurationIdAndIsActive(existingConfig.get().getConfigurationId(), ACTIVE);
			return priorities.stream().map(mapper::toBPPriorityVo).toList();
		} catch (FlickzzDeskException e) {
			throw e;
		} catch (Exception e) {
			log.error("Exception in getBusinessPartnerPriorityConfiguration method in BusinessPartnerService");
			throw new FlickzzDeskException(DEFAULT_ERROR_CODE);
		}
	}

	public BPSlaVO createBusinessPartnerSLAConfiguration(BpConfigRequestVO request) {
		log.info(generateLog("createBusinessPartnerSLAConfiguration", this.getClass().getName()));
		try {
			if (request == null || request.getBusinessPartnerId() == null) {
				throw new FlickzzDeskException(DOES_NOT_EXIST,
						getDescription(DOES_NOT_EXIST.getDescription(), BUSINESS_PARTNER));
			}

			Optional<BPPriority> existingPriority = bPPriorityRepository
					.findByPriorityIdAndIsActive(request.getPriorityId(), ACTIVE);

			if (existingPriority.isEmpty()) {
				throw new FlickzzDeskException(DOES_NOT_EXIST,
						getDescription(DOES_NOT_EXIST.getDescription(), PRIORITY));
			}

			Optional<BusinessPartner> businessPartner = businessPartnerRepository
					.findByBusinessPartnerIdAndIsActive(request.getBusinessPartnerId(), ACTIVE);
			if (!businessPartner.isPresent()) {
				throw new FlickzzDeskException(DOES_NOT_EXIST,
						getDescription(DOES_NOT_EXIST.getDescription(), BUSINESS_PARTNER));
			}

			if (bpSlaRepository.existsByConfigurationConfigurationIdAndPriorityPriorityIdAndIsActive(
					existingPriority.get().getConfiguration().getConfigurationId(),
					existingPriority.get().getPriorityId(), ACTIVE)) {
				throw new FlickzzDeskException(ALREADY_EXISTS,
						getDescription(ALREADY_EXISTS.getDescription(), "Business Partner SLA configuration"));
			}

			Optional<BPConfiguration> existingConfig = bPConfigurationRepository
					.findByBusinessPartnerBusinessPartnerIdAndIsActive(request.getBusinessPartnerId(), ACTIVE);

			BPConfiguration config = new BPConfiguration();

			if (!existingConfig.isPresent()) {
				config.setBusinessPartner(businessPartner.get());
				config.setCreatedBy(request.getCreatedBy());
				config.setIsCreatorAdmin(request.getIsCreatedByAdmin());
				bPConfigurationRepository.save(config);
			} else {
				config = existingConfig.get();
			}

			BPSla bpSla = mapper.toBPSla(request, config, existingPriority.get());
			
			// Prepare new snapshot for audit
			String newSnapshot = null;
			try {
				newSnapshot = objectMapper.writeValueAsString(buildSlaSnapshot(bpSla));
			} catch (Exception ignore) {
			}
			bpSlaRepository.save(bpSla);

			// Record CREATE audit
			java.util.Map<String, Object> changed = new java.util.HashMap<>();
			changed.put("firstResponseTime", bpSla.getFirstResponseTime());
			changed.put("firstResponseTerm", bpSla.getFirstResponseTerm());
			changed.put("resolutionTime", bpSla.getResolutionTime());
			changed.put("resolutionTerm", bpSla.getResolutionTerm());
			changed.put("updateFrequency", bpSla.getUpdateFrequency());
			changed.put("updateFrequencyTerm", bpSla.getUpdateFrequencyTerm());
			String changedStr = null;
			try { changedStr = objectMapper.writeValueAsString(changed); } catch (Exception ignore) {}

			auditService.recordAudit(mapper.toSystemAuditRequest("BusinessPartner", "SLA", "BPSla",
					bpSla.getSlaId(),
					CREATE,
					newSnapshot,
					null,
					changedStr,
					request.getCreatedBy(),
					loadUserNameByUserId(Long.valueOf(request.getCreatedBy())),
					request.getOrgId(),
					SUCCESS,
					null));

			return mapper.toBPSlaVo(bpSla);
		} catch (FlickzzDeskException e) {
			// record FlickzzDeskException as ERROR audit and rethrow
			String attempted = null;
			try {
				java.util.Map<String, Object> attemptedMap = new java.util.HashMap<>();
				attemptedMap.put("firstResponseTime", request != null ? request.getFirstResponseTime() : null);
				attemptedMap.put("firstResponseTerm", request != null ? request.getFirstResponseTerm() : null);
				attemptedMap.put("resolutionTime", request != null ? request.getResolutionTime() : null);
				attemptedMap.put("resolutionTerm", request != null ? request.getResolutionTerm() : null);
				attemptedMap.put("updateFrequency", request != null ? request.getUpdateFrequency() : null);
				attemptedMap.put("updateFrequencyTerm", request != null ? request.getUpdateFrequencyTerm() : null);
				attempted = objectMapper.writeValueAsString(attemptedMap);
			} catch (Exception ignore) {
			}
			auditService.recordExceptionAudit(mapper.toSystemAuditRequest("BusinessPartner", "SLA", "BPSla", null, CREATE,
					attempted, null, null, request != null ? request.getCreatedBy() : null, loadUserNameByUserId(Long.valueOf(request.getCreatedBy())), request != null ? request.getOrgId() : null, FAILED, e.getDescription()), e);
				throw e;
		} catch (Exception e) {
			String attempted = null;
			try {
				java.util.Map<String, Object> attemptedMap = new java.util.HashMap<>();
				attemptedMap.put("firstResponseTime", request != null ? request.getFirstResponseTime() : null);
				attemptedMap.put("firstResponseTerm", request != null ? request.getFirstResponseTerm() : null);
				attemptedMap.put("resolutionTime", request != null ? request.getResolutionTime() : null);
				attemptedMap.put("resolutionTerm", request != null ? request.getResolutionTerm() : null);
				attemptedMap.put("updateFrequency", request != null ? request.getUpdateFrequency() : null);
				attemptedMap.put("updateFrequencyTerm", request != null ? request.getUpdateFrequencyTerm() : null);
				attempted = objectMapper.writeValueAsString(attemptedMap);
			} catch (Exception ignore) {
			}
			auditService.recordExceptionAudit(mapper.toSystemAuditRequest("BusinessPartner", "SLA", "BPSla",
					null,
					CREATE,
					attempted,
					null,
					null,
					request != null ? request.getCreatedBy() : null,
					loadUserNameByUserId(Long.valueOf(request.getCreatedBy())),
					request != null ? request.getOrgId() : null,
					FAILED,
					e.getMessage()), e);
			log.error("Exception in createBusinessPartnerSLAConfiguration method in BusinessPartnerService", e);
			throw new FlickzzDeskException(DEFAULT_ERROR_CODE);
		}
	}

	public List<BPSlaVO> getBusinessPartnerSLAConfiguration(Long valueOf) {
		log.info(generateLog("getBusinessPartnerSLAConfiguration", this.getClass().getName()));
		try {

			Optional<BusinessPartner> businessPartner = businessPartnerRepository
					.findByBusinessPartnerIdAndIsActive(valueOf, ACTIVE);
			if (!businessPartner.isPresent()) {
				throw new FlickzzDeskException(DOES_NOT_EXIST,
						getDescription(DOES_NOT_EXIST.getDescription(), BUSINESS_PARTNER));
			}

			Optional<BPConfiguration> existingConfig = bPConfigurationRepository
					.findByBusinessPartnerBusinessPartnerIdAndIsActive(valueOf, ACTIVE);

			if (!existingConfig.isPresent()) {
				return Collections.emptyList();
			}

			List<BPSla> slas = bpSlaRepository
					.findByConfigurationConfigurationIdAndIsActive(existingConfig.get().getConfigurationId(), ACTIVE);
			return slas.stream().map(mapper::toBPSlaVo).toList();
		} catch (FlickzzDeskException e) {
			throw e;
		} catch (Exception e) {
			log.error("Exception in getBusinessPartnerSLAConfiguration method in BusinessPartnerService");
			throw new FlickzzDeskException(DEFAULT_ERROR_CODE);
		}
	}

	public BPSlaVO updateBusinessPartnerSLAConfiguration(BpConfigRequestVO request) {

		log.info(generateLog("updateBusinessPartnerSLAConfiguration", this.getClass().getName()));
		BPSla bpSla = null;
		try {
			if (request == null || request.getSlaId() == null) {
				throw new FlickzzDeskException(DOES_NOT_EXIST, getDescription(DOES_NOT_EXIST.getDescription(), SLA));
			}

			Optional<BPSla> existingSla = bpSlaRepository.findBySlaIdAndIsActive(request.getSlaId(), ACTIVE);

			if (existingSla.isEmpty()) {
				throw new FlickzzDeskException(DOES_NOT_EXIST, getDescription(DOES_NOT_EXIST.getDescription(), SLA));
			}

			bpSla = existingSla.get();
			
			// Capture old values before update
			String oldValue = null;
			try {
				oldValue = objectMapper.writeValueAsString(buildSlaSnapshot(bpSla));
			} catch (Exception ignore) {
			}
			
			// Track changed fields
			Map<String, Object> changedFields = new HashMap<>();
			if (!Objects.equals(bpSla.getFirstResponseTime(), request.getFirstResponseTime())) {
				changedFields.put("firstResponseTime", true);
			}
			if (!Objects.equals(bpSla.getFirstResponseTerm(), request.getFirstResponseTerm())) {
				changedFields.put("firstResponseTerm", true);
			}
			if (!Objects.equals(bpSla.getResolutionTime(), request.getResolutionTime())) {
				changedFields.put("resolutionTime", true);
			}
			if (!Objects.equals(bpSla.getResolutionTerm(), request.getResolutionTerm())) {
				changedFields.put("resolutionTerm", true);
			}
			if (!Objects.equals(bpSla.getUpdateFrequency(), request.getUpdateFrequency())) {
				changedFields.put("updateFrequency", true);
			}
			if (!Objects.equals(bpSla.getUpdateFrequencyTerm(), request.getUpdateFrequencyTerm())) {
				changedFields.put("updateFrequencyTerm", true);
			}
			if (!Objects.equals(bpSla.getUpdatedBy(), request.getUpdatedBy())) {
				changedFields.put("updatedBy", true);
			}
			
			// Update the SLA
			bpSla.setFirstResponseTime(request.getFirstResponseTime());
			bpSla.setFirstResponseTerm(request.getFirstResponseTerm());
			bpSla.setResolutionTime(request.getResolutionTime());
			bpSla.setResolutionTerm(request.getResolutionTerm());
			bpSla.setUpdateFrequency(request.getUpdateFrequency());
			bpSla.setUpdateFrequencyTerm(request.getUpdateFrequencyTerm());
			bpSla.setUpdatedBy(request.getUpdatedBy());
			bpSlaRepository.save(bpSla);
			
			// Record UPDATE audit if there are changes
			if (!changedFields.isEmpty()) {
				String newSnapshot = null;
				try {
					newSnapshot = objectMapper.writeValueAsString(buildSlaSnapshot(bpSla));
				} catch (Exception ignore) {
				}
				String changedStr = null;
				try {
					changedStr = objectMapper.writeValueAsString(changedFields);
				} catch (Exception ignore) {
				}
				Long companyId = null;
				try {
					if (bpSla.getConfiguration() != null && bpSla.getConfiguration().getBusinessPartner() != null
						&& bpSla.getConfiguration().getBusinessPartner().getCompany() != null) {
						companyId = bpSla.getConfiguration().getBusinessPartner().getCompany().getCompanyId();
					}
				} catch (Exception ignore) {
				}
				auditService.recordAudit(mapper.toSystemAuditRequest("BusinessPartner", "SLA", "BPSla",
						bpSla.getSlaId(),
						UPDATE,
						newSnapshot,
						oldValue,
						changedStr,
						request.getUpdatedBy(),
						loadUserNameByUserId(Long.valueOf(request.getUpdatedBy())),
						companyId,
						SUCCESS,
						null));
			}
			
			return mapper.toBPSlaVo(bpSla);
		} catch (FlickzzDeskException e) {
			// Attempt to save error audit
			try {
				Long entityId = null;
				Long companyId = null;
				String oldSnap = null;
				if (bpSla != null) {
					entityId = bpSla.getSlaId();
					try {
						oldSnap = objectMapper.writeValueAsString(buildSlaSnapshot(bpSla));
					} catch (Exception ignore) {
					}
					try {
						if (bpSla.getConfiguration() != null && bpSla.getConfiguration().getBusinessPartner() != null
							&& bpSla.getConfiguration().getBusinessPartner().getCompany() != null) {
							companyId = bpSla.getConfiguration().getBusinessPartner().getCompany().getCompanyId();
						}
					} catch (Exception ignore) {
					}
				}
				String newSnap = null;
				if (request != null) {
					try {
						Map<String, Object> attempted = new HashMap<>();
						attempted.put("firstResponseTime", request.getFirstResponseTime());
						attempted.put("firstResponseTerm", request.getFirstResponseTerm());
						attempted.put("resolutionTime", request.getResolutionTime());
						attempted.put("resolutionTerm", request.getResolutionTerm());
						attempted.put("updateFrequency", request.getUpdateFrequency());
						attempted.put("updateFrequencyTerm", request.getUpdateFrequencyTerm());
						newSnap = objectMapper.writeValueAsString(attempted);
					} catch (Exception ignore) {
					}
				}
				auditService.recordExceptionAudit(mapper.toSystemAuditRequest("BusinessPartner", "SLA", "BPSla",
						entityId, UPDATE, newSnap, oldSnap, null, 
						request != null ? request.getUpdatedBy() : null, loadUserNameByUserId(Long.valueOf(request.getUpdatedBy())), companyId, FAILED, e.getDescription()), e);
			} catch (Exception ignore) {
			}
			throw e;
		} catch (Exception e) {
			// Attempt to save error audit
			try {
				Long entityId = null;
				Long companyId = null;
				String oldSnap = null;
				if (bpSla != null) {
					entityId = bpSla.getSlaId();
					try {
						oldSnap = objectMapper.writeValueAsString(buildSlaSnapshot(bpSla));
					} catch (Exception ignore) {
					}
					try {
						if (bpSla.getConfiguration() != null && bpSla.getConfiguration().getBusinessPartner() != null
							&& bpSla.getConfiguration().getBusinessPartner().getCompany() != null) {
							companyId = bpSla.getConfiguration().getBusinessPartner().getCompany().getCompanyId();
						}
					} catch (Exception ignore) {
					}
				}
				String newSnap = null;
				if (request != null) {
					try {
						Map<String, Object> attempted = new HashMap<>();
						attempted.put("firstResponseTime", request.getFirstResponseTime());
						attempted.put("firstResponseTerm", request.getFirstResponseTerm());
						attempted.put("resolutionTime", request.getResolutionTime());
						attempted.put("resolutionTerm", request.getResolutionTerm());
						attempted.put("updateFrequency", request.getUpdateFrequency());
						attempted.put("updateFrequencyTerm", request.getUpdateFrequencyTerm());
						newSnap = objectMapper.writeValueAsString(attempted);
					} catch (Exception ignore) {
					}
				}
				auditService.recordExceptionAudit(mapper.toSystemAuditRequest("BusinessPartner", "SLA", "BPSla",
						entityId, UPDATE, newSnap, oldSnap, null,
						request != null ? request.getUpdatedBy() : null, loadUserNameByUserId(Long.valueOf(request.getUpdatedBy())), companyId, FAILED, e.getMessage()), e);
			} catch (Exception ignore) {
			}
			log.error("Exception in updateBusinessPartnerSLAConfiguration method in BusinessPartnerService", e);
			throw new FlickzzDeskException(DEFAULT_ERROR_CODE);
		}
	}

	public void deleteBusinessPartnerSLAConfiguration(String slaId, Long userId) {

		log.info(generateLog("deleteBusinessPartnerSLAConfiguration", this.getClass().getName()));
		BPSla bpSla = null;
		try {
			Optional<BPSla> existingSla = bpSlaRepository.findBySlaIdAndIsActive(Long.valueOf(slaId), ACTIVE);

			if (existingSla.isEmpty()) {
				throw new FlickzzDeskException(DOES_NOT_EXIST, getDescription(DOES_NOT_EXIST.getDescription(), SLA));
			}

			bpSla = existingSla.get();
			
			// Capture old snapshot before deletion
			String oldValue = null;
			try {
				oldValue = objectMapper.writeValueAsString(buildSlaSnapshot(bpSla));
			} catch (Exception ignore) {
			}

			bpSlaRepository.delete(bpSla);
			
			// Record DELETE audit
			Long companyId = null;
			try {
				if (bpSla.getConfiguration() != null && bpSla.getConfiguration().getBusinessPartner() != null
					&& bpSla.getConfiguration().getBusinessPartner().getCompany() != null) {
					companyId = bpSla.getConfiguration().getBusinessPartner().getCompany().getCompanyId();
				}
			} catch (Exception ignore) {
			}
			auditService.recordAudit(mapper.toSystemAuditRequest("BusinessPartner", "SLA", "BPSla",
					bpSla.getSlaId(),
					DELETE,
					oldValue,
					null,
					null,
					userId,
					loadUserNameByUserId(Long.valueOf(userId)),
					companyId,
					SUCCESS,
					null));
		} catch (FlickzzDeskException e) {
			// record FlickzzDeskException as ERROR audit and rethrow
			try {
				Long entityId = null;
				Long companyId = null;
				try {
					entityId = Long.valueOf(slaId);
				} catch (Exception ignore) {
				}
				if (bpSla != null && bpSla.getConfiguration() != null 
					&& bpSla.getConfiguration().getBusinessPartner() != null
					&& bpSla.getConfiguration().getBusinessPartner().getCompany() != null) {
					companyId = bpSla.getConfiguration().getBusinessPartner().getCompany().getCompanyId();
				}
				auditService.recordExceptionAudit(mapper.toSystemAuditRequest("BusinessPartner", "SLA", "BPSla",
						entityId, DELETE,
						null, null, null, userId, loadUserNameByUserId(Long.valueOf(userId)), companyId, FAILED, e.getDescription()), e);
			} catch (Exception ignore) {
			}
			throw e;
		} catch (Exception e) {
			// record generic exception as ERROR audit
			try {
				Long entityId = null;
				Long companyId = null;
				try {
					entityId = Long.valueOf(slaId);
				} catch (Exception ignore) {
				}
				if (bpSla != null && bpSla.getConfiguration() != null 
					&& bpSla.getConfiguration().getBusinessPartner() != null
					&& bpSla.getConfiguration().getBusinessPartner().getCompany() != null) {
					companyId = bpSla.getConfiguration().getBusinessPartner().getCompany().getCompanyId();
				}
				auditService.recordExceptionAudit(mapper.toSystemAuditRequest("BusinessPartner", "SLA", "BPSla",
						entityId, DELETE,
						null, null, null, userId, loadUserNameByUserId(Long.valueOf(userId)), companyId, FAILED, e.getMessage()), e);
			} catch (Exception ignore) {
			}
			log.error("Exception in deleteBusinessPartnerSLAConfiguration method in BusinessPartnerService");
			throw new FlickzzDeskException(DEFAULT_ERROR_CODE);
		}
	}

	public BPSlaVO getBusinessPartnerSLAConfigurationById(Long valueOf) {

		log.info(generateLog("getBusinessPartnerSLAConfigurationById", this.getClass().getName()));
		try {
			Optional<BPSla> existingSla = bpSlaRepository.findBySlaIdAndIsActive(valueOf, ACTIVE);

			if (existingSla.isEmpty()) {
				throw new FlickzzDeskException(DOES_NOT_EXIST, getDescription(DOES_NOT_EXIST.getDescription(), SLA));
			}

			return mapper.toBPSlaVo(existingSla.get());
		} catch (FlickzzDeskException e) {
			throw e;
		} catch (Exception e) {
			log.error("Exception in getBusinessPartnerSLAConfigurationById method in BusinessPartnerService");
			throw new FlickzzDeskException(DEFAULT_ERROR_CODE);
		}
	}

	public BPCategoryVO createBusinessPartnerCategoryConfiguration(BpConfigRequestVO request) {

		log.info(generateLog("createBusinessPartnerCategoryConfiguration", this.getClass().getName()));
		try {
			if (request == null || request.getBusinessPartnerId() == null) {
				throw new FlickzzDeskException(DOES_NOT_EXIST,
						getDescription(DOES_NOT_EXIST.getDescription(), BUSINESS_PARTNER));
			}

			Optional<BusinessPartner> businessPartner = businessPartnerRepository
					.findByBusinessPartnerIdAndIsActive(request.getBusinessPartnerId(), ACTIVE);
			if (!businessPartner.isPresent()) {
				throw new FlickzzDeskException(DOES_NOT_EXIST,
						getDescription(DOES_NOT_EXIST.getDescription(), BUSINESS_PARTNER));
			}

			Optional<BPConfiguration> existingConfig = bPConfigurationRepository
					.findByBusinessPartnerBusinessPartnerIdAndIsActive(request.getBusinessPartnerId(), ACTIVE);

			BPConfiguration config = new BPConfiguration();

			if (!existingConfig.isPresent()) {
				config.setBusinessPartner(businessPartner.get());
				config.setCreatedBy(request.getCreatedBy());
				config.setIsCreatorAdmin(request.getIsCreatedByAdmin());
				bPConfigurationRepository.save(config);
			} else {
				config = existingConfig.get();
			}

			if (bpCategoryRepository.existsByConfigurationConfigurationIdAndCategoryNameAndIsActive(
					config.getConfigurationId(), request.getCategoryName(), ACTIVE)) {
				throw new FlickzzDeskException(ALREADY_EXISTS,
						getDescription(ALREADY_EXISTS.getDescription(), CATEGORY));
			}

			BPCategory bpCategory = mapper.toBPCategory(request, config);
			
			// Prepare new snapshot for audit
			String newSnapshot = null;
			try {
				newSnapshot = objectMapper.writeValueAsString(buildCategorySnapshot(bpCategory));
			} catch (Exception ignore) {
			}
			bpCategoryRepository.save(bpCategory);

			if (request.getSubCategories() != null && !request.getSubCategories().isEmpty()) {
				for (String subCategoryName : request.getSubCategories()) {
					if (subCategoryName == null || subCategoryName.trim().isEmpty()) {
						continue;
					}
					upsertSubCategory(bpCategory, subCategoryName.trim(), request.getCreatedBy(), request.getUpdatedBy());
				}
			}

			// Record CREATE audit
			java.util.Map<String, Object> changed = new java.util.HashMap<>();
			changed.put("categoryName", bpCategory.getCategoryName());
			String changedStr = null;
			try { changedStr = objectMapper.writeValueAsString(changed); } catch (Exception ignore) {}

			auditService.recordAudit(mapper.toSystemAuditRequest("BusinessPartner", "Category", "BPCategory",
					bpCategory.getCategoryId(),
					CREATE,
					newSnapshot,
					null,
					changedStr,
					request.getCreatedBy(),
					loadUserNameByUserId(Long.valueOf(request.getCreatedBy())),
					request.getOrgId(),
					SUCCESS,
					null));

			return mapper.toBPCategoryVo(bpCategory);
		} catch (FlickzzDeskException e) {
			// record FlickzzDeskException as ERROR audit and rethrow
			String attempted = null;
			try {
				java.util.Map<String, Object> attemptedMap = new java.util.HashMap<>();
				attemptedMap.put("categoryName", request != null ? request.getCategoryName() : null);
				attempted = objectMapper.writeValueAsString(attemptedMap);
			} catch (Exception ignore) {
			}
			auditService.recordExceptionAudit(mapper.toSystemAuditRequest("BusinessPartner", "Category", "BPCategory", null, CREATE,
					attempted, null, null, request != null ? request.getCreatedBy() : null, loadUserNameByUserId(Long.valueOf(request.getCreatedBy())), request != null ? request.getOrgId() : null, FAILED, e.getDescription()), e);
				throw e;
		} catch (Exception e) {
			String attempted = null;
			try {
				java.util.Map<String, Object> attemptedMap = new java.util.HashMap<>();
				attemptedMap.put("categoryName", request != null ? request.getCategoryName() : null);
				attempted = objectMapper.writeValueAsString(attemptedMap);
			} catch (Exception ignore) {
			}
			auditService.recordExceptionAudit(mapper.toSystemAuditRequest("BusinessPartner", "Category", "BPCategory",
					null,
					CREATE,
					attempted,
					null,
					null,
					request != null ? request.getCreatedBy() : null,
					loadUserNameByUserId(Long.valueOf(request.getCreatedBy())),
					request != null ? request.getOrgId() : null,
					FAILED,
					e.getMessage()), e);
			log.error("Exception in createBusinessPartnerCategoryConfiguration method in BusinessPartnerService");
			throw new FlickzzDeskException(DEFAULT_ERROR_CODE);
		}
	}

	public BPCategoryVO updateBusinessPartnerCategoryConfiguration(BpConfigRequestVO request) {
		log.info(generateLog("updateBusinessPartnerCategoryConfiguration", this.getClass().getName()));
		BPCategory bpCategory = null;
		try {
			if (request == null || request.getCategoryId() == null) {
				throw new FlickzzDeskException(DOES_NOT_EXIST,
						getDescription(DOES_NOT_EXIST.getDescription(), CATEGORY));
			}

			Optional<BPCategory> existingCategory = bpCategoryRepository
					.findByCategoryIdAndIsActive(request.getCategoryId(), ACTIVE);
			if (existingCategory.isEmpty()) {
				throw new FlickzzDeskException(DOES_NOT_EXIST,
						getDescription(DOES_NOT_EXIST.getDescription(), CATEGORY));
			}

			bpCategory = existingCategory.get();
			
			// Capture old values before update
			String oldValue = null;
			try {
				oldValue = objectMapper.writeValueAsString(buildCategorySnapshot(bpCategory));
			} catch (Exception ignore) {
			}
			
			// Track changed fields
			Map<String, Object> changedFields = new HashMap<>();
			if (!bpCategory.getCategoryName().equals(request.getCategoryName())) {
				changedFields.put("categoryName", true);
			}
			if (!Objects.equals(bpCategory.getSubCategories().stream().map(BPSubCategory::getSubCategoryName).toList(), request.getSubCategories())) {
				changedFields.put("subCategories", true);
			}
			if (!Objects.equals(bpCategory.getUpdatedBy(), request.getUpdatedBy())) {
				changedFields.put("updatedBy", true);
			}
			
			// Update the category
			bpCategory.setCategoryName(request.getCategoryName());
			bpCategory.setUpdatedBy(request.getUpdatedBy());
			bpCategoryRepository.save(bpCategory);

			if (request.getSubCategories() != null) {
				Set<String> incomingSubCategories = new LinkedHashSet<>();
				for (String subCategoryName : request.getSubCategories()) {
					if (subCategoryName == null || subCategoryName.trim().isEmpty()) {
						continue;
					}
					incomingSubCategories.add(subCategoryName.trim());
				}

				validateSubCategoryAssignmentsForRemoval(bpCategory, incomingSubCategories);

				List<BPSubCategory> existingSubCategories = bpSubCategoryRepository
						.findByCategoryCategoryIdAndIsActive(bpCategory.getCategoryId(), ACTIVE);
				Set<String> existingNames = existingSubCategories.stream().map(BPSubCategory::getSubCategoryName)
						.collect(Collectors.toSet());

				for (String subCategoryName : incomingSubCategories) {
					if (existingNames.contains(subCategoryName)) {
						continue;
					}
					upsertSubCategory(bpCategory, subCategoryName, request.getCreatedBy(), request.getUpdatedBy());
				}

				for (BPSubCategory subCategory : existingSubCategories) {
					if (!incomingSubCategories.contains(subCategory.getSubCategoryName())) {
						bpSubCategoryRepository.delete(subCategory);
					}
				}
			}
			
			// Record UPDATE audit if there are changes
			if (!changedFields.isEmpty()) {
				String newSnapshot = null;
				try {
					newSnapshot = objectMapper.writeValueAsString(buildCategorySnapshot(bpCategory));
				} catch (Exception ignore) {
				}
				String changedStr = null;
				try {
					changedStr = objectMapper.writeValueAsString(changedFields);
				} catch (Exception ignore) {
				}
				Long companyId = null;
				try {
					if (bpCategory.getConfiguration() != null && bpCategory.getConfiguration().getBusinessPartner() != null
						&& bpCategory.getConfiguration().getBusinessPartner().getCompany() != null) {
						companyId = bpCategory.getConfiguration().getBusinessPartner().getCompany().getCompanyId();
					}
				} catch (Exception ignore) {
				}
				auditService.recordAudit(mapper.toSystemAuditRequest("BusinessPartner", "Category", "BPCategory",
						bpCategory.getCategoryId(),
						UPDATE,
						newSnapshot,
						oldValue,
						changedStr,
						request.getUpdatedBy(),
						loadUserNameByUserId(Long.valueOf(request.getUpdatedBy())),
						companyId,
						SUCCESS,
						null));
			}
			
			return mapper.toBPCategoryVo(bpCategory);
		} catch (FlickzzDeskException e) {
			// Attempt to save error audit
			try {
				Long entityId = null;
				Long companyId = null;
				String oldSnap = null;
				if (bpCategory != null) {
					entityId = bpCategory.getCategoryId();
					try {
						oldSnap = objectMapper.writeValueAsString(buildCategorySnapshot(bpCategory));
					} catch (Exception ignore) {
					}
					try {
						if (bpCategory.getConfiguration() != null && bpCategory.getConfiguration().getBusinessPartner() != null
							&& bpCategory.getConfiguration().getBusinessPartner().getCompany() != null) {
							companyId = bpCategory.getConfiguration().getBusinessPartner().getCompany().getCompanyId();
						}
					} catch (Exception ignore) {
					}
				}
				String newSnap = null;
				if (request != null) {
					try {
						Map<String, Object> attempted = new HashMap<>();
						attempted.put("categoryName", request.getCategoryName());
						attempted.put("updatedBy", request.getUpdatedBy());
						newSnap = objectMapper.writeValueAsString(attempted);
					} catch (Exception ignore) {
					}
				}
				auditService.recordExceptionAudit(mapper.toSystemAuditRequest("BusinessPartner", "Category", "BPCategory",
						entityId, UPDATE, newSnap, oldSnap, null, 
						request != null ? request.getUpdatedBy() : null,
						loadUserNameByUserId(Long.valueOf(request.getUpdatedBy())), companyId, FAILED, e.getDescription()), e);
			} catch (Exception ignore) {
			}
			throw e;
		} catch (Exception e) {
			// Attempt to save error audit
			try {
				Long entityId = null;
				Long companyId = null;
				String oldSnap = null;
				if (bpCategory != null) {
					entityId = bpCategory.getCategoryId();
					try {
						oldSnap = objectMapper.writeValueAsString(buildCategorySnapshot(bpCategory));
					} catch (Exception ignore) {
					}
					try {
						if (bpCategory.getConfiguration() != null && bpCategory.getConfiguration().getBusinessPartner() != null
							&& bpCategory.getConfiguration().getBusinessPartner().getCompany() != null) {
							companyId = bpCategory.getConfiguration().getBusinessPartner().getCompany().getCompanyId();
						}
					} catch (Exception ignore) {
					}
				}
				String newSnap = null;
				if (request != null) {
					try {
						Map<String, Object> attempted = new HashMap<>();
						attempted.put("categoryName", request.getCategoryName());
						attempted.put("updatedBy", request.getUpdatedBy());
						newSnap = objectMapper.writeValueAsString(attempted);
					} catch (Exception ignore) {
					}
				}
				auditService.recordExceptionAudit(mapper.toSystemAuditRequest("BusinessPartner", "Category", "BPCategory",
						entityId, UPDATE, newSnap, oldSnap, null,
						request != null ? request.getUpdatedBy() : null, loadUserNameByUserId(Long.valueOf(request.getUpdatedBy())), companyId, FAILED, e.getMessage()), e);
			} catch (Exception ignore) {
			}
			log.error("Exception in updateBusinessPartnerCategoryConfiguration method in BusinessPartnerService", e);
			throw new FlickzzDeskException(DEFAULT_ERROR_CODE);
		}
	}

	public void deleteBusinessPartnerCategoryConfiguration(String categoryId, Long userId) {
		log.info(generateLog("deleteBusinessPartnerCategoryConfiguration", this.getClass().getName()));
		BPCategory bpCategory = null;
		try {
			Optional<BPCategory> existingCategory = bpCategoryRepository
					.findByCategoryIdAndIsActive(Long.valueOf(categoryId), ACTIVE);
			if (existingCategory.isEmpty()) {
				throw new FlickzzDeskException(DOES_NOT_EXIST,
						getDescription(DOES_NOT_EXIST.getDescription(), CATEGORY));
			}

			bpCategory = existingCategory.get();
			
			// Capture old snapshot before deletion
			String oldValue = null;
			try {
				oldValue = objectMapper.writeValueAsString(buildCategorySnapshot(bpCategory));
			} catch (Exception ignore) {
			}

			validateSubCategoryAssignmentsForDeletion(bpCategory);

			bpCategoryRepository.delete(bpCategory);
			List<BPSubCategory> existingSubCategories = bpSubCategoryRepository
					.findByCategoryCategoryIdAndIsActive(bpCategory.getCategoryId(), ACTIVE);
			for (BPSubCategory subCategory : existingSubCategories) {
				subCategory.setIsActive(Boolean.FALSE);
				subCategory.setUpdatedBy(userId);
				bpSubCategoryRepository.save(subCategory);
			}
			
			// Record DELETE audit
			Long companyId = null;
			try {
				if (bpCategory.getConfiguration() != null && bpCategory.getConfiguration().getBusinessPartner() != null
					&& bpCategory.getConfiguration().getBusinessPartner().getCompany() != null) {
					companyId = bpCategory.getConfiguration().getBusinessPartner().getCompany().getCompanyId();
				}
			} catch (Exception ignore) {
			}
			auditService.recordAudit(mapper.toSystemAuditRequest("BusinessPartner", "Category", "BPCategory",
					bpCategory.getCategoryId(),
					DELETE,
					oldValue,
					null,
					null,
					userId,
					loadUserNameByUserId(Long.valueOf(userId)),
					companyId,
					SUCCESS,
					null));
		} catch (FlickzzDeskException e) {
			// record FlickzzDeskException as ERROR audit and rethrow
			try {
				Long entityId = null;
				Long companyId = null;
				try {
					entityId = Long.valueOf(categoryId);
				} catch (Exception ignore) {
				}
				if (bpCategory != null && bpCategory.getConfiguration() != null 
					&& bpCategory.getConfiguration().getBusinessPartner() != null
					&& bpCategory.getConfiguration().getBusinessPartner().getCompany() != null) {
					companyId = bpCategory.getConfiguration().getBusinessPartner().getCompany().getCompanyId();
				}
				auditService.recordExceptionAudit(mapper.toSystemAuditRequest("BusinessPartner", "Category", "BPCategory",
						entityId, DELETE,
						null, null, null, userId, loadUserNameByUserId(Long.valueOf(userId)), companyId, FAILED, e.getDescription()), e);
			} catch (Exception ignore) {
			}
			throw e;
		} catch (Exception e) {
			// record generic exception as ERROR audit
			try {
				Long entityId = null;
				Long companyId = null;
				try {
					entityId = Long.valueOf(categoryId);
				} catch (Exception ignore) {
				}
				if (bpCategory != null && bpCategory.getConfiguration() != null 
					&& bpCategory.getConfiguration().getBusinessPartner() != null
					&& bpCategory.getConfiguration().getBusinessPartner().getCompany() != null) {
					companyId = bpCategory.getConfiguration().getBusinessPartner().getCompany().getCompanyId();
				}
				auditService.recordExceptionAudit(mapper.toSystemAuditRequest("BusinessPartner", "Category", "BPCategory",
						entityId, DELETE,
						null, null, null, userId, loadUserNameByUserId(Long.valueOf(userId)), companyId, FAILED, e.getMessage()), e);
			} catch (Exception ignore) {
			}
			log.error("Exception in deleteBusinessPartnerCategoryConfiguration method in BusinessPartnerService");
			throw new FlickzzDeskException(DEFAULT_ERROR_CODE);
		}
	}

	BPSubCategory upsertSubCategory(BPCategory bpCategory, String subCategoryName, Long createdBy, Long updatedBy) {
		if (bpCategory == null || subCategoryName == null || subCategoryName.trim().isEmpty()) {
			return null;
		}

		String normalizedName = subCategoryName.trim();
		Optional<BPSubCategory> existingSubCategory = bpSubCategoryRepository
				.findByCategoryCategoryIdAndSubCategoryName(bpCategory.getCategoryId(), normalizedName);
		if (existingSubCategory.isPresent()) {
			BPSubCategory subCategory = existingSubCategory.get();
			if (!Boolean.TRUE.equals(subCategory.getIsActive())) {
				subCategory.setIsActive(Boolean.TRUE);
				subCategory.setUpdatedBy(updatedBy);
				if (subCategory.getCreatedBy() == null && createdBy != null) {
					subCategory.setCreatedBy(createdBy);
				}
				return bpSubCategoryRepository.save(subCategory);
			}
			return subCategory;
		}

		BPSubCategory subCategory = BPSubCategory.builder().category(bpCategory).subCategoryName(normalizedName)
				.createdBy(createdBy).updatedBy(updatedBy).build();
		return bpSubCategoryRepository.save(subCategory);
	}

	void validateSubCategoryAssignmentsForRemoval(BPCategory bpCategory, Set<String> incomingSubCategories) {
		if (bpCategory == null || bpCategory.getConfiguration() == null || incomingSubCategories == null) {
			return;
		}

		List<BPSubCategory> existingSubCategories = bpSubCategoryRepository
				.findByCategoryCategoryIdAndIsActive(bpCategory.getCategoryId(), ACTIVE);
		for (BPSubCategory subCategory : existingSubCategories) {
			if (incomingSubCategories.contains(subCategory.getSubCategoryName())) {
				continue;
			}
			validateSubCategoryAssignment(subCategory, bpCategory.getConfiguration().getConfigurationId());
		}
	}

	void validateSubCategoryAssignmentsForDeletion(BPCategory bpCategory) {
		if (bpCategory == null || bpCategory.getConfiguration() == null) {
			return;
		}

		List<BPSubCategory> existingSubCategories = bpSubCategoryRepository
				.findByCategoryCategoryIdAndIsActive(bpCategory.getCategoryId(), ACTIVE);
		for (BPSubCategory subCategory : existingSubCategories) {
			validateSubCategoryAssignment(subCategory, bpCategory.getConfiguration().getConfigurationId());
		}
	}

	private void validateSubCategoryAssignment(BPSubCategory subCategory, Long configurationId) {
		if (subCategory == null) {
			return;
		}
		boolean hasAssignment = bpAssignmentRepository
				.findByConfigurationConfigurationIdAndSubCategorySubCategoryIdAndIsActive(
						configurationId, subCategory.getSubCategoryId(), ACTIVE)
				.isPresent();
		if (hasAssignment) {
			throw new FlickzzDeskException(INVALID_FIELD,
					"SubCategory " + subCategory.getSubCategoryName() + " cannot be removed as it has existing assignment");
		}
	}

	public BPCategoryVO getBusinessPartnerCategoryConfigurationById(Long categoryId) {
		log.info(generateLog("getBusinessPartnerCategoryConfigurationById", this.getClass().getName()));
		try {
			Optional<BPCategory> existingCategory = bpCategoryRepository.findByCategoryIdAndIsActive(categoryId,
					ACTIVE);
			if (existingCategory.isEmpty()) {
				throw new FlickzzDeskException(DOES_NOT_EXIST,
						getDescription(DOES_NOT_EXIST.getDescription(), CATEGORY));
			}
			return mapper.toBPCategoryVo(existingCategory.get());
		} catch (FlickzzDeskException e) {
			throw e;
		} catch (Exception e) {
			log.error("Exception in getBusinessPartnerCategoryConfigurationById method in BusinessPartnerService");
			throw new FlickzzDeskException(DEFAULT_ERROR_CODE);
		}
	}

	public List<BPCategoryVO> getBusinessPartnerCategoryConfiguration(Long businessPartnerId) {
		log.info(generateLog("getBusinessPartnerCategoryConfiguration", this.getClass().getName()));
		try {
			Optional<BusinessPartner> businessPartner = businessPartnerRepository
					.findByBusinessPartnerIdAndIsActive(businessPartnerId, ACTIVE);
			if (!businessPartner.isPresent()) {
				throw new FlickzzDeskException(DOES_NOT_EXIST,
						getDescription(DOES_NOT_EXIST.getDescription(), BUSINESS_PARTNER));
			}

			Optional<BPConfiguration> existingConfig = bPConfigurationRepository
					.findByBusinessPartnerBusinessPartnerIdAndIsActive(businessPartnerId, ACTIVE);
			if (!existingConfig.isPresent()) {
				return Collections.emptyList();
			}

			List<BPCategory> categories = bpCategoryRepository
					.findByConfigurationConfigurationIdAndIsActive(existingConfig.get().getConfigurationId(), ACTIVE);
			return categories.stream().map(mapper::toBPCategoryVo).toList();
		} catch (FlickzzDeskException e) {
			throw e;
		} catch (Exception e) {
			log.error("Exception in getBusinessPartnerCategoryConfiguration method in BusinessPartnerService");
			throw new FlickzzDeskException(DEFAULT_ERROR_CODE);
		}
	}

	public BPSupportGroupVO createBusinessPartnerSupportGroupConfiguration(BpConfigRequestVO request) {
		log.info(generateLog("createBusinessPartnerSupportGroupConfiguration", this.getClass().getName()));
		try {
			if (request == null || request.getBusinessPartnerId() == null) {
				throw new FlickzzDeskException(DOES_NOT_EXIST,
						getDescription(DOES_NOT_EXIST.getDescription(), BUSINESS_PARTNER));
			}
			if (request.getGroupName() == null || request.getGroupName().trim().isEmpty()) {
				throw new FlickzzDeskException(INVALID_FIELD,
						getDescription(INVALID_FIELD.getDescription(), "Group name"));
			}
			if (request.getAgents() == null || request.getAgents().isEmpty()) {
				throw new FlickzzDeskException(SET_TEXT, getDescription(SET_TEXT.getDescription(), "agent list"));
			}

			Optional<BusinessPartner> businessPartner = businessPartnerRepository
					.findByBusinessPartnerIdAndIsActive(request.getBusinessPartnerId(), ACTIVE);
			if (!businessPartner.isPresent()) {
				throw new FlickzzDeskException(DOES_NOT_EXIST,
						getDescription(DOES_NOT_EXIST.getDescription(), BUSINESS_PARTNER));
			}

			Optional<BPConfiguration> existingConfig = bPConfigurationRepository
					.findByBusinessPartnerBusinessPartnerIdAndIsActive(request.getBusinessPartnerId(), ACTIVE);

			BPConfiguration config = new BPConfiguration();
			if (!existingConfig.isPresent()) {
				config.setBusinessPartner(businessPartner.get());
				config.setCreatedBy(request.getCreatedBy());
				config.setIsCreatorAdmin(request.getIsCreatedByAdmin());
				bPConfigurationRepository.save(config);
			} else {
				config = existingConfig.get();
			}

			if (bpSupportGroupRepository.existsByConfigurationConfigurationIdAndGroupNameAndIsActive(
					config.getConfigurationId(), request.getGroupName().trim(), ACTIVE)) {
				throw new FlickzzDeskException(ALREADY_EXISTS,
						getDescription(ALREADY_EXISTS.getDescription(), "Support group"));
			}

			Set<Long> agentIds = new LinkedHashSet<>();
			for (Long agentId : request.getAgents()) {
				if (agentId == null) {
					continue;
				}
				agentIds.add(agentId);
			}
			if (agentIds.isEmpty()) {
				throw new FlickzzDeskException(INVALID_FIELD,
						getDescription(INVALID_FIELD.getDescription(), "Agent list"));
			}

			for (Long agentId : agentIds) {
				Optional<AgentMaster> agent = agentMasterRepository.findById(agentId);
				if (agent.isEmpty() || !agent.get().getIsActive()) {
					throw new FlickzzDeskException(DOES_NOT_EXIST,
							getDescription(DOES_NOT_EXIST.getDescription(), AGENT));
				}
			}

			BPSupportGroup supportGroup = BPSupportGroup.builder().configuration(config)
					.groupName(request.getGroupName().trim()).createdBy(request.getCreatedBy())
					.updatedBy(request.getUpdatedBy()).build();
			bpSupportGroupRepository.save(supportGroup);

			for (Long agentId : agentIds) {
				AgentMaster agent = agentMasterRepository.findById(agentId)
						.orElseThrow(() -> new FlickzzDeskException(DOES_NOT_EXIST,
								getDescription(DOES_NOT_EXIST.getDescription(), AGENT)));
				BPSupportGroupMember member = BPSupportGroupMember.builder().supportGroup(supportGroup).agent(agent)
						.isGroupLead(Boolean.FALSE).build();
				bpSupportGroupMemberRepository.save(member);
			}

			// Handle managers (internal and business partner managers)
			Set<Long> internalManagerIds = new LinkedHashSet<>();
			if (request.getManagerInternalAgents() != null) {
				for (Long mId : request.getManagerInternalAgents()) {
					if (mId != null) {
						internalManagerIds.add(mId);
					}
				}
			}

			Set<Long> bpManagerIds = new LinkedHashSet<>();
			if (request.getManagerBpAgents() != null) {
				for (Long mId : request.getManagerBpAgents()) {
					if (mId != null) {
						bpManagerIds.add(mId);
					}
				}
			}

			// validate manager agents exist (if any)
			Set<Long> allManagerIds = new LinkedHashSet<>();
			allManagerIds.addAll(internalManagerIds);
			allManagerIds.addAll(bpManagerIds);
			for (Long managerId : allManagerIds) {
				if (managerId == null) continue;
				Optional<AgentMaster> agent = agentMasterRepository.findById(managerId);
				if (agent.isEmpty() || !agent.get().getIsActive()) {
					throw new FlickzzDeskException(DOES_NOT_EXIST,
							getDescription(DOES_NOT_EXIST.getDescription(), AGENT));
				}
			}

			// save internal managers
			for (Long managerId : internalManagerIds) {
				AgentMaster agent = agentMasterRepository.findById(managerId)
						.orElseThrow(() -> new FlickzzDeskException(DOES_NOT_EXIST,
								getDescription(DOES_NOT_EXIST.getDescription(), AGENT)));
				BPSupportGroupManager manager = BPSupportGroupManager.builder().supportGroup(supportGroup)
						.agent(agent).isInternal(Boolean.TRUE).isBP(Boolean.FALSE).build();
				bpSupportGroupManagerRepository.save(manager);
			}

			// save BP managers
			for (Long managerId : bpManagerIds) {
				AgentMaster agent = agentMasterRepository.findById(managerId)
						.orElseThrow(() -> new FlickzzDeskException(DOES_NOT_EXIST,
								getDescription(DOES_NOT_EXIST.getDescription(), AGENT)));
				BPSupportGroupManager manager = BPSupportGroupManager.builder().supportGroup(supportGroup)
						.agent(agent).isInternal(Boolean.FALSE).isBP(Boolean.TRUE).build();
				bpSupportGroupManagerRepository.save(manager);
			}

			// Prepare new snapshot for audit
			String newSnapshot = null;
			try {
				newSnapshot = objectMapper.writeValueAsString(buildSupportGroupSnapshot(supportGroup, agentIds, internalManagerIds, bpManagerIds));
			} catch (Exception ignore) {
			}

			// Record CREATE audit
			java.util.Map<String, Object> changed = new java.util.HashMap<>();
			changed.put("groupName", supportGroup.getGroupName());
			changed.put("members", agentIds);
			changed.put("internalManagers", internalManagerIds);
			changed.put("bpManagers", bpManagerIds);
			String changedStr = null;
			try { changedStr = objectMapper.writeValueAsString(changed); } catch (Exception ignore) {}

			auditService.recordAudit(mapper.toSystemAuditRequest("BusinessPartner", "SupportGroup", "BPSupportGroup",
					supportGroup.getSupportGroupId(),
					CREATE,
					newSnapshot,
					null,
					changedStr,
					request.getCreatedBy(),
					loadUserNameByUserId(Long.valueOf(request.getCreatedBy())),
					request.getOrgId(),
					SUCCESS,
					null));

			return mapper.toSupportGroupVo(supportGroup);
		} catch (FlickzzDeskException e) {
			// record FlickzzDeskException as ERROR audit and rethrow
			String attempted = null;
			try {
				java.util.Map<String, Object> attemptedMap = new java.util.HashMap<>();
				attemptedMap.put("groupName", request != null ? request.getGroupName() : null);
				attemptedMap.put("members", request != null? request.getAgents() : null);
				attemptedMap.put("internalManagers", request != null ? request.getManagerInternalAgents() : null);
				attemptedMap.put("bpManagers", request != null ? request.getManagerBpAgents() : null);
				attempted = objectMapper.writeValueAsString(attemptedMap);
			} catch (Exception ignore) {
			}
			auditService.recordExceptionAudit(mapper.toSystemAuditRequest("BusinessPartner", "SupportGroup", "BPSupportGroup", null, CREATE,
					attempted, null, null, request != null ? request.getCreatedBy() : null,
					loadUserNameByUserId(Long.valueOf(request.getCreatedBy())), (request != null ? request.getOrgId() : null), FAILED, e.getDescription()), e);
				throw e;
		} catch (Exception e) {
			String attempted = null;
			try {
				java.util.Map<String, Object> attemptedMap = new java.util.HashMap<>();
				attemptedMap.put("groupName", request != null ? request.getGroupName() : null);
				attemptedMap.put("members", request != null && request.getAgents() != null ? request.getAgents() : null);
				attemptedMap.put("internalManagers", request != null ? request.getManagerInternalAgents() : null);
				attemptedMap.put("bpManagers", request != null ? request.getManagerBpAgents() : null);
				attempted = objectMapper.writeValueAsString(attemptedMap);
			} catch (Exception ignore) {
			}
			auditService.recordExceptionAudit(mapper.toSystemAuditRequest("BusinessPartner", "SupportGroup", "BPSupportGroup",
					null,
					CREATE,
					attempted,
					null,
					null,
					request != null ? request.getCreatedBy() : null,
					loadUserNameByUserId(Long.valueOf(request.getCreatedBy())),
					request != null ? request.getOrgId() : null,
					FAILED,
					e.getMessage()), e);
			log.error("Exception in createBusinessPartnerSupportGroupConfiguration method in BusinessPartnerService");
			throw new FlickzzDeskException(DEFAULT_ERROR_CODE);
		}
	}

	public BPSupportGroupVO updateBusinessPartnerSupportGroupConfiguration(BpConfigRequestVO request) {
		log.info(generateLog("updateBusinessPartnerSupportGroupConfiguration", this.getClass().getName()));
		BPSupportGroup supportGroup = null;
		try {
			if (request == null || request.getSupportGroupId() == null) {
				throw new FlickzzDeskException(DOES_NOT_EXIST,
						getDescription(DOES_NOT_EXIST.getDescription(), "Support group"));
			}
			if (request.getGroupName() == null || request.getGroupName().trim().isEmpty()) {
				throw new FlickzzDeskException(INVALID_FIELD,
						getDescription(INVALID_FIELD.getDescription(), "Group name"));
			}
			if (request.getAgents() == null || request.getAgents().isEmpty()) {
				throw new FlickzzDeskException(SET_TEXT, getDescription(SET_TEXT.getDescription(), "agent list"));
			}

			Optional<BPSupportGroup> existingGroup = bpSupportGroupRepository
					.findBySupportGroupIdAndIsActive(request.getSupportGroupId(), ACTIVE);
			if (existingGroup.isEmpty()) {
				throw new FlickzzDeskException(DOES_NOT_EXIST,
						getDescription(DOES_NOT_EXIST.getDescription(), "Support group"));
			}

			supportGroup = existingGroup.get();

			// Track changed fields
			Map<String, Object> changedFields = new HashMap<>();
			if (!supportGroup.getGroupName().equals(request.getGroupName().trim())) {
				changedFields.put("groupName", true);
			}
			if (!Objects.equals(supportGroup.getUpdatedBy(), request.getUpdatedBy())) {
				changedFields.put("updatedBy", true);
			}
			
			supportGroup.setGroupName(request.getGroupName().trim());
			supportGroup.setUpdatedBy(request.getUpdatedBy());
			bpSupportGroupRepository.save(supportGroup);

			Set<Long> incomingAgentIds = new LinkedHashSet<>();
			for (Long agentId : request.getAgents()) {
				if (agentId != null) {
					incomingAgentIds.add(agentId);
				}
			}
			if (incomingAgentIds.isEmpty()) {
				throw new FlickzzDeskException(INVALID_FIELD,
						getDescription(INVALID_FIELD.getDescription(), "Agent list"));
			}
			for (Long agentId : incomingAgentIds) {
				Optional<AgentMaster> agent = agentMasterRepository.findById(agentId);
				if (agent.isEmpty() || !agent.get().getIsActive()) {
					throw new FlickzzDeskException(DOES_NOT_EXIST,
							getDescription(DOES_NOT_EXIST.getDescription(), AGENT));
				}
			}

			List<BPSupportGroupMember> existingMembers = bpSupportGroupMemberRepository
					.findBySupportGroupSupportGroupId(supportGroup.getSupportGroupId());
			Set<Long> existingAgentIds = existingMembers.stream().map(member -> member.getAgent().getAgentId())
					.collect(Collectors.toSet());

			if (!existingAgentIds.equals(incomingAgentIds)) {
				changedFields.put("members", true);
			}

			for (Long agentId : incomingAgentIds) {
				Optional<BPSupportGroupMember> existingMember = bpSupportGroupMemberRepository
						.findBySupportGroupSupportGroupIdAndAgentAgentId(supportGroup.getSupportGroupId(), agentId);
				if (existingMember.isPresent()) {
					existingMember.get().setIsActive(Boolean.TRUE);
					bpSupportGroupMemberRepository.save(existingMember.get());
				} else {
					AgentMaster agent = agentMasterRepository.findById(agentId)
							.orElseThrow(() -> new FlickzzDeskException(DOES_NOT_EXIST,
									getDescription(DOES_NOT_EXIST.getDescription(), AGENT)));
					BPSupportGroupMember member = BPSupportGroupMember.builder().supportGroup(supportGroup).agent(agent)
							.isGroupLead(Boolean.FALSE).build();
					bpSupportGroupMemberRepository.save(member);
				}
			}

			for (BPSupportGroupMember member : existingMembers) {
				if (!incomingAgentIds.contains(member.getAgent().getAgentId())) {
					member.setIsActive(Boolean.FALSE);
					bpSupportGroupMemberRepository.save(member);
				}
			}

			// Handle managers for update: reactivate/add incoming managers and deactivate removed ones
			Set<Long> incomingInternalManagerIds = new LinkedHashSet<>();
			if (request.getManagerInternalAgents() != null) {
				for (Long mId : request.getManagerInternalAgents()) {
					if (mId != null) incomingInternalManagerIds.add(mId);
				}
			}

			Set<Long> incomingBpManagerIds = new LinkedHashSet<>();
			if (request.getManagerBpAgents() != null) {
				for (Long mId : request.getManagerBpAgents()) {
					if (mId != null) incomingBpManagerIds.add(mId);
				}
			}

			Set<Long> existingInternalManagerIds = existingGroup.get().getManagers().stream()
					.filter(BPSupportGroupManager::getIsInternal)
					.map(manager -> manager.getAgent().getAgentId())
					.collect(Collectors.toSet());

			if (!existingInternalManagerIds.equals(incomingInternalManagerIds)) {
				changedFields.put("internalManagers", true);
			}

			Set<Long> existingBpManagerIds = existingGroup.get().getManagers().stream()
					.filter(BPSupportGroupManager::getIsBP)
					.map(manager -> manager.getAgent().getAgentId())
					.collect(Collectors.toSet());


			// Capture old values before update
			String oldValue = null;
			try {
				oldValue = objectMapper.writeValueAsString(buildSupportGroupSnapshot(supportGroup, existingMembers.stream().map(BPSupportGroupMember::getAgent).map(AgentMaster::getAgentId).collect(Collectors.toSet()), existingInternalManagerIds, existingBpManagerIds));
			} catch (Exception ignore) {
			}

			if (!existingBpManagerIds.equals(incomingBpManagerIds)) {
				changedFields.put("bpManagers", true);
			}

			// validate managers exist (if provided)
			Set<Long> allIncomingManagers = new LinkedHashSet<>();
			allIncomingManagers.addAll(incomingInternalManagerIds);
			allIncomingManagers.addAll(incomingBpManagerIds);
			for (Long managerId : allIncomingManagers) {
				if (managerId == null) continue;
				Optional<AgentMaster> am = agentMasterRepository.findById(managerId);
				if (am.isEmpty() || !am.get().getIsActive()) {
					throw new FlickzzDeskException(DOES_NOT_EXIST,
							getDescription(DOES_NOT_EXIST.getDescription(), AGENT));
				}
			}

			List<BPSupportGroupManager> existingManagers = bpSupportGroupManagerRepository
					.findBySupportGroupSupportGroupId(supportGroup.getSupportGroupId());
			Set<Long> existingManagerIds = existingManagers.stream().map(m -> m.getAgent().getAgentId())
					.collect(Collectors.toSet());

			// add or reactivate incoming internal managers
			for (Long managerId : incomingInternalManagerIds) {
				Optional<BPSupportGroupManager> existingManager = bpSupportGroupManagerRepository
						.findBySupportGroupSupportGroupIdAndAgentAgentId(supportGroup.getSupportGroupId(), managerId);
				if (existingManager.isPresent()) {
					BPSupportGroupManager mgr = existingManager.get();
					mgr.setIsActive(Boolean.TRUE);
					mgr.setIsInternal(Boolean.TRUE);
					mgr.setIsBP(Boolean.FALSE);
					bpSupportGroupManagerRepository.save(mgr);
				} else {
					AgentMaster agent = agentMasterRepository.findById(managerId)
							.orElseThrow(() -> new FlickzzDeskException(DOES_NOT_EXIST,
									getDescription(DOES_NOT_EXIST.getDescription(), AGENT)));
					BPSupportGroupManager mgr = BPSupportGroupManager.builder().supportGroup(supportGroup)
							.agent(agent).isInternal(Boolean.TRUE).isBP(Boolean.FALSE).build();
					bpSupportGroupManagerRepository.save(mgr);
				}
			}

			// add or reactivate incoming BP managers
			for (Long managerId : incomingBpManagerIds) {
				Optional<BPSupportGroupManager> existingManager = bpSupportGroupManagerRepository
						.findBySupportGroupSupportGroupIdAndAgentAgentId(supportGroup.getSupportGroupId(), managerId);
				if (existingManager.isPresent()) {
					BPSupportGroupManager mgr = existingManager.get();
					mgr.setIsActive(Boolean.TRUE);
					mgr.setIsBP(Boolean.TRUE);
					mgr.setIsInternal(Boolean.FALSE);
					bpSupportGroupManagerRepository.save(mgr);
				} else {
					AgentMaster agent = agentMasterRepository.findById(managerId)
							.orElseThrow(() -> new FlickzzDeskException(DOES_NOT_EXIST,
									getDescription(DOES_NOT_EXIST.getDescription(), AGENT)));
					BPSupportGroupManager mgr = BPSupportGroupManager.builder().supportGroup(supportGroup)
							.agent(agent).isInternal(Boolean.FALSE).isBP(Boolean.TRUE).build();
					bpSupportGroupManagerRepository.save(mgr);
				}
			}

			// deactivate removed managers
			for (BPSupportGroupManager mgr : existingManagers) {
				Long aid = mgr.getAgent().getAgentId();
				if (!allIncomingManagers.contains(aid)) {
					mgr.setIsActive(Boolean.FALSE);
					bpSupportGroupManagerRepository.save(mgr);
				}
			}

			// Record UPDATE audit if there are changes
			if (!changedFields.isEmpty()) {
				String newSnapshot = null;
				try {
					newSnapshot = objectMapper.writeValueAsString(buildSupportGroupSnapshot(supportGroup, incomingAgentIds, incomingInternalManagerIds, incomingBpManagerIds));
				} catch (Exception ignore) {
				}
				String changedStr = null;
				try {
					changedStr = objectMapper.writeValueAsString(changedFields);
				} catch (Exception ignore) {
				}
				Long companyId = null;
				try {
					if (supportGroup.getConfiguration() != null && supportGroup.getConfiguration().getBusinessPartner() != null
						&& supportGroup.getConfiguration().getBusinessPartner().getCompany() != null) {
						companyId = supportGroup.getConfiguration().getBusinessPartner().getCompany().getCompanyId();
					}
				} catch (Exception ignore) {
				}
				auditService.recordAudit(mapper.toSystemAuditRequest("BusinessPartner", "SupportGroup", "BPSupportGroup",
						supportGroup.getSupportGroupId(),
						UPDATE,
						newSnapshot,
						oldValue,
						changedStr,
						request.getUpdatedBy(),
						loadUserNameByUserId(Long.valueOf(request.getUpdatedBy())),
						companyId,
						SUCCESS,
						null));
			}

			return mapper.toSupportGroupVo(supportGroup);
		} catch (FlickzzDeskException e) {
			// Attempt to save error audit
			try {
				Long entityId = null;
				Long companyId = null;
				String oldSnap = null;
				if (supportGroup != null) {
					entityId = supportGroup.getSupportGroupId();
					try {
						oldSnap = objectMapper.writeValueAsString(buildSupportGroupSnapshot(supportGroup, supportGroup.getMembers().stream().map(member -> member.getAgent().getAgentId()).collect(Collectors.toSet()),
								supportGroup.getManagers().stream().filter(manager -> manager.getIsInternal() == Boolean.TRUE) .map(internalManager -> internalManager.getAgent().getAgentId()).collect(Collectors.toSet()),
								supportGroup.getManagers().stream().filter(manager -> manager.getIsBP() == Boolean.TRUE) .map(bpManager -> bpManager.getAgent().getAgentId()).collect(Collectors.toSet())));
					} catch (Exception ignore) {
					}
					try {
						if (supportGroup.getConfiguration() != null && supportGroup.getConfiguration().getBusinessPartner() != null
							&& supportGroup.getConfiguration().getBusinessPartner().getCompany() != null) {
							companyId = supportGroup.getConfiguration().getBusinessPartner().getCompany().getCompanyId();
						}
					} catch (Exception ignore) {
					}
				}
				String newSnap = null;
				if (request != null) {
					try {
						Map<String, Object> attempted = new HashMap<>();
						attempted.put("groupName", request.getGroupName());
						attempted.put("agentCount", request.getAgents() != null ? request.getAgents().size() : 0);
						attempted.put("updatedBy", request.getUpdatedBy());
						newSnap = objectMapper.writeValueAsString(attempted);
					} catch (Exception ignore) {
					}
				}
				auditService.recordExceptionAudit(mapper.toSystemAuditRequest("BusinessPartner", "SupportGroup", "BPSupportGroup",
						entityId, UPDATE, newSnap, oldSnap, null, 
						request != null ? request.getUpdatedBy() : null,
						loadUserNameByUserId(Long.valueOf(request.getUpdatedBy())), companyId, FAILED, e.getDescription()), e);
			} catch (Exception ignore) {
			}
			throw e;
		} catch (Exception e) {
			// Attempt to save error audit
			try {
				Long entityId = null;
				Long companyId = null;
				String oldSnap = null;
				if (supportGroup != null) {
					entityId = supportGroup.getSupportGroupId();
					try {
						oldSnap = objectMapper.writeValueAsString(buildSupportGroupSnapshot(supportGroup, supportGroup.getMembers().stream().map(member -> member.getAgent().getAgentId()).collect(Collectors.toSet()),
								supportGroup.getManagers().stream().filter(manager -> manager.getIsInternal() == Boolean.TRUE) .map(internalManager -> internalManager.getAgent().getAgentId()).collect(Collectors.toSet()),
								supportGroup.getManagers().stream().filter(manager -> manager.getIsBP() == Boolean.TRUE) .map(bpManager -> bpManager.getAgent().getAgentId()).collect(Collectors.toSet())));
					} catch (Exception ignore) {
					}
					try {
						if (supportGroup.getConfiguration() != null && supportGroup.getConfiguration().getBusinessPartner() != null
							&& supportGroup.getConfiguration().getBusinessPartner().getCompany() != null) {
							companyId = supportGroup.getConfiguration().getBusinessPartner().getCompany().getCompanyId();
						}
					} catch (Exception ignore) {
					}
				}
				String newSnap = null;
				if (request != null) {
					try {
						Map<String, Object> attempted = new HashMap<>();
						attempted.put("groupName", request.getGroupName());
						attempted.put("agentCount", request.getAgents() != null ? request.getAgents().size() : 0);
						attempted.put("updatedBy", request.getUpdatedBy());
						newSnap = objectMapper.writeValueAsString(attempted);
					} catch (Exception ignore) {
					}
				}
				auditService.recordExceptionAudit(mapper.toSystemAuditRequest("BusinessPartner", "SupportGroup", "BPSupportGroup",
						entityId, UPDATE, newSnap, oldSnap, null,
						request != null ? request.getUpdatedBy() : null, loadUserNameByUserId(Long.valueOf(request.getUpdatedBy())), companyId, FAILED, e.getMessage()), e);
			} catch (Exception ignore) {
			}
			log.error("Exception in updateBusinessPartnerSupportGroupConfiguration method in BusinessPartnerService");
			throw new FlickzzDeskException(DEFAULT_ERROR_CODE);
		}
	}

	public void deleteBusinessPartnerSupportGroupConfiguration(String supportGroupId, Long userId) {
		log.info(generateLog("deleteBusinessPartnerSupportGroupConfiguration", this.getClass().getName()));
		BPSupportGroup supportGroup = null;
		try {
			Optional<BPSupportGroup> existingGroup = bpSupportGroupRepository
					.findBySupportGroupIdAndIsActive(Long.valueOf(supportGroupId), ACTIVE);
			if (existingGroup.isEmpty()) {
				throw new FlickzzDeskException(DOES_NOT_EXIST,
						getDescription(DOES_NOT_EXIST.getDescription(), "Support group"));
			}

			supportGroup = existingGroup.get();

			// Capture old snapshot before deletion
			String oldValue = null;
			try {
				oldValue = objectMapper.writeValueAsString(buildSupportGroupSnapshot(supportGroup,
						supportGroup.getMembers().stream().map(member -> member.getAgent().getAgentId()).collect(Collectors.toSet()),
						supportGroup.getManagers().stream().filter(manager -> manager.getIsInternal()).map(manager -> manager.getAgent().getAgentId()).collect(Collectors.toSet()),
						supportGroup.getManagers().stream().filter(manager -> manager.getIsBP()).map(manager -> manager.getAgent().getAgentId()).collect(Collectors.toSet())));
			} catch (Exception ignore) {
			}

			validateSupportGroupAssignmentForRemoval(supportGroup);

			bpSupportGroupRepository.delete(supportGroup);

			List<BPSupportGroupMember> members = bpSupportGroupMemberRepository
					.findBySupportGroupSupportGroupIdAndIsActive(supportGroup.getSupportGroupId(), ACTIVE);
			for (BPSupportGroupMember member : members) {
				bpSupportGroupMemberRepository.delete(member);
			}

			List<BPSupportGroupManager> managers = bpSupportGroupManagerRepository
					.findBySupportGroupSupportGroupIdAndIsActive(supportGroup.getSupportGroupId(), ACTIVE);
			for (BPSupportGroupManager manager : managers) {
				bpSupportGroupManagerRepository.delete(manager);
			}

			// Record DELETE audit
			Long companyId = null;
			try {
				if (supportGroup.getConfiguration() != null && supportGroup.getConfiguration().getBusinessPartner() != null
					&& supportGroup.getConfiguration().getBusinessPartner().getCompany() != null) {
					companyId = supportGroup.getConfiguration().getBusinessPartner().getCompany().getCompanyId();
				}
			} catch (Exception ignore) {
			}
			auditService.recordAudit(mapper.toSystemAuditRequest("BusinessPartner", "SupportGroup", "BPSupportGroup",
					supportGroup.getSupportGroupId(),
					DELETE,
					oldValue,
					null,
					null,
					userId,
					loadUserNameByUserId(Long.valueOf(userId)),
					companyId,
					SUCCESS,
					null));
		} catch (FlickzzDeskException e) {
			// record FlickzzDeskException as ERROR audit and rethrow
			try {
				Long entityId = null;
				Long companyId = null;
				try {
					entityId = Long.valueOf(supportGroupId);
				} catch (Exception ignore) {
				}
				if (supportGroup != null && supportGroup.getConfiguration() != null 
					&& supportGroup.getConfiguration().getBusinessPartner() != null
					&& supportGroup.getConfiguration().getBusinessPartner().getCompany() != null) {
					companyId = supportGroup.getConfiguration().getBusinessPartner().getCompany().getCompanyId();
				}
				auditService.recordExceptionAudit(mapper.toSystemAuditRequest("BusinessPartner", "SupportGroup", "BPSupportGroup",
						entityId, DELETE,
						null, null, null, userId, loadUserNameByUserId(Long.valueOf(userId)), companyId, FAILED, e.getDescription()), e);
			} catch (Exception ignore) {
			}
			throw e;
		} catch (Exception e) {
			// record generic exception as ERROR audit
			try {
				Long entityId = null;
				Long companyId = null;
				try {
					entityId = Long.valueOf(supportGroupId);
				} catch (Exception ignore) {
				}
				if (supportGroup != null && supportGroup.getConfiguration() != null 
					&& supportGroup.getConfiguration().getBusinessPartner() != null
					&& supportGroup.getConfiguration().getBusinessPartner().getCompany() != null) {
					companyId = supportGroup.getConfiguration().getBusinessPartner().getCompany().getCompanyId();
				}
				auditService.recordExceptionAudit(mapper.toSystemAuditRequest("BusinessPartner", "SupportGroup", "BPSupportGroup",
						entityId, DELETE,
						null, null, null, userId, loadUserNameByUserId(Long.valueOf(userId)), companyId, FAILED, e.getMessage()), e);
			} catch (Exception ignore) {
			}
			log.error("Exception in deleteBusinessPartnerSupportGroupConfiguration method in BusinessPartnerService");
			throw new FlickzzDeskException(DEFAULT_ERROR_CODE);
		}
	}

	private void validateSupportGroupAssignmentForRemoval(BPSupportGroup supportGroup) {
		if (supportGroup == null) {
			return;
		}
		boolean hasAssignment = bpAssignmentRepository
				.findByConfigurationConfigurationIdAndSupportGroupSupportGroupIdAndIsActive(
						supportGroup.getConfiguration().getConfigurationId(), supportGroup.getSupportGroupId(), ACTIVE)
				.isPresent();
		if (hasAssignment) {
			throw new FlickzzDeskException(INVALID_FIELD,
					supportGroup.getGroupName() + " cannot be removed as it has existing assignment");
		}
	}

	public BPSupportGroupVO getBusinessPartnerSupportGroupConfigurationById(Long supportGroupId) {
		log.info(generateLog("getBusinessPartnerSupportGroupConfigurationById", this.getClass().getName()));
		try {
			Optional<BPSupportGroup> existingGroup = bpSupportGroupRepository
					.findBySupportGroupIdAndIsActive(supportGroupId, ACTIVE);
			if (existingGroup.isEmpty()) {
				throw new FlickzzDeskException(DOES_NOT_EXIST,
						getDescription(DOES_NOT_EXIST.getDescription(), "Support group"));
			}
			return mapper.toSupportGroupVo(existingGroup.get());
		} catch (FlickzzDeskException e) {
			throw e;
		} catch (Exception e) {
			log.error("Exception in getBusinessPartnerSupportGroupConfigurationById method in BusinessPartnerService");
			throw new FlickzzDeskException(DEFAULT_ERROR_CODE);
		}
	}

	public List<BPSupportGroupVO> getBusinessPartnerSupportGroupConfiguration(Long businessPartnerId) {
		log.info(generateLog("getBusinessPartnerSupportGroupConfiguration", this.getClass().getName()));
		try {
			Optional<BusinessPartner> businessPartner = businessPartnerRepository
					.findByBusinessPartnerIdAndIsActive(businessPartnerId, ACTIVE);
			if (!businessPartner.isPresent()) {
				throw new FlickzzDeskException(DOES_NOT_EXIST,
						getDescription(DOES_NOT_EXIST.getDescription(), BUSINESS_PARTNER));
			}

			Optional<BPConfiguration> existingConfig = bPConfigurationRepository
					.findByBusinessPartnerBusinessPartnerIdAndIsActive(businessPartnerId, ACTIVE);
			if (!existingConfig.isPresent()) {
				return Collections.emptyList();
			}

			List<BPSupportGroup> groups = bpSupportGroupRepository
					.findByConfigurationConfigurationIdAndIsActive(existingConfig.get().getConfigurationId(), ACTIVE);
			return groups.stream().map(mapper::toNoBakcRefSupportGroupVo).toList();
		} catch (FlickzzDeskException e) {
			throw e;
		} catch (Exception e) {
			log.error("Exception in getBusinessPartnerSupportGroupConfiguration method in BusinessPartnerService");
			throw new FlickzzDeskException(DEFAULT_ERROR_CODE);
		}
	}

	public List<BPSubCategoryVO> getBusinessPartnerSubCategoryConfiguration(Long valueOf) {

		log.info(generateLog("getBusinessPartnerSubCategoryConfiguration", this.getClass().getName()));
		try {
			Optional<BusinessPartner> businessPartner = businessPartnerRepository
					.findByBusinessPartnerIdAndIsActive(valueOf, ACTIVE);
			if (!businessPartner.isPresent()) {
				throw new FlickzzDeskException(DOES_NOT_EXIST,
						getDescription(DOES_NOT_EXIST.getDescription(), BUSINESS_PARTNER));
			}

			Optional<BPConfiguration> existingConfig = bPConfigurationRepository
					.findByBusinessPartnerBusinessPartnerIdAndIsActive(valueOf, ACTIVE);
			if (!existingConfig.isPresent()) {
				return Collections.emptyList();
			}

			List<BPCategory> categories = bpCategoryRepository
					.findByConfigurationConfigurationIdAndIsActive(existingConfig.get().getConfigurationId(), ACTIVE);

			List<BPSubCategoryVO> subCategoryVOs = new ArrayList<>();
			for (BPCategory category : categories) {
				List<BPSubCategory> subCategories = bpSubCategoryRepository
						.findByCategoryCategoryIdAndIsActive(category.getCategoryId(), ACTIVE);
				for (BPSubCategory subCategory : subCategories) {
					BPSubCategoryVO vo = new BPSubCategoryVO();
					vo.setSubCategoryId(subCategory.getSubCategoryId());
					vo.setSubCategoryName(subCategory.getSubCategoryName());
					subCategoryVOs.add(vo);
				}
			}
			return subCategoryVOs;
		} catch (FlickzzDeskException e) {
			throw e;
		} catch (Exception e) {
			log.error("Exception in getBusinessPartnerSubCategoryConfiguration method in BusinessPartnerService");
			throw new FlickzzDeskException(DEFAULT_ERROR_CODE);
		}
	}

	public BPAssignmentVO createBusinessPartnerAssignmentConfiguration(BpConfigRequestVO request) {
		log.info(generateLog("createBusinessPartnerAssignmentConfiguration", this.getClass().getName()));
		try {
			if (request == null || request.getBusinessPartnerId() == null) {
				throw new FlickzzDeskException(DOES_NOT_EXIST,
						getDescription(DOES_NOT_EXIST.getDescription(), BUSINESS_PARTNER));
			}
			if (request.getSupportGroupId() == null) {
				throw new FlickzzDeskException(DOES_NOT_EXIST,
						getDescription(DOES_NOT_EXIST.getDescription(), "Support group"));
			}
			if (request.getSubCategoryId() == null) {
				throw new FlickzzDeskException(DOES_NOT_EXIST,
						getDescription(DOES_NOT_EXIST.getDescription(), "Sub category"));
			}

			Optional<BusinessPartner> businessPartner = businessPartnerRepository
					.findByBusinessPartnerIdAndIsActive(request.getBusinessPartnerId(), ACTIVE);
			if (!businessPartner.isPresent()) {
				throw new FlickzzDeskException(DOES_NOT_EXIST,
						getDescription(DOES_NOT_EXIST.getDescription(), BUSINESS_PARTNER));
			}

			Optional<BPConfiguration> existingConfig = bPConfigurationRepository
					.findByBusinessPartnerBusinessPartnerIdAndIsActive(request.getBusinessPartnerId(), ACTIVE);
			BPConfiguration config = existingConfig.orElseGet(() -> {
				BPConfiguration newConfig = new BPConfiguration();
				newConfig.setBusinessPartner(businessPartner.get());
				newConfig.setCreatedBy(request.getCreatedBy());
				newConfig.setIsCreatorAdmin(request.getIsCreatedByAdmin());
				bPConfigurationRepository.save(newConfig);
				return newConfig;
			});

			Optional<BPSupportGroup> supportGroup = bpSupportGroupRepository
					.findBySupportGroupIdAndIsActive(request.getSupportGroupId(), ACTIVE);
			if (supportGroup.isEmpty()) {
				throw new FlickzzDeskException(DOES_NOT_EXIST,
						getDescription(DOES_NOT_EXIST.getDescription(), "Support group"));
			}
			if (!Objects.equals(supportGroup.get().getConfiguration().getConfigurationId(),
					config.getConfigurationId())) {
				throw new FlickzzDeskException(INVALID_FIELD,
						getDescription(INVALID_FIELD.getDescription(), "Support group"));
			}

			Optional<BPSubCategory> subCategory = bpSubCategoryRepository
					.findBySubCategoryIdAndIsActive(request.getSubCategoryId(), ACTIVE);
			if (subCategory.isEmpty()) {
				throw new FlickzzDeskException(DOES_NOT_EXIST,
						getDescription(DOES_NOT_EXIST.getDescription(), "Sub category"));
			}
			if (!Objects.equals(subCategory.get().getCategory().getConfiguration().getConfigurationId(),
					config.getConfigurationId())) {
				throw new FlickzzDeskException(INVALID_FIELD,
						getDescription(INVALID_FIELD.getDescription(), "Sub category"));
			}

			Optional<BPAssignment> existingAssignmentBySupportGroup = bpAssignmentRepository
					.findByConfigurationConfigurationIdAndSupportGroupSupportGroupIdAndIsActive(
							config.getConfigurationId(), request.getSupportGroupId(), ACTIVE);
			if (existingAssignmentBySupportGroup.isPresent()) {
				throw new FlickzzDeskException(ALREADY_EXISTS,
						getDescription(ALREADY_EXISTS.getDescription(), "Assignment for support group"));
			}

			Optional<BPAssignment> existingAssignmentBySubCategory = bpAssignmentRepository
					.findByConfigurationConfigurationIdAndSubCategorySubCategoryIdAndIsActive(
							config.getConfigurationId(), request.getSubCategoryId(), ACTIVE);
			if (existingAssignmentBySubCategory.isPresent()) {
				throw new FlickzzDeskException(ALREADY_EXISTS,
						getDescription(ALREADY_EXISTS.getDescription(), "Assignment for sub category"));
			}

			Optional<BPAssignment> existingAssignment = bpAssignmentRepository
					.findByConfigurationConfigurationIdAndSupportGroupSupportGroupIdAndSubCategorySubCategoryId(
							config.getConfigurationId(), request.getSupportGroupId(), request.getSubCategoryId());

			BPAssignment assignment;

			if (existingAssignment.isPresent()) {
				assignment = existingAssignment.get();
				assignment.setIsActive(Boolean.TRUE);
				assignment.setUpdatedBy(request.getUpdatedBy());
			} else {
				assignment = BPAssignment.builder().configuration(config).subCategory(subCategory.get())
						.supportGroup(supportGroup.get()).createdBy(request.getCreatedBy())
						.updatedBy(request.getUpdatedBy()).build();
			}
			
			// Prepare new snapshot for audit
			String newSnapshot = null;
			try {
				newSnapshot = objectMapper.writeValueAsString(buildAssignmentSnapshot(assignment));
			} catch (Exception ignore) {
			}
			bpAssignmentRepository.save(assignment);

			// Record CREATE audit
			java.util.Map<String, Object> changed = new java.util.HashMap<>();
			changed.put("supportGroupId", assignment.getSupportGroup().getSupportGroupId());
			changed.put("subCategoryId", assignment.getSubCategory().getSubCategoryId());
			String changedStr = null;
			try { changedStr = objectMapper.writeValueAsString(changed); } catch (Exception ignore) {}

			auditService.recordAudit(mapper.toSystemAuditRequest("BusinessPartner", "Assignment", "BPAssignment",
					assignment.getAssignmentId(),
					CREATE,
					newSnapshot,
					null,
					changedStr,
					request.getCreatedBy(),
					loadUserNameByUserId(Long.valueOf(request.getCreatedBy())),
					request.getOrgId(),
					SUCCESS,
					null));

			return mapper.toBPAssignmentVo(assignment);
		} catch (FlickzzDeskException e) {
			// record FlickzzDeskException as ERROR audit and rethrow
			String attempted = null;
			try {
				java.util.Map<String, Object> attemptedMap = new java.util.HashMap<>();
				attemptedMap.put("supportGroupId", request != null ? request.getSupportGroupId() : null);
				attemptedMap.put("subCategoryId", request != null ? request.getSubCategoryId() : null);
				attempted = objectMapper.writeValueAsString(attemptedMap);
			} catch (Exception ignore) {
			}
			auditService.recordExceptionAudit(mapper.toSystemAuditRequest("BusinessPartner", "Assignment", "BPAssignment", null, CREATE,
					attempted, null, null, request != null ? request.getCreatedBy() : null,
					loadUserNameByUserId(Long.valueOf(request.getCreatedBy())), (request != null ? request.getOrgId() : null), FAILED, e.getDescription()), e);
				throw e;
		} catch (Exception e) {
			String attempted = null;
			try {
				java.util.Map<String, Object> attemptedMap = new java.util.HashMap<>();
				attemptedMap.put("supportGroupId", request != null ? request.getSupportGroupId() : null);
				attemptedMap.put("subCategoryId", request != null ? request.getSubCategoryId() : null);
				attempted = objectMapper.writeValueAsString(attemptedMap);
			} catch (Exception ignore) {
			}
			auditService.recordExceptionAudit(mapper.toSystemAuditRequest("BusinessPartner", "Assignment", "BPAssignment",
					null,
					CREATE,
					attempted,
					null,
					null,
					request != null ? request.getCreatedBy() : null,
					loadUserNameByUserId(Long.valueOf(request.getCreatedBy())),
					request != null ? request.getOrgId() : null,
					FAILED,
					e.getMessage()), e);
			log.error("Exception in createBusinessPartnerAssignmentConfiguration method in BusinessPartnerService");
			throw new FlickzzDeskException(DEFAULT_ERROR_CODE);
		}
	}

	public BPAssignmentVO updateBusinessPartnerAssignmentConfiguration(BpConfigRequestVO request) {
		log.info(generateLog("updateBusinessPartnerAssignmentConfiguration", this.getClass().getName()));
		BPAssignment assignment = null;
		try {
			if (request == null || request.getSupportGroupId() == null || request.getSubCategoryId() == null) {
				throw new FlickzzDeskException(DOES_NOT_EXIST,
						getDescription(DOES_NOT_EXIST.getDescription(), "Assignment"));
			}

			Optional<BPAssignment> existingAssignment = bpAssignmentRepository
					.findBySupportGroupSupportGroupIdAndIsActive(request.getSupportGroupId(), ACTIVE);
			if (existingAssignment.isEmpty()) {
				throw new FlickzzDeskException(DOES_NOT_EXIST,
						getDescription(DOES_NOT_EXIST.getDescription(), "Assignment"));
			}

			assignment = existingAssignment.get();
			
			// Capture old values before update
			String oldValue = null;
			try {
				oldValue = objectMapper.writeValueAsString(buildAssignmentSnapshot(assignment));
			} catch (Exception ignore) {
			}
			
			// Track changed fields
			Map<String, Object> changedFields = new HashMap<>();
			if (!Objects.equals(assignment.getSubCategory().getSubCategoryId(), request.getSubCategoryId())) {
				changedFields.put("subCategoryId", true);
			}
			if (!Objects.equals(assignment.getUpdatedBy(), request.getUpdatedBy())) {
				changedFields.put("updatedBy", true);
			}

			Optional<BPSubCategory> targetSubCategory = bpSubCategoryRepository
					.findBySubCategoryIdAndIsActive(request.getSubCategoryId(), ACTIVE);
			if (targetSubCategory.isEmpty()) {
				throw new FlickzzDeskException(DOES_NOT_EXIST,
						getDescription(DOES_NOT_EXIST.getDescription(), "Sub category"));
			}

			Optional<BPAssignment> assignmentForSubCategory = bpAssignmentRepository
					.findByConfigurationConfigurationIdAndSubCategorySubCategoryIdAndIsActive(
							assignment.getConfiguration().getConfigurationId(),
							request.getSubCategoryId(), ACTIVE);
			if (assignmentForSubCategory.isPresent()
					&& !Objects.equals(assignmentForSubCategory.get().getAssignmentId(),
							assignment.getAssignmentId())) {
				throw new FlickzzDeskException(ALREADY_EXISTS,
						getDescription(ALREADY_EXISTS.getDescription(), "Assignment for sub category"));
			}

			assignment.setUpdatedBy(request.getUpdatedBy());
			assignment.setSubCategory(targetSubCategory.get());
			bpAssignmentRepository.save(assignment);
			
			// Record UPDATE audit if there are changes
			if (!changedFields.isEmpty()) {
				String newSnapshot = null;
				try {
					newSnapshot = objectMapper.writeValueAsString(buildAssignmentSnapshot(assignment));
				} catch (Exception ignore) {
				}
				String changedStr = null;
				try {
					changedStr = objectMapper.writeValueAsString(changedFields);
				} catch (Exception ignore) {
				}
				Long companyId = null;
				try {
					if (assignment.getConfiguration() != null && assignment.getConfiguration().getBusinessPartner() != null
						&& assignment.getConfiguration().getBusinessPartner().getCompany() != null) {
						companyId = assignment.getConfiguration().getBusinessPartner().getCompany().getCompanyId();
					}
				} catch (Exception ignore) {
				}
				auditService.recordAudit(mapper.toSystemAuditRequest("BusinessPartner", "Assignment", "BPAssignment",
						assignment.getAssignmentId(),
						UPDATE,
						newSnapshot,
						oldValue,
						changedStr,
						request.getUpdatedBy(),
						loadUserNameByUserId(Long.valueOf(request.getUpdatedBy())),
						companyId,
						SUCCESS,
						null));
			}
			
			return mapper.toBPAssignmentVo(assignment);
		} catch (FlickzzDeskException e) {
			// Attempt to save error audit
			try {
				Long entityId = null;
				Long companyId = null;
				String oldSnap = null;
				if (assignment != null) {
					entityId = assignment.getAssignmentId();
					try {
						oldSnap = objectMapper.writeValueAsString(buildAssignmentSnapshot(assignment));
					} catch (Exception ignore) {
					}
					try {
						if (assignment.getConfiguration() != null && assignment.getConfiguration().getBusinessPartner() != null
							&& assignment.getConfiguration().getBusinessPartner().getCompany() != null) {
							companyId = assignment.getConfiguration().getBusinessPartner().getCompany().getCompanyId();
						}
					} catch (Exception ignore) {
					}
				}
				String newSnap = null;
				if (request != null) {
					try {
						Map<String, Object> attempted = new HashMap<>();
						attempted.put("supportGroupId", request.getSupportGroupId());
						attempted.put("subCategoryId", request.getSubCategoryId());
						attempted.put("updatedBy", request.getUpdatedBy());
						newSnap = objectMapper.writeValueAsString(attempted);
					} catch (Exception ignore) {
					}
				}
				auditService.recordExceptionAudit(mapper.toSystemAuditRequest("BusinessPartner", "Assignment", "BPAssignment",
						entityId, UPDATE, newSnap, oldSnap, null, 
						request != null ? request.getUpdatedBy() : null,
						loadUserNameByUserId(Long.valueOf(request.getUpdatedBy())), companyId, FAILED, e.getDescription()), e);
			} catch (Exception ignore) {
			}
			throw e;
		} catch (Exception e) {
			// Attempt to save error audit
			try {
				Long entityId = null;
				Long companyId = null;
				String oldSnap = null;
				if (assignment != null) {
					entityId = assignment.getAssignmentId();
					try {
						oldSnap = objectMapper.writeValueAsString(buildAssignmentSnapshot(assignment));
					} catch (Exception ignore) {
					}
					try {
						if (assignment.getConfiguration() != null && assignment.getConfiguration().getBusinessPartner() != null
							&& assignment.getConfiguration().getBusinessPartner().getCompany() != null) {
							companyId = assignment.getConfiguration().getBusinessPartner().getCompany().getCompanyId();
						}
					} catch (Exception ignore) {
					}
				}
				String newSnap = null;
				if (request != null) {
					try {
						Map<String, Object> attempted = new HashMap<>();
						attempted.put("supportGroupId", request.getSupportGroupId());
						attempted.put("subCategoryId", request.getSubCategoryId());
						attempted.put("updatedBy", request.getUpdatedBy());
						newSnap = objectMapper.writeValueAsString(attempted);
					} catch (Exception ignore) {
					}
				}
				auditService.recordExceptionAudit(mapper.toSystemAuditRequest("BusinessPartner", "Assignment", "BPAssignment",
						entityId, UPDATE, newSnap, oldSnap, null,
						request != null ? request.getUpdatedBy() : null, loadUserNameByUserId(Long.valueOf(request.getUpdatedBy())), companyId, FAILED, e.getMessage()), e);
			} catch (Exception ignore) {
			}
			log.error("Exception in updateBusinessPartnerAssignmentConfiguration method in BusinessPartnerService");
			throw new FlickzzDeskException(DEFAULT_ERROR_CODE);
		}
	}

	public void deleteBusinessPartnerAssignmentConfiguration(String assignmentId, Long userId) {
		log.info(generateLog("deleteBusinessPartnerAssignmentConfiguration", this.getClass().getName()));
		BPAssignment assignment = null;
		try {
			Optional<BPAssignment> existingAssignment = bpAssignmentRepository
					.findByAssignmentIdAndIsActive(Long.valueOf(assignmentId), ACTIVE);
			if (existingAssignment.isEmpty()) {
				throw new FlickzzDeskException(DOES_NOT_EXIST,
						getDescription(DOES_NOT_EXIST.getDescription(), "Assignment"));
			}
			
			assignment = existingAssignment.get();
			
			// Capture old snapshot before deletion
			String oldValue = null;
			try {
				oldValue = objectMapper.writeValueAsString(buildAssignmentSnapshot(assignment));
			} catch (Exception ignore) {
			}

			bpAssignmentRepository.delete(assignment);
			
			// Record DELETE audit
			Long companyId = null;
			try {
				if (assignment.getConfiguration() != null && assignment.getConfiguration().getBusinessPartner() != null
					&& assignment.getConfiguration().getBusinessPartner().getCompany() != null) {
					companyId = assignment.getConfiguration().getBusinessPartner().getCompany().getCompanyId();
				}
			} catch (Exception ignore) {
			}
			auditService.recordAudit(mapper.toSystemAuditRequest("BusinessPartner", "Assignment", "BPAssignment",
					assignment.getAssignmentId(),
					DELETE,
					oldValue,
					null,
					null,
					userId,
					loadUserNameByUserId(Long.valueOf(userId)),
					companyId,
					SUCCESS,
					null));
		} catch (FlickzzDeskException e) {
			// record FlickzzDeskException as ERROR audit and rethrow
			try {
				Long entityId = null;
				Long companyId = null;
				try {
					entityId = Long.valueOf(assignmentId);
				} catch (Exception ignore) {
				}
				if (assignment != null && assignment.getConfiguration() != null 
					&& assignment.getConfiguration().getBusinessPartner() != null
					&& assignment.getConfiguration().getBusinessPartner().getCompany() != null) {
					companyId = assignment.getConfiguration().getBusinessPartner().getCompany().getCompanyId();
				}
				auditService.recordExceptionAudit(mapper.toSystemAuditRequest("BusinessPartner", "Assignment", "BPAssignment",
						entityId, DELETE,
						null, null, null, userId, loadUserNameByUserId(Long.valueOf(userId)), companyId, FAILED, e.getDescription()), e);
			} catch (Exception ignore) {
			}
			throw e;
		} catch (Exception e) {
			// record generic exception as ERROR audit
			try {
				Long entityId = null;
				Long companyId = null;
				try {
					entityId = Long.valueOf(assignmentId);
				} catch (Exception ignore) {
				}
				if (assignment != null && assignment.getConfiguration() != null 
					&& assignment.getConfiguration().getBusinessPartner() != null
					&& assignment.getConfiguration().getBusinessPartner().getCompany() != null) {
					companyId = assignment.getConfiguration().getBusinessPartner().getCompany().getCompanyId();
				}
				auditService.recordExceptionAudit(mapper.toSystemAuditRequest("BusinessPartner", "Assignment", "BPAssignment",
						entityId, DELETE,
						null, null, null, userId, loadUserNameByUserId(Long.valueOf(userId)), companyId, FAILED, e.getMessage()), e);
			} catch (Exception ignore) {
			}
			log.error("Exception in deleteBusinessPartnerAssignmentConfiguration method in BusinessPartnerService");
			throw new FlickzzDeskException(DEFAULT_ERROR_CODE);
		}
	}

	public BPAssignmentVO getBusinessPartnerAssignmentConfigurationBySupportGroupId(Long supportGroupId) {
		log.info(generateLog("getBusinessPartnerAssignmentConfigurationBySupportGroupId", this.getClass().getName()));
		try {
			Optional<BPAssignment> existingAssignment = bpAssignmentRepository
					.findBySupportGroupSupportGroupIdAndIsActive(supportGroupId, ACTIVE);
			if (existingAssignment.isEmpty()) {
				throw new FlickzzDeskException(DOES_NOT_EXIST,
						getDescription(DOES_NOT_EXIST.getDescription(), "Assignment"));
			}
			return mapper.toBPAssignmentVo(existingAssignment.get());
		} catch (FlickzzDeskException e) {
			throw e;
		} catch (Exception e) {
			log.error(
					"Exception in getBusinessPartnerAssignmentConfigurationBySupportGroupId method in BusinessPartnerService");
			throw new FlickzzDeskException(DEFAULT_ERROR_CODE);
		}
	}

	public List<BPAssignmentVO> getBusinessPartnerAssignmentConfiguration(Long businessPartnerId) {
		log.info(generateLog("getBusinessPartnerAssignmentConfiguration", this.getClass().getName()));
		try {
			Optional<BusinessPartner> businessPartner = businessPartnerRepository
					.findByBusinessPartnerIdAndIsActive(businessPartnerId, ACTIVE);
			if (!businessPartner.isPresent()) {
				throw new FlickzzDeskException(DOES_NOT_EXIST,
						getDescription(DOES_NOT_EXIST.getDescription(), BUSINESS_PARTNER));
			}

			Optional<BPConfiguration> existingConfig = bPConfigurationRepository
					.findByBusinessPartnerBusinessPartnerIdAndIsActive(businessPartnerId, ACTIVE);
			if (!existingConfig.isPresent()) {
				return Collections.emptyList();
			}

			List<BPAssignment> assignments = bpAssignmentRepository
					.findByConfigurationConfigurationIdAndIsActive(existingConfig.get().getConfigurationId(), ACTIVE);
			return assignments.stream().map(mapper::toBPAssignmentVo).toList();
		} catch (FlickzzDeskException e) {
			throw e;
		} catch (Exception e) {
			log.error("Exception in getBusinessPartnerAssignmentConfiguration method in BusinessPartnerService");
			throw new FlickzzDeskException(DEFAULT_ERROR_CODE);
		}
	}

	public String loadUserNameByUserId(Long userId) {
		EnquiryRegistration enquiryRegistration = enquiryRegistrationRepository
				.findById(userId).orElse(null);
		if (enquiryRegistration != null) {
			return buildName(enquiryRegistration.getFirstName(), enquiryRegistration.getMiddleName(), enquiryRegistration.getLastName());
		}

		User user = userRepository.findById(userId).orElseThrow(() -> new FlickzzDeskException(DOES_NOT_EXIST,
				getDescription(DOES_NOT_EXIST.getDescription(), FD_USER)));
		return buildName(user.getFirstName(), user.getMiddleName(), user.getLastName());
	}
}
