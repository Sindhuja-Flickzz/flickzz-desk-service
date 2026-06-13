package com.flickzz.desk.service;

import static com.flickzz.desk.config.FlickzzDeskConstants.*;
import static com.flickzz.desk.config.FlickzzDeskUtility.*;
import static com.flickzz.desk.exception.FlickzzDeskErrorCodes.*;

import java.util.*;

import org.slf4j.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.security.crypto.password.*;
import org.springframework.stereotype.*;
import org.springframework.transaction.annotation.*;

import com.flickzz.desk.exception.*;
import com.flickzz.desk.mapper.*;
import com.flickzz.desk.model.*;
import com.flickzz.desk.repo.*;
import com.flickzz.desk.vo.*;

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
	private UserRepository userRepository;

	@Autowired
	private LoginMasterRepository loginMasterRepository;

	@Autowired
	private CountryMasterRepository countryMasterRepository;

	@Autowired
	private CityMasterRepository cityMasterRepository;

	@Autowired
	private LanguageMasterRepository languageMasterRepository;

	@Autowired
	private EnquiryRegistrationRepository enquiryRegistrationRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private CommonMapper mapper;

	@Autowired
	private MailService mailService;

	@Transactional
	public AgentMasterVO createAgent(AgentRequestVO request) {
		log.info(generateLog(ENTRY, this.getClass().getName()));
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

			if (request == null || request.getPhoneNumber() == null) {
				throw new FlickzzDeskException(INVALID_FIELD, getDescription(INVALID_FIELD.getDescription(), PHONE));
			}

			Optional<User> user = userRepository.findByUserName(request.getMailId());
			user.ifPresent(agent -> {
				throw new FlickzzDeskException(ALREADY_EXISTS,
						getDescription(ALREADY_EXISTS.getDescription(), request.getMailId()));
			});

			Optional<EnquiryRegistration> enquiryRegistration = enquiryRegistrationRepository
					.findByEmailAndIsActive(request.getMailId(), ACTIVE);

			Optional<CompanyMaster> company = companyMasterRepository.findByCompanyIdAndIsActive(request.getOrgId(),
					ACTIVE);
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

			CountryMaster country = countryMasterRepository.findById(request.getCountryId())
					.orElseThrow(() -> new FlickzzDeskException(DOES_NOT_EXIST,
							getDescription(DOES_NOT_EXIST.getDescription(), COUNTRY)));

			CityMaster city = cityMasterRepository.findById(request.getCityId())
					.orElseThrow(() -> new FlickzzDeskException(DOES_NOT_EXIST,
							getDescription(DOES_NOT_EXIST.getDescription(), CITY)));

			LanguageMaster language = languageMasterRepository.findById(request.getLanguageId())
					.orElseThrow(() -> new FlickzzDeskException(DOES_NOT_EXIST,
							getDescription(DOES_NOT_EXIST.getDescription(), LANGUAGE)));

			String rawPassword = generateTemporaryPassword();

			String userRole = enquiryRegistration.isPresent() ? ROLE_ADMIN_AGENT : ROLE_AGENT;

			User newUser = User.builder().firstName(request.getAgentName()).email(request.getMailId())
					.userName(request.getMailId()).email(request.getMailId()).registerId(request.getAccessId())
					.phoneCode(request.getPhoneCode()).phoneNumber(request.getPhoneNumber()).country(country).city(city)
					.language(language).createdBy(request.getCreatedBy()).password(passwordEncoder.encode(rawPassword))
					.role(userRole).mfaEnabled(false).build();
			userRepository.save(newUser);

			LoginMaster loginMaster = mapper.userToLoginMaster(newUser);
			loginMasterRepository.save(loginMaster);

			AgentMaster agent = AgentMaster.builder().agentName(request.getAgentName()).mailId(request.getMailId())
					.accessId(request.getAccessId()).organization(company.get()).calendarMaster(calendar.get())
					.createdBy(request.getCreatedBy()).user(newUser).build();
			AgentMaster agentMaster = agentMasterRepository.save(agent);

			enquiryRegistration.ifPresent(enquiry -> {
				enquiry.setIsActive(false);
				enquiryRegistrationRepository.save(enquiry);
			});

			request.getSkills().stream().forEach(skillInfo -> {
				Optional<SkillMaster> skill = skillMasterRepository.findById(skillInfo.getSkillId());
				AgentSkillsMapping agentSkill = AgentSkillsMapping.builder().agent(agentMaster).skill(skill.get())
						.experienceYears(skillInfo.getExperienceYears())
						.experienceMonths(skillInfo.getExperienceMonths()).createdBy(request.getCreatedBy()).build();
				agentSkillsMappingRepository.save(agentSkill);
			});

			mailService.sendTemporaryPasswordEmail(newUser.getEmail(), newUser.getFirstName(), rawPassword);

			return mapper.toAgentMasterVO(agentMaster);
		} catch (FlickzzDeskException e) {
			throw e;
		} catch (Exception e) {
			log.error("Exception in createAgent method in FlickzzDeskService");
			throw new FlickzzDeskException(DEFAULT_ERROR_CODE);
		}
	}

	public AgentMasterVO getAgentInfo(String agentId) {
		log.info(generateLog(ENTRY, this.getClass().getName()));
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
		log.info(generateLog(ENTRY, this.getClass().getName()));
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
		log.info(generateLog(ENTRY, this.getClass().getName()));
		try {
			Optional<AgentMaster> existing = agentMasterRepository.findById(request.getAgentId());
			if (existing == null) {
				throw new FlickzzDeskException(DOES_NOT_EXIST, getDescription(DOES_NOT_EXIST.getDescription(), AGENT));
			}

			request.getSkills().stream().forEach(skillInfo -> {
				Optional<SkillMaster> skill = skillMasterRepository.findById(skillInfo.getSkillId());
				if (skill == null) {
					throw new FlickzzDeskException(DOES_NOT_EXIST,
							getDescription(DOES_NOT_EXIST.getDescription(), SKILL));
				}
			});

			existing.get().getAgentSkillsMappings().clear();

			Optional<CalendarMaster> calendar = calendarMasterRepository.findById(request.getCalendarId());
			if (calendar == null) {
				throw new FlickzzDeskException(DOES_NOT_EXIST,
						getDescription(DOES_NOT_EXIST.getDescription(), CALENDAR));
			}

			CountryMaster country = countryMasterRepository.findById(request.getCountryId())
					.orElseThrow(() -> new FlickzzDeskException(DOES_NOT_EXIST,
							getDescription(DOES_NOT_EXIST.getDescription(), COUNTRY)));

			CityMaster city = cityMasterRepository.findById(request.getCityId())
					.orElseThrow(() -> new FlickzzDeskException(DOES_NOT_EXIST,
							getDescription(DOES_NOT_EXIST.getDescription(), CITY)));

			LanguageMaster language = languageMasterRepository.findById(request.getLanguageId())
					.orElseThrow(() -> new FlickzzDeskException(DOES_NOT_EXIST,
							getDescription(DOES_NOT_EXIST.getDescription(), LANGUAGE)));

			AgentMaster agent = existing.get();
			agent.setAgentName(request.getAgentName());
			agent.setCalendarMaster(calendar.get());
			agent.setUpdatedBy(request.getUpdatedBy());
			agentMasterRepository.save(agent);

			User user = existing.get().getUser();
			user.setCountry(country);
			user.setCity(city);
			user.setPhoneCode(request.getPhoneCode());
			user.setPhoneNumber(request.getPhoneNumber());
			user.setLanguage(language);
			user.setUpdatedBy(request.getUpdatedBy());
			userRepository.save(user);

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
		log.info(generateLog(ENTRY, this.getClass().getName()));
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

	public List<AgentMasterVO> getAgentList(String orgId) {
		log.info(generateLog(ENTRY, this.getClass().getName()));
		try {
			return agentMasterRepository.findAllByOrganization_CompanyId(Long.valueOf(orgId)).stream()
					.map(mapper::toAgentMasterVO).toList();
		} catch (Exception e) {
			log.error("Exception in getPlantList method in FlickzzDeskService");
			throw new FlickzzDeskException(DEFAULT_ERROR_CODE);
		}
	}

	public List<AgentSkillsMappingVO> getAgentSkills(String agentId) {
		log.info(generateLog(ENTRY, this.getClass().getName()));
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

	public AgentMasterVO getAgentInfoByEmail(String agentName) {
		log.info(generateLog(ENTRY, this.getClass().getName()));
		try {
			AgentMaster agentMaster = agentMasterRepository.findByMailId(agentName)
					.orElseThrow(() -> new FlickzzDeskException(DOES_NOT_EXIST,
							getDescription(DOES_NOT_EXIST.getDescription(), AGENT)));
			return new AgentMasterVO();
		} catch (FlickzzDeskException e) {
			throw e;
		} catch (Exception e) {
			log.error("Exception in getAgentInfo method in FlickzzDeskService");
			throw new FlickzzDeskException(DEFAULT_ERROR_CODE);
		}
	}
}
