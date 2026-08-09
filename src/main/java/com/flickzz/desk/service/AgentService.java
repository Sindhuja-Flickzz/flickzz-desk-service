package com.flickzz.desk.service;

import static com.flickzz.desk.config.FlickzzDeskConstants.*;
import static com.flickzz.desk.config.FlickzzDeskUtility.*;
import static com.flickzz.desk.exception.FlickzzDeskErrorCodes.*;

import java.util.*;
import java.util.stream.Collectors;

import com.flickzz.desk.vo.request.AgentRequestVO;
import com.flickzz.desk.vo.request.AgentSkillRequestVO;
import org.slf4j.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.*;
import org.springframework.stereotype.*;
import org.springframework.transaction.annotation.*;
import org.springframework.util.StringUtils;

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
	private UserLanguageMappingRepository userLanguageMappingRepository;

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
			if (request == null) {
				throw new FlickzzDeskException(INVALID_FIELD,
						getDescription(INVALID_FIELD.getDescription(), "request"));
			}

			if (!StringUtils.hasText(request.getAgentName())) {
				throw new FlickzzDeskException(INVALID_FIELD,
						getDescription(INVALID_FIELD.getDescription(), AGENT_NAME));
			}

			if (!StringUtils.hasText(request.getMailId())) {
				throw new FlickzzDeskException(INVALID_FIELD, getDescription(INVALID_FIELD.getDescription(), MAIL_ID));
			}

			if (!StringUtils.hasText(request.getAccessId())) {
				throw new FlickzzDeskException(INVALID_FIELD,
						getDescription(INVALID_FIELD.getDescription(), ACCESS_ID));
			}

			if (!StringUtils.hasText(request.getPhoneNumber())) {
				throw new FlickzzDeskException(INVALID_FIELD, getDescription(INVALID_FIELD.getDescription(), PHONE));
			}

			if (userRepository.existsByUserName(request.getMailId())) {
				throw new FlickzzDeskException(ALREADY_EXISTS,
						getDescription(ALREADY_EXISTS.getDescription(), request.getMailId()));
			}

			Optional<EnquiryRegistration> enquiryRegistration = enquiryRegistrationRepository
					.findTopByEmailAndIsActiveTrueOrderByVersionDesc(request.getMailId());

			Optional<CompanyMaster> company = companyMasterRepository.findByCompanyIdAndIsActive(request.getOrgId(),
					ACTIVE);
			if (!company.isPresent()) {
				throw new FlickzzDeskException(DOES_NOT_EXIST,
						getDescription(DOES_NOT_EXIST.getDescription(), COMPANY));
			}

			Set<Long> skillIds = request.getSkills().stream().map(AgentSkillRequestVO::getSkillId)
					.collect(Collectors.toSet());
			List<SkillMaster> skillMasters = skillMasterRepository.findAllById(skillIds);
			Map<Long, SkillMaster> skillMap = skillMasters.stream()
					.collect(Collectors.toMap(SkillMaster::getSkillId, skill -> skill));

			for (AgentSkillRequestVO skillInfo : request.getSkills()) {
				if (!skillMap.containsKey(skillInfo.getSkillId())) {
					throw new FlickzzDeskException(DOES_NOT_EXIST,
						getDescription(DOES_NOT_EXIST.getDescription(), (SKILL + " " + skillInfo.getSkillName())));
				}
			}

			Optional<CalendarMaster> calendar = calendarMasterRepository.findById(request.getCalendarId());
			if (!calendar.isPresent()) {
				throw new FlickzzDeskException(DOES_NOT_EXIST,
						getDescription(DOES_NOT_EXIST.getDescription(), CALENDAR));
			}

			CountryMaster country = countryMasterRepository.findByCountryIdAndIsActiveTrue(request.getCountryId())
					.orElseThrow(() -> new FlickzzDeskException(DOES_NOT_EXIST,
							getDescription(DOES_NOT_EXIST.getDescription(), COUNTRY)));

			CityMaster city = cityMasterRepository.findByCityIdAndIsActiveTrue(request.getCityId())
					.orElseThrow(() -> new FlickzzDeskException(DOES_NOT_EXIST,
							getDescription(DOES_NOT_EXIST.getDescription(), CITY)));

