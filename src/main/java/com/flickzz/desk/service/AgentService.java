package com.flickzz.desk.service;

import static com.flickzz.desk.config.FlickzzDeskConstants.ACCESS_ID;
import static com.flickzz.desk.config.FlickzzDeskConstants.AGENT;
import static com.flickzz.desk.config.FlickzzDeskConstants.AGENT_NAME;
import static com.flickzz.desk.config.FlickzzDeskConstants.CALENDAR;
import static com.flickzz.desk.config.FlickzzDeskConstants.COMPANY;
import static com.flickzz.desk.config.FlickzzDeskConstants.ENTRY;
import static com.flickzz.desk.config.FlickzzDeskConstants.MAIL_ID;
import static com.flickzz.desk.config.FlickzzDeskConstants.PHONE;
import static com.flickzz.desk.config.FlickzzDeskConstants.SKILL;
import static com.flickzz.desk.config.FlickzzDeskUtility.generateLog;
import static com.flickzz.desk.config.FlickzzDeskUtility.getDescription;
import static com.flickzz.desk.exception.FlickzzDeskErrorCodes.ALREADY_EXISTS;
import static com.flickzz.desk.exception.FlickzzDeskErrorCodes.DEFAULT_ERROR_CODE;
import static com.flickzz.desk.exception.FlickzzDeskErrorCodes.DOES_NOT_EXIST;
import static com.flickzz.desk.exception.FlickzzDeskErrorCodes.INVALID_FIELD;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.flickzz.desk.exception.FlickzzDeskException;
import com.flickzz.desk.mapper.CommonMapper;
import com.flickzz.desk.model.AgentMaster;
import com.flickzz.desk.model.AgentSkillsMapping;
import com.flickzz.desk.model.CalendarMaster;
import com.flickzz.desk.model.CompanyMaster;
import com.flickzz.desk.model.SkillMaster;
import com.flickzz.desk.repo.AgentMasterRepository;
import com.flickzz.desk.repo.AgentSkillsMappingRepository;
import com.flickzz.desk.repo.CalendarMasterRepository;
import com.flickzz.desk.repo.CompanyMasterRepository;
import com.flickzz.desk.repo.SkillMasterRepository;
import com.flickzz.desk.vo.AgentMasterVO;
import com.flickzz.desk.vo.AgentRequestVO;
import com.flickzz.desk.vo.AgentSkillsMappingVO;

@Service
public class AgentService {

	private static final Logger log = LoggerFactory.getLogger(AgentService.class);

	@Autowired
	private CompanyMasterRepository companyMasterRepository;

	@Autowired
	private SkillMasterRepository skillMasterRepository;

	@Autowired
	private CalendarMasterRepository calendarMasterRepository;

	@Autowired
	private AgentMasterRepository agentMasterRepository;

	@Autowired
	private AgentSkillsMappingRepository agentSkillsMappingRepository;

	@Autowired
	private CommonMapper mapper;

