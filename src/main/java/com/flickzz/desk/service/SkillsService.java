package com.flickzz.desk.service;

import static com.flickzz.desk.config.FlickzzDeskConstants.*;
import static com.flickzz.desk.config.FlickzzDeskUtility.*;
import static com.flickzz.desk.exception.FlickzzDeskErrorCodes.*;

import java.util.*;

import org.slf4j.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.*;

import com.flickzz.desk.exception.*;
import com.flickzz.desk.mapper.*;
import com.flickzz.desk.model.*;
import com.flickzz.desk.repo.*;
import com.flickzz.desk.vo.*;

@Service
public class SkillsService {

	private static final Logger log = LoggerFactory.getLogger(SkillsService.class);

	@Autowired
	SkillMasterRepository skillMasterRepository;

	@Autowired
	CompanyMasterRepository companyMasterRepository;

	@Autowired
	CommonMapper mapper;

	public List<SkillMasterVO> createSkills(List<SkillRequestVO> skills) {
		log.info(generateLog("createSkills", this.getClass().getName()));
		try {
			if (skills.size() == 0) {
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

				skillMasterRepository.findBySkillNameAndCompany_CompanyIdAndIsActive(skill.getSkillName(),
						skill.getCompanyId(), ACTIVE).ifPresent(s -> {
							throw new FlickzzDeskException(ALREADY_EXISTS,
									getDescription(ALREADY_EXISTS.getDescription(), skill.getSkillName()));
						});

				SkillMaster skillMaster = SkillMaster.builder().skillName(skill.getSkillName()).company(company)
						.createdBy(skill.getCreatedBy())
						.isCreatorAdmin(skill.getIsCreatedByAdmin() != null ? skill.getIsCreatedByAdmin() : false)
						.build();
				skillMasterVOs.add(mapper.toSkillMasterVo(skillMasterRepository.save(skillMaster)));

			});

			return skillMasterVOs;
		} catch (FlickzzDeskException e) {
			throw e;
		} catch (Exception e) {
			log.error("Exception in createSkills method in SkillsService");
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
			skillMaster.setIsActive(ACTIVE);
			skillMaster.setUpdatedBy(request.getUpdatedBy());
			return mapper.toSkillMasterVo(skillMasterRepository.save(skillMaster));
		} catch (FlickzzDeskException e) {
			throw e;
		} catch (Exception e) {
			log.error("Exception in updateSkill method in SkillsService");
			throw new FlickzzDeskException(DEFAULT_ERROR_CODE);
		}
	}

	public void deleteSkill(String skillId) {
		log.info(generateLog("deleteSkill", this.getClass().getName()));
		try {
			SkillMaster skillMaster = skillMasterRepository.findBySkillId(Long.valueOf(skillId))
					.orElseThrow(() -> new FlickzzDeskException(DOES_NOT_EXIST,
							getDescription(DOES_NOT_EXIST.getDescription(), SKILL)));
			skillMaster.setIsActive(false);
			skillMasterRepository.save(skillMaster);
		} catch (FlickzzDeskException e) {
			throw e;
		} catch (Exception e) {
			log.error("Exception in deleteSkill method in SkillsService");
			throw new FlickzzDeskException(DEFAULT_ERROR_CODE);
		}
	}

	public List<SkillMasterVO> getSkillList(String orgId) {
		log.info(generateLog("getSkillList", this.getClass().getName()));
		try {
			return skillMasterRepository.findAllByCompany_CompanyIdAndIsActive(Long.valueOf(orgId), ACTIVE).stream()
					.map(skill -> mapper.toSkillMasterVo(skill)).toList();
		} catch (Exception e) {
			log.error("Exception in getSkillList method in SkillsService");
			throw new FlickzzDeskException(DEFAULT_ERROR_CODE);
		}
	}

}
