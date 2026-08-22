package com.flickzz.desk.service;

import static com.flickzz.desk.config.FlickzzDeskConstants.ACTIVE;
import static com.flickzz.desk.config.FlickzzDeskConstants.COMPANY;
import static com.flickzz.desk.config.FlickzzDeskConstants.ENTRY;
import static com.flickzz.desk.config.FlickzzDeskConstants.IMPACT;
import static com.flickzz.desk.config.FlickzzDeskConstants.IMPACT_CODE;
import static com.flickzz.desk.config.FlickzzDeskConstants.IMPACT_LEVEL;
import static com.flickzz.desk.config.FlickzzDeskUtility.generateLog;
import static com.flickzz.desk.config.FlickzzDeskUtility.getDescription;
import static com.flickzz.desk.exception.FlickzzDeskErrorCodes.*;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import com.flickzz.desk.exception.FlickzzDeskException;
import com.flickzz.desk.mapper.CommonMapper;
import com.flickzz.desk.model.CompanyMaster;
import com.flickzz.desk.model.ImpactMaster;
import com.flickzz.desk.repo.CompanyMasterRepository;
import com.flickzz.desk.repo.ImpactMasterRepository;
import com.flickzz.desk.vo.ImpactMasterVO;
import com.flickzz.desk.vo.request.ImpactRequestVO;

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

			Optional<CompanyMaster> company = companyMasterRepository.findByCompanyIdAndIsActiveTrue(request.getOrgId());
			if (company.isEmpty()) {
				throw new FlickzzDeskException(DOES_NOT_EXIST,
						getDescription(DOES_NOT_EXIST.getDescription(), COMPANY));
			}

			impactMasterRepository.findByOrganizationCompanyIdAndImpactCodeAndImpactLevel(
					request.getOrgId(), request.getImpactCode(), request.getImpactLevel())
					.ifPresent(impact -> {
						if (Boolean.TRUE.equals(impact.getIsActive())) {
							if (request.getImpactCode().equals(impact.getImpactCode())) {
								throw new FlickzzDeskException(ALREADY_EXISTS, getDescription(ALREADY_EXISTS.getDescription(), IMPACT_CODE));
							} else {
								throw new FlickzzDeskException(ALREADY_EXISTS, getDescription(ALREADY_EXISTS.getDescription(), IMPACT_LEVEL));
							}
						}
						throw new FlickzzDeskException(DELETED_ERROR, getDescription(DELETED_ERROR.getDescription(), IMPACT_CODE));
					});

			ImpactMaster impactMaster = mapper.toImpactMaster(request, company.get());

			return mapper.toImpactMasterVo(impactMasterRepository.save(impactMaster));
		} catch (FlickzzDeskException e) {
			throw e;
		} catch (DataIntegrityViolationException e) {
			throw new FlickzzDeskException(ALREADY_EXISTS,
					getDescription(ALREADY_EXISTS.getDescription(), IMPACT_CODE));
		} catch (Exception e) {
			log.error("Exception in createImpact method in ImpactService");
			throw new FlickzzDeskException(DEFAULT_ERROR_CODE);
		}
	}

	public ImpactMasterVO getImpactInfo(String impactId) {
		log.info(generateLog(ENTRY, this.getClass().getName()));
		try {
			Optional<ImpactMaster> impactMaster = impactMasterRepository.findById(Long.valueOf(impactId));
			if (impactMaster.isEmpty()) {
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
			if (existing.isEmpty()) {
				throw new FlickzzDeskException(DOES_NOT_EXIST, getDescription(DOES_NOT_EXIST.getDescription(), IMPACT));
			}

			if (request.getImpactLevel() == null) {
				throw new FlickzzDeskException(INVALID_FIELD,
						getDescription(INVALID_FIELD.getDescription(), IMPACT_LEVEL));
			}

			ImpactMaster impactMaster = existing.get();
			String impactCode = request.getImpactCode() == null ? impactMaster.getImpactCode() : request.getImpactCode();
			Integer impactLevel = request.getImpactLevel() == null ? impactMaster.getImpactLevel() : request.getImpactLevel();

			impactMasterRepository.findByOrganizationCompanyIdAndImpactCodeAndImpactLevel(
					impactMaster.getOrganization().getCompanyId(), impactCode, impactLevel)
					.filter(other -> !other.getImpactId().equals(impactMaster.getImpactId()))
					.ifPresent(other -> {
						if (Boolean.TRUE.equals(other.getIsActive())) {
							throw new FlickzzDeskException(ALREADY_EXISTS, getDescription(ALREADY_EXISTS.getDescription(), IMPACT_LEVEL));
						}
						throw new FlickzzDeskException(DELETED_ERROR, getDescription(DELETED_ERROR.getDescription(), IMPACT_CODE));
					});

			impactMaster.setImpactCode(impactCode);
			impactMaster.setImpactLevel(impactLevel);
			return mapper.toImpactMasterVo(impactMasterRepository.save(impactMaster));
		} catch (FlickzzDeskException e) {
			throw e;
		} catch (DataIntegrityViolationException e) {
			throw new FlickzzDeskException(ALREADY_EXISTS,
					getDescription(ALREADY_EXISTS.getDescription(), IMPACT_LEVEL));
		} catch (Exception e) {
			log.error("Exception in updateImpact method in ImpactService");
			throw new FlickzzDeskException(DEFAULT_ERROR_CODE);
		}
	}

	public void deleteImpact(String impactId) {
		log.info(generateLog(ENTRY, this.getClass().getName()));
		try {
			Optional<ImpactMaster> existing = impactMasterRepository.findById(Long.valueOf(impactId));
			if (existing.isEmpty()) {
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