	public AgentMasterVO createAgent(AgentRequestVO request) {
		log.debug(generateLog(ENTRY, this.getClass().getName()));
		try {
			if (request == null || request.getAgentName() == null) {
				throw new FlickzzDeskException(INVALID_FIELD,
						getDescription(INVALID_FIELD.getDescription(), AGENT_NAME));
			}

			if (request == null || request.getMailId() == null) {
				throw new FlickzzDeskException(INVALID_FIELD, getDescription(INVALID_FIELD.getDescription(), MAIL_ID));
			}

			if (request == null || request.getAccessId() == null) {
				throw new FlickzzDeskException(INVALID_FIELD,
						getDescription(INVALID_FIELD.getDescription(), ACCESS_ID));
			}

			if (request == null || request.getPhone() == null) {
				throw new FlickzzDeskException(INVALID_FIELD, getDescription(INVALID_FIELD.getDescription(), PHONE));
			}

			Optional<CompanyMaster> company = companyMasterRepository.findById(request.getOrgId());
			if (company == null) {
				throw new FlickzzDeskException(DOES_NOT_EXIST,
						getDescription(DOES_NOT_EXIST.getDescription(), COMPANY));
			}

			request.getSkills().stream().forEach(skillInfo -> {
				Optional<SkillMaster> skill = skillMasterRepository.findById(skillInfo.getSkillId());
				if (skill == null) {
					throw new FlickzzDeskException(DOES_NOT_EXIST,
							getDescription(DOES_NOT_EXIST.getDescription(), (SKILL + " " + skillInfo.getSkillName())));
				}
			});

			Optional<CalendarMaster> calendar = calendarMasterRepository.findById(request.getCalendarId());
			if (calendar == null) {
				throw new FlickzzDeskException(DOES_NOT_EXIST,
						getDescription(DOES_NOT_EXIST.getDescription(), CALENDAR));
			}

			AgentMaster agent = AgentMaster.builder().agentName(request.getAgentName()).mailId(request.getMailId())
					.accessId(request.getAccessId()).phone(request.getPhone()).organization(company.get())
					.calendarMaster(calendar.get()).createdBy(request.getCreatedBy()).build();
			AgentMaster agentMaster = agentMasterRepository.save(agent);

			request.getSkills().stream().forEach(skillInfo -> {
				Optional<SkillMaster> skill = skillMasterRepository.findById(skillInfo.getSkillId());
				AgentSkillsMapping agentSkill = AgentSkillsMapping.builder().agent(agentMaster).skill(skill.get())
						.experienceYears(skillInfo.getExperienceYears())
						.experienceMonths(skillInfo.getExperienceMonths()).createdBy(request.getCreatedBy()).build();
				agentSkillsMappingRepository.save(agentSkill);
			});

			return mapper.toAgentMasterVO(agentMasterRepository.save(agent));
		} catch (FlickzzDeskException e) {
			throw e;
		} catch (Exception e) {
			log.error("Exception in createAgent method in FlickzzDeskService");
			throw new FlickzzDeskException(DEFAULT_ERROR_CODE);
		}
	}

	public AgentMasterVO getAgentInfo(String agentId) {
		log.debug(generateLog(ENTRY, this.getClass().getName()));
		try {
			Optional<AgentMaster> agentMaster = agentMasterRepository.findById(Long.valueOf(agentId));
			if (agentMaster == null) {
				throw new FlickzzDeskException(DOES_NOT_EXIST, getDescription(DOES_NOT_EXIST.getDescription(), AGENT));
			}
			return mapper.toAgentMasterVO(agentMaster.get());
		} catch (FlickzzDeskException e) {
			throw e;
		} catch (Exception e) {
			log.error("Exception in getAgentInfo method in FlickzzDeskService");
			throw new FlickzzDeskException(DEFAULT_ERROR_CODE);
		}
	}

	public AgentMasterVO getAgentInfoByName(String agentName) {
		log.debug(generateLog(ENTRY, this.getClass().getName()));
		try {
			Optional<AgentMaster> agentMaster = agentMasterRepository.findByAgentName(agentName);
			agentMaster.ifPresent(agent -> {
				throw new FlickzzDeskException(ALREADY_EXISTS, getDescription(ALREADY_EXISTS.getDescription(), AGENT));
			});
			return new AgentMasterVO();
		} catch (FlickzzDeskException e) {
			throw e;
		} catch (Exception e) {
			log.error("Exception in getAgentInfo method in FlickzzDeskService");
			throw new FlickzzDeskException(DEFAULT_ERROR_CODE);
		}
	}

