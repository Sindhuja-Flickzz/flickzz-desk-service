package com.flickzz.desk.service;

import static com.flickzz.desk.config.FlickzzDeskConstants.*;
import static com.flickzz.desk.config.FlickzzDeskUtility.*;
import static com.flickzz.desk.exception.FlickzzDeskErrorCodes.*;

import java.util.*;

import com.flickzz.desk.vo.request.SkillRequestVO;
import org.slf4j.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.*;

import com.flickzz.desk.exception.*;
import com.flickzz.desk.mapper.*;
import com.flickzz.desk.model.*;
import com.flickzz.desk.repo.*;
import com.flickzz.desk.vo.*;
import tools.jackson.databind.ObjectMapper;

@Service
public class SkillsService {

	private static final Logger log = LoggerFactory.getLogger(SkillsService.class);

	@Autowired
	SkillMasterRepository skillMasterRepository;

	@Autowired
	CompanyMasterRepository companyMasterRepository;
	
	@Autowired
	CommonMapper mapper;
	
	@Autowired
	private AuditService auditService;
	
	@Autowired
	private ObjectMapper objectMapper;
	
    @Autowired
    private AgentSkillsMappingRepository agentSkillsMappingRepository;

	public List<SkillMasterVO> createSkills(List<SkillRequestVO> skills) {
		log.info(generateLog("createSkills", this.getClass().getName()));
		try {
			if (skills == null || skills.isEmpty()) {
				throw new FlickzzDeskException(NO_DATA, NO_DATA.getDescription());
			}

			CompanyMaster company = companyMasterRepository.findByCompanyId(skills.get(0).getCompanyId())
					.orElseThrow(() -> new FlickzzDeskException(DOES_NOT_EXIST,
							getDescription(DOES_NOT_EXIST.getDescription(), COMPANY)));

			List<SkillMasterVO> skillMasterVOs = new ArrayList<SkillMasterVO>();

			skills.forEach(skill -> {
				if (skill.getSkillName() == null) {
					throw new FlickzzDeskException(INVALID_FIELD,
							getDescription(INVALID_FIELD.getDescription(), SKILL_NAME));
				}

				Optional<SkillMaster> skillMaster =  skillMasterRepository.findBySkillNameAndCompany_CompanyId(skill.getSkillName(),
						skill.getCompanyId());

				if (skillMaster.isPresent()) {
					if(skillMaster.get().getIsActive() == ACTIVE) {
								getDescription(ALREADY_EXISTS.getDescription(), skill.getSkillName());
					} else {
						skillMaster.get().setIsActive(ACTIVE);
						skillMaster.get().setUpdatedBy(skill.getCreatedBy());
						SkillMaster savedSkill = skillMasterRepository.save(skillMaster.get());
						Map<String, Object> snapshot = new HashMap<>();
						snapshot.put("skillId", savedSkill.getSkillId());
						snapshot.put("skillName", savedSkill.getSkillName());
						snapshot.put("companyId", savedSkill.getCompany() != null ? savedSkill.getCompany().getCompanyId() : null);
						snapshot.put("isActive", savedSkill.getIsActive());
						snapshot.put("updatedBy", savedSkill.getUpdatedBy());
						String newValue = serialize(snapshot);
						auditService.recordAudit(mapper.toSystemAuditRequest("Skill", "Skill", "SkillMaster",
								savedSkill.getSkillId(),
								"CREATE",
								newValue,
								null,
								null,
								skill.getCreatedBy(),
								"SYSTEM",
								savedSkill.getCompany() != null ? savedSkill.getCompany().getCompanyId() : null,
								"SUCCESS",
								null));
						skillMasterVOs.add(mapper.toSkillMasterVo(savedSkill));
					}
				} else {
					SkillMaster newSkillMaster = SkillMaster.builder().skillName(skill.getSkillName()).company(company)
							.createdBy(skill.getCreatedBy())
							.isCreatorAdmin(skill.getIsCreatedByAdmin() != null ? skill.getIsCreatedByAdmin() : false)
							.build();
					SkillMaster savedSkill = skillMasterRepository.save(newSkillMaster);
					Map<String, Object> snapshot = new HashMap<>();
					snapshot.put("skillId", savedSkill.getSkillId());
					snapshot.put("skillName", savedSkill.getSkillName());
					snapshot.put("companyId", savedSkill.getCompany() != null ? savedSkill.getCompany().getCompanyId() : null);
					snapshot.put("isActive", savedSkill.getIsActive());
					snapshot.put("createdBy", savedSkill.getCreatedBy());
					String newValue = serialize(snapshot);
					auditService.recordAudit(mapper.toSystemAuditRequest("Skill", "Skill", "SkillMaster",
							savedSkill.getSkillId(),
							"CREATE",
							newValue,
							null,
							null,
							skill.getCreatedBy(),
							"SYSTEM",
							savedSkill.getCompany() != null ? savedSkill.getCompany().getCompanyId() : null,
							"SUCCESS",
							null));
					skillMasterVOs.add(mapper.toSkillMasterVo(savedSkill));
				}
			});

			return skillMasterVOs;
		} catch (FlickzzDeskException e) {
			auditService.recordExceptionAudit(mapper.toSystemAuditRequest("Skill", "Skill", "SkillMaster",
					null, "CREATE", null, null, null, null, "SYSTEM", null, "FAILED", e.getMessage()), e);
			throw e;
		} catch (Exception e) {
			log.error("Exception in createSkills method in SkillsService");
			auditService.recordExceptionAudit(mapper.toSystemAuditRequest("Skill", "Skill", "SkillMaster",
					null, "CREATE", null, null, null, null, "SYSTEM", null, "FAILED", e.getMessage()), e);
			throw new FlickzzDeskException(DEFAULT_ERROR_CODE);
		}
	}

