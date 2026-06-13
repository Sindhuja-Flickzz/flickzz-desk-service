package com.flickzz.desk.service;

import static com.flickzz.desk.config.FlickzzDeskConstants.COMPANY;
import static com.flickzz.desk.config.FlickzzDeskConstants.ENTRY;
import static com.flickzz.desk.config.FlickzzDeskConstants.PRIORITY;
import static com.flickzz.desk.config.FlickzzDeskConstants.PRIORITY_AND_COMPANY;
import static com.flickzz.desk.config.FlickzzDeskConstants.PRIORITY_NAME;
import static com.flickzz.desk.config.FlickzzDeskConstants.RANK;
import static com.flickzz.desk.config.FlickzzDeskConstants.RESOLUTION_SLA;
import static com.flickzz.desk.config.FlickzzDeskConstants.RESPONSE_SLA;
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
import com.flickzz.desk.model.PriorityMaster;
import com.flickzz.desk.repo.CompanyMasterRepository;
import com.flickzz.desk.repo.PriorityMasterRepository;
import com.flickzz.desk.vo.PriorityMasterVO;
import com.flickzz.desk.vo.PriorityRequestVO;

@Service
public class PriorityService {

	private static final Logger log = LoggerFactory.getLogger(PriorityService.class);

	@Autowired
	PriorityMasterRepository priorityMasterRepository;

	@Autowired
	private CompanyMasterRepository companyMasterRepository;

	@Autowired
	CommonMapper mapper;

	public PriorityMasterVO createPriority(PriorityRequestVO request) {
		log.info(generateLog(ENTRY, this.getClass().getName()));
		try {
			if (request == null || request.getPriorityName() == null) {
				throw new FlickzzDeskException(INVALID_FIELD,
						getDescription(INVALID_FIELD.getDescription(), PRIORITY_NAME));
			} else if (request.getRank() == null) {
				throw new FlickzzDeskException(INVALID_FIELD, getDescription(INVALID_FIELD.getDescription(), RANK));
			} else if (request.getResponseSla() == null) {
				throw new FlickzzDeskException(INVALID_FIELD,
						getDescription(INVALID_FIELD.getDescription(), RESPONSE_SLA));
			} else if (request.getResolutionSla() == null) {
				throw new FlickzzDeskException(INVALID_FIELD,
						getDescription(INVALID_FIELD.getDescription(), RESOLUTION_SLA));
			}

			Optional<CompanyMaster> company = companyMasterRepository.findById(request.getOrgId());
			if (company == null) {
				throw new FlickzzDeskException(DOES_NOT_EXIST,
						getDescription(DOES_NOT_EXIST.getDescription(), COMPANY));
			}

			priorityMasterRepository
					.findByOrganizationCompanyIdAndPriorityName(request.getOrgId(), request.getPriorityName())
					.ifPresent(p -> {
						throw new FlickzzDeskException(ALREADY_EXISTS,
								getDescription(ALREADY_EXISTS.getDescription(), PRIORITY_AND_COMPANY));
					});

			PriorityMaster priorityMaster = mapper.toPriorityMaster(request, company.get());

			return mapper.toPriorityMasterVo(priorityMasterRepository.save(priorityMaster));
		} catch (FlickzzDeskException e) {
			throw e;
		} catch (Exception e) {
			log.error("Exception in createPriority method in PriorityService");
			throw new FlickzzDeskException(DEFAULT_ERROR_CODE);
		}
	}

	public PriorityMasterVO getPriorityInfo(String priorityId) {
		log.info(generateLog(ENTRY, this.getClass().getName()));
		try {
			Optional<PriorityMaster> priorityMaster = priorityMasterRepository.findById(Long.valueOf(priorityId));
			if (priorityMaster == null) {
				throw new FlickzzDeskException(DOES_NOT_EXIST,
						getDescription(DOES_NOT_EXIST.getDescription(), PRIORITY));
			}

			return mapper.toPriorityMasterVo(priorityMaster.get());
		} catch (FlickzzDeskException e) {
			throw e;
		} catch (Exception e) {
			log.error("Exception in getPriorityInfo method in PriorityService");
			throw new FlickzzDeskException(DEFAULT_ERROR_CODE);
		}
	}

	public PriorityMasterVO updatePriority(PriorityRequestVO request) {
		log.info(generateLog(ENTRY, this.getClass().getName()));
		try {
			Optional<PriorityMaster> existing = priorityMasterRepository.findById(request.getPriorityId());
			if (existing == null) {
				throw new FlickzzDeskException(DOES_NOT_EXIST,
						getDescription(DOES_NOT_EXIST.getDescription(), PRIORITY));
			}

			if (request == null || request.getPriorityName() == null) {
				throw new FlickzzDeskException(INVALID_FIELD,
						getDescription(INVALID_FIELD.getDescription(), PRIORITY_NAME));
			} else if (request.getRank() == null) {
				throw new FlickzzDeskException(INVALID_FIELD, getDescription(INVALID_FIELD.getDescription(), RANK));
			} else if (request.getResponseSla() == null) {
				throw new FlickzzDeskException(INVALID_FIELD,
						getDescription(INVALID_FIELD.getDescription(), RESPONSE_SLA));
			} else if (request.getResolutionSla() == null) {
				throw new FlickzzDeskException(INVALID_FIELD,
						getDescription(INVALID_FIELD.getDescription(), RESOLUTION_SLA));
			}

			Optional<CompanyMaster> company = companyMasterRepository.findById(request.getOrgId());
			if (company == null) {
				throw new FlickzzDeskException(DOES_NOT_EXIST,
						getDescription(DOES_NOT_EXIST.getDescription(), COMPANY));
			}

			PriorityMaster priorityMaster = existing.get();
			priorityMaster.setPriorityName(request.getPriorityName());
			priorityMaster.setRank(request.getRank());
			priorityMaster.setResponseSla(request.getResponseSla());
			priorityMaster.setResolutionSla(request.getResolutionSla());
			priorityMaster.setColorCode(request.getColorCode());
			return mapper.toPriorityMasterVo(priorityMasterRepository.save(priorityMaster));
		} catch (FlickzzDeskException e) {
			throw e;
		} catch (Exception e) {
			log.error("Exception in updatePriority method in PriorityService");
			throw new FlickzzDeskException(DEFAULT_ERROR_CODE);
		}
	}

	public void deletePriority(String priorityId) {
		log.info(generateLog(ENTRY, this.getClass().getName()));
		try {
			Optional<PriorityMaster> existing = priorityMasterRepository.findById(Long.valueOf(priorityId));
			if (existing == null) {
				throw new FlickzzDeskException(DOES_NOT_EXIST,
						getDescription(DOES_NOT_EXIST.getDescription(), PRIORITY));
			}
			priorityMasterRepository.delete(existing.get());
		} catch (FlickzzDeskException e) {
			throw e;
		} catch (Exception e) {
			log.error("Exception in deletePriority method in PriorityService");
			throw new FlickzzDeskException(DEFAULT_ERROR_CODE);
		}
	}

	public List<PriorityMasterVO> getPriorityList() {
		log.info(generateLog(ENTRY, this.getClass().getName()));
		try {
			return priorityMasterRepository.findAll().stream().map(priority -> mapper.toPriorityMasterVo(priority))
					.toList();
		} catch (Exception e) {
			log.error("Exception in getPriorityList method in PriorityService");
			throw new FlickzzDeskException(DEFAULT_ERROR_CODE);
		}
	}

}