	public AgentMasterVO updateAgent(AgentRequestVO request) {
		log.debug(generateLog(ENTRY, this.getClass().getName()));
		try {
			Optional<AgentMaster> existing = agentMasterRepository.findById(request.getAgentId());
			if (existing == null) {
				throw new FlickzzDeskException(DOES_NOT_EXIST, getDescription(DOES_NOT_EXIST.getDescription(), AGENT));
			}

			if (request == null || request.getPhone() == null) {
				throw new FlickzzDeskException(INVALID_FIELD, getDescription(INVALID_FIELD.getDescription(), PHONE));
			}

			Optional<CompanyMaster> company = companyMasterRepository.findById(request.getOrgId());
			if (company == null) {
				throw new FlickzzDeskException(DOES_NOT_EXIST,
						getDescription(DOES_NOT_EXIST.getDescription(), COMPANY));
			}

			request.getSkills().stream().forEach(skillInfo -> {
				Optional<SkillMaster> skill = skillMasterRepository.findById(skillInfo.getSkillId());
				if (skill == null) {
					throw new FlickzzDeskException(DOES_NOT_EXIST,
							getDescription(DOES_NOT_EXIST.getDescription(), SKILL));
				}
			});

			existing.get().getAgentSkillsMappings().clear();
//			List<SkillMaster> skills = skillMasterRepository.findAllById(request.getSkills());

			Optional<CalendarMaster> calendar = calendarMasterRepository.findById(request.getCalendarId());
			if (calendar == null) {
				throw new FlickzzDeskException(DOES_NOT_EXIST,
						getDescription(DOES_NOT_EXIST.getDescription(), CALENDAR));
			}

			AgentMaster agent = existing.get();
			agent.setPhone(request.getPhone());
			agent.setOrganization(company.get());
			agent.setCalendarMaster(calendar.get());
			agent.setUpdatedBy(request.getUpdatedBy());
			agentMasterRepository.save(agent);

			request.getSkills().stream().forEach(skillInfo -> {
				Optional<SkillMaster> skill = skillMasterRepository.findById(skillInfo.getSkillId());
				AgentSkillsMapping agentSkill = AgentSkillsMapping.builder().agent(existing.get()).skill(skill.get())
						.experienceYears(skillInfo.getExperienceYears())
						.experienceMonths(skillInfo.getExperienceMonths()).createdBy(request.getCreatedBy()).build();
				agentSkillsMappingRepository.save(agentSkill);
			});

			return mapper.toAgentMasterVO(agentMasterRepository.save(existing.get()));
		} catch (FlickzzDeskException e) {
			throw e;
		} catch (Exception e) {
			log.error("Exception in updateAgent method in FlickzzDeskService");
			throw new FlickzzDeskException(DEFAULT_ERROR_CODE);
		}
	}

	public void deleteAgent(String agentId) {
		log.debug(generateLog(ENTRY, this.getClass().getName()));
		try {
			Optional<AgentMaster> existing = agentMasterRepository.findById(Long.valueOf(agentId));
			if (existing == null) {
				throw new FlickzzDeskException(DOES_NOT_EXIST, getDescription(DOES_NOT_EXIST.getDescription(), AGENT));
			}
			agentMasterRepository.delete(existing.get());
		} catch (FlickzzDeskException e) {
			throw e;
		} catch (Exception e) {
			log.error("Exception in deleteAgent method in FlickzzDeskService");
			throw new FlickzzDeskException(DEFAULT_ERROR_CODE);
		}
	}

	public List<AgentMasterVO> getAgentList() {
		log.debug(generateLog(ENTRY, this.getClass().getName()));
		try {
			return agentMasterRepository.findAll().stream().map(mapper::toAgentMasterVO).toList();
		} catch (Exception e) {
			log.error("Exception in getPlantList method in FlickzzDeskService");
			throw new FlickzzDeskException(DEFAULT_ERROR_CODE);
		}
	}

	public List<AgentSkillsMappingVO> getAgentSkills(String agentId) {
		log.debug(generateLog(ENTRY, this.getClass().getName()));
		try {
			List<AgentSkillsMapping> agentMaster = agentSkillsMappingRepository
					.findByAgentAgentId(Long.valueOf(agentId));
			if (agentMaster == null) {
				return List.of();
			}
			return agentMaster.stream().map(mapper::toAgentSkillMappingVo).toList();
		} catch (FlickzzDeskException e) {
			throw e;
		} catch (Exception e) {
			log.error("Exception in getAgentSkills method in FlickzzDeskService");
			throw new FlickzzDeskException(DEFAULT_ERROR_CODE);
		}
	}
}