	public SkillMasterVO getSkillInfo(String skillId) {
		log.info(generateLog("getSkillInfo", this.getClass().getName()));
		try {
			SkillMaster skillMaster = skillMasterRepository.findBySkillId(Long.valueOf(skillId))
					.orElseThrow(() -> new FlickzzDeskException(DOES_NOT_EXIST,
							getDescription(DOES_NOT_EXIST.getDescription(), SKILL)));
			return mapper.toSkillMasterVo(skillMaster);
		} catch (FlickzzDeskException e) {
			throw e;
		} catch (Exception e) {
			log.error("Exception in getSkillInfo method in SkillsService");
			throw new FlickzzDeskException(DEFAULT_ERROR_CODE);
		}
	}

	public SkillMasterVO updateSkill(SkillRequestVO request) {
		log.info(generateLog("updateSkill", this.getClass().getName()));
		try {
			SkillMaster skillMaster = skillMasterRepository.findBySkillId(request.getSkillId())
					.orElseThrow(() -> new FlickzzDeskException(DOES_NOT_EXIST,
							getDescription(DOES_NOT_EXIST.getDescription(), SKILL)));
			Map<String, Object> oldSnapshot = new HashMap<>();
			oldSnapshot.put("skillId", skillMaster.getSkillId());
			oldSnapshot.put("skillName", skillMaster.getSkillName());
			oldSnapshot.put("companyId", skillMaster.getCompany() != null ? skillMaster.getCompany().getCompanyId() : null);
			oldSnapshot.put("isActive", skillMaster.getIsActive());
			oldSnapshot.put("updatedBy", skillMaster.getUpdatedBy());
			skillMaster.setIsActive(ACTIVE);
			skillMaster.setUpdatedBy(request.getUpdatedBy());
			SkillMaster savedSkill = skillMasterRepository.save(skillMaster);
			Map<String, Object> newSnapshot = new HashMap<>();
			newSnapshot.put("skillId", savedSkill.getSkillId());
			newSnapshot.put("skillName", savedSkill.getSkillName());
			newSnapshot.put("companyId", savedSkill.getCompany() != null ? savedSkill.getCompany().getCompanyId() : null);
			newSnapshot.put("isActive", savedSkill.getIsActive());
			newSnapshot.put("updatedBy", savedSkill.getUpdatedBy());
			String oldValue = serialize(oldSnapshot);
			String newValue = serialize(newSnapshot);
			auditService.recordAudit(mapper.toSystemAuditRequest("Skill", "Skill", "SkillMaster",
					savedSkill.getSkillId(),
					"UPDATE",
					newValue,
					oldValue,
					null,
					request != null ? request.getUpdatedBy() : null,
					"SYSTEM",
					savedSkill.getCompany() != null ? savedSkill.getCompany().getCompanyId() : null,
					"SUCCESS",
					null));
			return mapper.toSkillMasterVo(savedSkill);
		} catch (FlickzzDeskException e) {
			auditService.recordExceptionAudit(mapper.toSystemAuditRequest("Skill", "Skill", "SkillMaster",
					null, "UPDATE", null, null, null, request != null ? request.getUpdatedBy() : null, "SYSTEM", null, "FAILED", e.getMessage()), e);
			throw e;
		} catch (Exception e) {
			log.error("Exception in updateSkill method in SkillsService");
			auditService.recordExceptionAudit(mapper.toSystemAuditRequest("Skill", "Skill", "SkillMaster",
					null, "UPDATE", null, null, null, request != null ? request.getUpdatedBy() : null, "SYSTEM", null, "FAILED", e.getMessage()), e);
			throw new FlickzzDeskException(DEFAULT_ERROR_CODE);
		}
	}

