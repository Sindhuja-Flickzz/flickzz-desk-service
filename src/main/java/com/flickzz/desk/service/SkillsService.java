package com.flickzz.desk.service;

import static com.flickzz.desk.config.FlickzzDeskConstants.ENTRY;
import static com.flickzz.desk.config.FlickzzDeskConstants.SKILL;
import static com.flickzz.desk.config.FlickzzDeskConstants.SKILL_NAME;
import static com.flickzz.desk.config.FlickzzDeskUtility.generateLog;
import static com.flickzz.desk.config.FlickzzDeskUtility.getDescription;
import static com.flickzz.desk.exception.FlickzzDeskErrorCodes.*;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.flickzz.desk.exception.FlickzzDeskException;
import com.flickzz.desk.mapper.CommonMapper;
import com.flickzz.desk.model.SkillMaster;
import com.flickzz.desk.repo.SkillMasterRepository;
import com.flickzz.desk.vo.SkillMasterVO;
import com.flickzz.desk.vo.SkillRequestVO;

@Service
public class SkillsService {

	private static final Logger log = LoggerFactory.getLogger(SkillsService.class);
	
	@Autowired
	SkillMasterRepository skillMasterRepository;
	
	@Autowired
	CommonMapper mapper;
	
	public List<SkillMasterVO> createSkills(List<SkillRequestVO> skills) {
		log.debug(generateLog(ENTRY, this.getClass().getName()));
		try {			
			if (skills.size() == 0) {
				throw new FlickzzDeskException(NO_DATA, NO_DATA.getDescription());
			}
			
			List<SkillMasterVO> skillMasterVOs = new ArrayList<SkillMasterVO>();
			
			skills.forEach(skill -> {
				if (skill.getSkillName() == null) {
					throw new FlickzzDeskException(INVALID_FIELD,
							getDescription(INVALID_FIELD.getDescription(), SKILL_NAME));
				}
				
				skillMasterRepository.findBySkillName(skill.getSkillName()).ifPresent(s -> {
					throw new FlickzzDeskException(ALREADY_EXISTS,
							getDescription(ALREADY_EXISTS.getDescription(), skill.getSkillName()));
				});
				
				SkillMaster skillMaster = SkillMaster.builder()
						.skillName(skill.getSkillName())
						.createdBy(skill.getCreatedBy())
						.build();
				skillMasterVOs.add(mapper.toSkillMasterVo(skillMasterRepository.save(skillMaster)));
				
			});
			
			return skillMasterVOs;
		} catch (FlickzzDeskException e) {
			throw e;
		} catch (Exception e) {
			log.error("Exception in createCompany method in FlickzzDeskService");
			throw new FlickzzDeskException(DEFAULT_ERROR_CODE);
		}
	}

	public SkillMasterVO getSkillInfo(String skillId) {
		log.debug(generateLog(ENTRY, this.getClass().getName()));
		try {
			SkillMaster skillMaster = skillMasterRepository.findBySkillId(Long.valueOf(skillId)).orElseThrow(
					() -> new FlickzzDeskException(DOES_NOT_EXIST, getDescription(DOES_NOT_EXIST.getDescription(), SKILL)));
			return mapper.toSkillMasterVo(skillMaster);
		} catch (FlickzzDeskException e) {
			throw e;
		} catch (Exception e) {
			log.error("Exception in getCompanyInfo method in FlickzzDeskService");
			throw new FlickzzDeskException(DEFAULT_ERROR_CODE);
		}
	}

	public SkillMasterVO updateSkill(SkillRequestVO request) {
		log.debug(generateLog(ENTRY, this.getClass().getName()));
		try {
			SkillMaster skillMaster = skillMasterRepository.findBySkillId(request.getSkillId()).orElseThrow(
					() -> new FlickzzDeskException(DOES_NOT_EXIST, getDescription(DOES_NOT_EXIST.getDescription(), SKILL)));
			skillMaster.setSkillName(request.getSkillName());
			skillMaster.setUpdatedBy(request.getUpdatedBy());
			return mapper.toSkillMasterVo(skillMasterRepository.save(skillMaster));			
		} catch (FlickzzDeskException e) {
			throw e;
		} catch (Exception e) {
			log.error("Exception in getCompanyInfo method in FlickzzDeskService");
			throw new FlickzzDeskException(DEFAULT_ERROR_CODE);
		}
	}

	public void deleteSkill(String skillId) {
		log.debug(generateLog(ENTRY, this.getClass().getName()));
		try {
			SkillMaster skillMaster = skillMasterRepository.findBySkillId(Long.valueOf(skillId))
					.orElseThrow(() -> new FlickzzDeskException(DOES_NOT_EXIST,
							getDescription(DOES_NOT_EXIST.getDescription(), SKILL)));
			skillMasterRepository.delete(skillMaster);
		} catch (FlickzzDeskException e) {
			throw e;
		} catch (Exception e) {
			log.error("Exception in deleteCompany method in FlickzzDeskService");
			throw new FlickzzDeskException(DEFAULT_ERROR_CODE);
		}
	}

	public List<SkillMasterVO> getSkillList() {
		log.debug(generateLog(ENTRY, this.getClass().getName()));
		try {
			return skillMasterRepository.findAll().stream().map(skill -> mapper.toSkillMasterVo(skill)).toList() ;
		} catch (Exception e) {
			log.error("Exception in getCompanyList method in FlickzzDeskService");
			throw new FlickzzDeskException(DEFAULT_ERROR_CODE);
		}
	}

}