//			LanguageMaster language = languageMasterRepository.findByLanguageIdAndIsActiveTrue(request.getLanguageIds())
//					.orElseThrow(() -> new FlickzzDeskException(DOES_NOT_EXIST,
//							getDescription(DOES_NOT_EXIST.getDescription(), LANGUAGE)));

			String rawPassword = generateTemporaryPassword();

			String userRole = enquiryRegistration.isPresent() ? ROLE_ADMIN_AGENT : ROLE_AGENT;

			User newUser = User.builder().firstName(request.getAgentName()).email(request.getMailId())
					.userName(request.getMailId()).email(request.getMailId()).registerId(request.getAccessId())
					.phoneCode(request.getPhoneCode()).phoneNumber(request.getPhoneNumber()).country(country).city(city)
					.createdBy(request.getCreatedBy()).isCreatorAdmin(request.getIsCreatedByAdmin())
					.password(passwordEncoder.encode(rawPassword)).role(userRole).mfaEnabled(false).build();
			userRepository.save(newUser);

			LoginMaster loginMaster = mapper.userToLoginMaster(newUser);
			loginMasterRepository.save(loginMaster);

			// persist user language mappings if provided
			List<Long> languageIds = request.getLanguageIds();
			if ((languageIds == null || languageIds.isEmpty()) && request.getLanguageIds() != null) {
				languageIds = request.getLanguageIds();
			}
			if (languageIds != null && !languageIds.isEmpty()) {
				List<LanguageMaster> langs = languageMasterRepository.findAllById(languageIds);
				Map<Long, LanguageMaster> langMap = langs.stream()
					.filter(l -> Boolean.TRUE.equals(l.getIsActive()))
					.collect(Collectors.toMap(LanguageMaster::getLanguageId, l -> l));
				for (Long lid : languageIds) {
					if (!langMap.containsKey(lid)) {
						throw new FlickzzDeskException(DOES_NOT_EXIST,
							getDescription(DOES_NOT_EXIST.getDescription(), LANGUAGE));
					}
				}
				List<UserLanguageMapping> userLangs = languageIds.stream().map(lid ->
					UserLanguageMapping.builder().user(newUser).language(langMap.get(lid))
						.createdBy(request.getCreatedBy()).isCreatorAdmin(request.getIsCreatedByAdmin()).build()
				).collect(Collectors.toList());
				userLanguageMappingRepository.saveAll(userLangs);
			}

			AgentMaster agent = AgentMaster.builder().agentName(request.getAgentName()).mailId(request.getMailId())
					.accessId(request.getAccessId()).organization(company.get()).calendarMaster(calendar.get())
					.createdBy(request.getCreatedBy()).isCreatorAdmin(request.getIsCreatedByAdmin()).user(newUser)
					.build();
			AgentMaster agentMaster = agentMasterRepository.save(agent);

			enquiryRegistration.ifPresent(enquiry -> {
				enquiry.setIsActive(false);
				enquiryRegistrationRepository.save(enquiry);
			});

			List<AgentSkillsMapping> agentSkills = request.getSkills().stream().map(skillInfo -> {
				SkillMaster skill = skillMap.get(skillInfo.getSkillId());
				return AgentSkillsMapping.builder().agent(agentMaster).skill(skill)
						.experienceYears(skillInfo.getExperienceYears())
						.experienceMonths(skillInfo.getExperienceMonths()).createdBy(request.getCreatedBy())
						.isCreatorAdmin(request.getIsCreatedByAdmin()).build();
			}).collect(Collectors.toList());

			agentSkillsMappingRepository.saveAll(agentSkills);

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
			Optional<AgentMaster> agentMaster = agentMasterRepository.findByAgentNameAndIsActiveTrue(agentName);
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
			if (request == null || request.getAgentId() == null) {
				throw new FlickzzDeskException(INVALID_FIELD,
						getDescription(INVALID_FIELD.getDescription(), AGENT));
			}

			Optional<AgentMaster> existing = agentMasterRepository.findById(request.getAgentId());
			if (existing.isEmpty()) {
				throw new FlickzzDeskException(DOES_NOT_EXIST,
						getDescription(DOES_NOT_EXIST.getDescription(), AGENT));
			}

			Set<Long> skillIds = request.getSkills().stream().map(AgentSkillRequestVO::getSkillId)
					.collect(Collectors.toSet());
			List<SkillMaster> skillMasters = skillMasterRepository.findAllById(skillIds);
			Map<Long, SkillMaster> skillMap = skillMasters.stream()
					.collect(Collectors.toMap(SkillMaster::getSkillId, skill -> skill));

			for (AgentSkillRequestVO skillInfo : request.getSkills()) {
				if (!skillMap.containsKey(skillInfo.getSkillId())) {
					throw new FlickzzDeskException(DOES_NOT_EXIST,
						getDescription(DOES_NOT_EXIST.getDescription(), SKILL));
				}
			}

			AgentMaster agent = existing.get();
			agent.getAgentSkillsMappings().clear();

			Optional<CalendarMaster> calendar = calendarMasterRepository.findById(request.getCalendarId());
			if (calendar.isEmpty()) {
				throw new FlickzzDeskException(DOES_NOT_EXIST,
						getDescription(DOES_NOT_EXIST.getDescription(), CALENDAR));
			}

			CountryMaster country = countryMasterRepository.findByCountryIdAndIsActiveTrue(request.getCountryId())
					.orElseThrow(() -> new FlickzzDeskException(DOES_NOT_EXIST,
							getDescription(DOES_NOT_EXIST.getDescription(), COUNTRY)));

			CityMaster city = cityMasterRepository.findByCityIdAndIsActiveTrue(request.getCityId())
					.orElseThrow(() -> new FlickzzDeskException(DOES_NOT_EXIST,
							getDescription(DOES_NOT_EXIST.getDescription(), CITY)));

			agent.setAgentName(request.getAgentName());
			agent.setCalendarMaster(calendar.get());
			agent.setUpdatedBy(request.getUpdatedBy());
			agent.setIsUpdaterAdmin(request.getIsUpdatedByAdmin());
			agentMasterRepository.save(agent);

			User user = agent.getUser();
			user.setCountry(country);
			user.setCity(city);
			user.setPhoneCode(request.getPhoneCode());
			user.setPhoneNumber(request.getPhoneNumber());
			user.setUpdatedBy(request.getUpdatedBy());
			user.setIsUpdaterAdmin(request.getIsUpdatedByAdmin());
			user.getLanguages().clear();
			userRepository.save(user);

			// update user language mappings
			List<Long> languageIdsForUpdate = request.getLanguageIds();
			if ((languageIdsForUpdate == null || languageIdsForUpdate.isEmpty()) && request.getLanguageIds() != null) {
				languageIdsForUpdate = request.getLanguageIds();
			}
			// remove existing mappings
			userLanguageMappingRepository.deleteByUser(user);
			if (languageIdsForUpdate != null && !languageIdsForUpdate.isEmpty()) {
				List<LanguageMaster> langs = languageMasterRepository.findAllById(languageIdsForUpdate);
				Map<Long, LanguageMaster> langMap = langs.stream()
					.filter(l -> Boolean.TRUE.equals(l.getIsActive()))
					.collect(Collectors.toMap(LanguageMaster::getLanguageId, l -> l));
				for (Long lid : languageIdsForUpdate) {
					if (!langMap.containsKey(lid)) {
						throw new FlickzzDeskException(DOES_NOT_EXIST,
							getDescription(DOES_NOT_EXIST.getDescription(), LANGUAGE));
					}
				}
				List<UserLanguageMapping> userLangs = languageIdsForUpdate.stream().map(lid ->
					UserLanguageMapping.builder().user(user).language(langMap.get(lid))
						.createdBy(request.getCreatedBy()).isCreatorAdmin(request.getIsCreatedByAdmin()).build()
				).collect(Collectors.toList());
				userLanguageMappingRepository.saveAll(userLangs);
			}

			List<AgentSkillsMapping> agentSkills = request.getSkills().stream().map(skillInfo -> {
				SkillMaster skill = skillMap.get(skillInfo.getSkillId());
				return AgentSkillsMapping.builder().agent(agent).skill(skill)
						.experienceYears(skillInfo.getExperienceYears())
						.experienceMonths(skillInfo.getExperienceMonths()).createdBy(request.getCreatedBy())
						.isCreatorAdmin(request.getIsCreatedByAdmin()).build();
			}).collect(Collectors.toList());
			agentSkillsMappingRepository.saveAll(agentSkills);
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
			AgentMaster agent = existing.get();
			agent.setIsActive(DEACTIVATE);
			agentMasterRepository.save(agent);
		} catch (DataIntegrityViolationException e) {
			log.error("DataIntegrityViolationException in deleteAgent method in FlickzzDeskService");
			throw new FlickzzDeskException(DB_SAVE_ERROR, "Unassign agent from all assignment to delete");
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

	public List<AgentMasterVO> getActiveAgentList(String orgId) {
		log.info(generateLog(ENTRY, this.getClass().getName()));
		try {
			return agentMasterRepository.findAllByOrganization_CompanyIdAndIsActiveTrue(Long.valueOf(orgId)).stream()
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
					.findByAgentAgentIdAndIsActiveTrue(Long.valueOf(agentId));
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

	public AgentMasterVO getAgentInfoByEmail(String email) {
		log.info(generateLog(ENTRY, this.getClass().getName()));
		try {
			AgentMaster agentMaster = agentMasterRepository.findByMailIdAndIsActiveTrue(email)
					.orElseThrow(() -> new FlickzzDeskException(DOES_NOT_EXIST,
							getDescription(DOES_NOT_EXIST.getDescription(), AGENT)));
			return mapper.toAgentMasterVO(agentMaster);
		} catch (FlickzzDeskException e) {
			throw e;
		} catch (Exception e) {
			log.error("Exception in getAgentInfo method in FlickzzDeskService");
			throw new FlickzzDeskException(DEFAULT_ERROR_CODE);
		}
	}
}
