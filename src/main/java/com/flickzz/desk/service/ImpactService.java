package com.flickzz.desk.service;

import static com.flickzz.desk.config.FlickzzDeskConstants.ACTIVE;
import static com.flickzz.desk.config.FlickzzDeskConstants.COMPANY;
import static com.flickzz.desk.config.FlickzzDeskConstants.ENTRY;
import static com.flickzz.desk.config.FlickzzDeskConstants.IMPACT;
import static com.flickzz.desk.config.FlickzzDeskConstants.IMPACT_CODE;
import static com.flickzz.desk.config.FlickzzDeskConstants.IMPACT_LEVEL;
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
import com.flickzz.desk.model.CompanyMaster;
import com.flickzz.desk.model.ImpactMaster;
import com.flickzz.desk.repo.CompanyMasterRepository;
import com.flickzz.desk.repo.ImpactMasterRepository;
import com.flickzz.desk.vo.ImpactMasterVO;
import com.flickzz.desk.vo.ImpactRequestVO;

@Service
public class ImpactService {

	private static final Logger log = LoggerFactory.getLogger(ImpactService.class);

	@Autowired
	ImpactMasterRepository impactMasterRepository;

	@Autowired
	private CompanyMasterRepository companyMasterRepository;

	@Autowired
	CommonMapper mapper;

	public ImpactMasterVO createImpact(ImpactRequestVO request) {
		log.info(generateLog(ENTRY, this.getClass().getName()));
		try {
			if (request == null || request.getImpactCode() == null) {
				throw new FlickzzDeskException(INVALID_FIELD,
						getDescription(INVALID_FIELD.getDescription(), IMPACT_CODE));
			} else if (request.getImpactLevel() == null) {
				throw new FlickzzDeskException(INVALID_FIELD,
						getDescription(INVALID_FIELD.getDescription(), IMPACT_LEVEL));
			}

			Optional<CompanyMaster> company = companyMasterRepository.findById(request.getOrgId());
			if (company == null) {
				throw new FlickzzDeskException(DOES_NOT_EXIST,
						getDescription(DOES_NOT_EXIST.getDescription(), COMPANY));
			}

			impactMasterRepository.findByOrganizationCompanyIdAndImpactCode(request.getOrgId(), request.getImpactCode())
					.ifPresent(p -> {
						throw new FlickzzDeskException(ALREADY_EXISTS, getDescription(ALREADY_EXISTS.getDescription(),
								(IMPACT_CODE + " for selected organization")));
					});

			ImpactMaster impactMaster = mapper.toImpactMaster(request, company.get());

			return mapper.toImpactMasterVo(impactMasterRepository.save(impactMaster));
		} catch (FlickzzDeskException e) {
			throw e;
		} catch (Exception e) {
			log.error("Exception in createImpact method in ImpactService");
			throw new FlickzzDeskException(DEFAULT_ERROR_CODE);
		}
	}

	public ImpactMasterVO getImpactInfo(String impactId) {
		log.info(generateLog(ENTRY, this.getClass().getName()));
		try {
			Optional<ImpactMaster> impactMaster = impactMasterRepository.findById(Long.valueOf(impactId));
			if (impactMaster == null) {
				throw new FlickzzDeskException(DOES_NOT_EXIST,
						getDescription(DOES_NOT_EXIST.getDescription(), "Impact"));
			}

			return mapper.toImpactMasterVo(impactMaster.get());
		} catch (FlickzzDeskException e) {
			throw e;
		} catch (Exception e) {
			log.error("Exception in getImpactInfo method in ImpactService");
			throw new FlickzzDeskException(DEFAULT_ERROR_CODE);
		}
	}

	public ImpactMasterVO updateImpact(ImpactRequestVO request) {
		log.info(generateLog(ENTRY, this.getClass().getName()));
		try {
			Optional<ImpactMaster> existing = impactMasterRepository.findById(request.getImpactId());
			if (existing == null) {
				throw new FlickzzDeskException(DOES_NOT_EXIST, getDescription(DOES_NOT_EXIST.getDescription(), IMPACT));
			}

			if (request.getImpactLevel() == null) {
				throw new FlickzzDeskException(INVALID_FIELD,
						getDescription(INVALID_FIELD.getDescription(), IMPACT_LEVEL));
			}

			ImpactMaster impactMaster = existing.get();
			impactMaster.setSlaMultiplier(request.getSlaMultiplier());
			impactMaster.setImpactLevel(request.getImpactLevel());
			return mapper.toImpactMasterVo(impactMasterRepository.save(impactMaster));
		} catch (FlickzzDeskException e) {
			throw e;
		} catch (Exception e) {
			log.error("Exception in updateImpact method in ImpactService");
			throw new FlickzzDeskException(DEFAULT_ERROR_CODE);
		}
	}

	public void deleteImpact(String impactId) {
		log.info(generateLog(ENTRY, this.getClass().getName()));
		try {
			Optional<ImpactMaster> existing = impactMasterRepository.findById(Long.valueOf(impactId));
			if (existing == null) {
				throw new FlickzzDeskException(DOES_NOT_EXIST, getDescription(DOES_NOT_EXIST.getDescription(), IMPACT));
			}
			impactMasterRepository.delete(existing.get());
		} catch (FlickzzDeskException e) {
			throw e;
		} catch (Exception e) {
			log.error("Exception in deleteImpact method in ImpactService");
			throw new FlickzzDeskException(DEFAULT_ERROR_CODE);
		}
	}

	public List<ImpactMasterVO> getImpactList(String orgId) {
		log.info(generateLog(ENTRY, this.getClass().getName()));
		try {
			if (orgId == null || orgId.isEmpty() || Integer.valueOf(orgId) == 0) {
				return impactMasterRepository.findAllByIsActive(ACTIVE).stream()
						.map(impact -> mapper.toImpactMasterVo(impact)).toList();
			}

			return impactMasterRepository.findAllByOrganizationCompanyIdAndIsActive(Long.valueOf(orgId), ACTIVE)
					.stream().map(impact -> mapper.toImpactMasterVo(impact)).toList();
		} catch (Exception e) {
			log.error("Exception in getImpactList method in ImpactService");
			throw new FlickzzDeskException(DEFAULT_ERROR_CODE);
		}
	}
}