	public void deleteSkill(String skillId, Long userId) {
		log.info(generateLog("deleteSkill", this.getClass().getName()));
		try {
			SkillMaster skillMaster = skillMasterRepository.findBySkillId(Long.valueOf(skillId))
					.orElseThrow(() -> new FlickzzDeskException(DOES_NOT_EXIST,
							getDescription(DOES_NOT_EXIST.getDescription(), SKILL)));
			if(agentSkillsMappingRepository.existsBySkill_SkillIdAndIsActive(Long.valueOf(skillId), ACTIVE)) {
				log.info("Skill is assigned to an agent, cannot delete");
				throw new FlickzzDeskException(DB_SAVE_ERROR, "Please unassign the skill from all agent(s) before deleting.");
			}
			skillMaster.setIsActive(DEACTIVATE);
			SkillMaster savedSkill = skillMasterRepository.save(skillMaster);
			Map<String, Object> snapshot = new HashMap<>();
			snapshot.put("skillId", savedSkill.getSkillId());
			snapshot.put("skillName", savedSkill.getSkillName());
			snapshot.put("companyId", savedSkill.getCompany() != null ? savedSkill.getCompany().getCompanyId() : null);
			snapshot.put("isActive", savedSkill.getIsActive());
			snapshot.put("updatedBy", savedSkill.getUpdatedBy());
			String newValue = serialize(snapshot);
			auditService.recordAudit(mapper.toSystemAuditRequest("Skill", "Skill", "SkillMaster",
					savedSkill.getSkillId(),
					"DELETE",
					newValue,
					null,
					null,
					userId,
					"SYSTEM",
					savedSkill.getCompany() != null ? savedSkill.getCompany().getCompanyId() : null,
					"SUCCESS",
					null));
		} catch (FlickzzDeskException e) {
			auditService.recordExceptionAudit(mapper.toSystemAuditRequest("Skill", "Skill", "SkillMaster",
					null, "DELETE", null, null, null, userId, "SYSTEM", null, "FAILED", e.getMessage()), e);
			throw e;
		} catch (Exception e) {
			log.error("Exception in deleteSkill method in SkillsService");
			auditService.recordExceptionAudit(mapper.toSystemAuditRequest("Skill", "Skill", "SkillMaster",
					null, "DELETE", null, null, null, userId, "SYSTEM", null, "FAILED", e.getMessage()), e);
			throw new FlickzzDeskException(DEFAULT_ERROR_CODE);
		}
	}

	public List<SkillMasterVO> getSkillList(String orgId) {
		log.info(generateLog("getSkillList", this.getClass().getName()));
		try {
			return skillMasterRepository.findAllByCompany_CompanyId(Long.valueOf(orgId)).stream()
					.map(skill -> mapper.toSkillMasterVo(skill)).toList();
		} catch (Exception e) {
			log.error("Exception in getSkillList method in SkillsService");
			throw new FlickzzDeskException(DEFAULT_ERROR_CODE);
		}
	}

	private String serialize(Object value) {
		try {
			return value == null ? null : objectMapper.writeValueAsString(value);
		} catch (Exception e) {
			log.error("Exception while serializing skill audit payload", e);
			return null;
		}
	}

}
