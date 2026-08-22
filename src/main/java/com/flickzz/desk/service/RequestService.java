package com.flickzz.desk.service;

import static com.flickzz.desk.config.FlickzzDeskConstants.*;
import static com.flickzz.desk.config.FlickzzDeskUtility.generateLog;
import static com.flickzz.desk.config.FlickzzDeskUtility.getDescription;
import static com.flickzz.desk.exception.FlickzzDeskErrorCodes.*;

import java.util.List;

import com.flickzz.desk.model.CompanyMaster;
import com.flickzz.desk.repo.CompanyMasterRepository;
import com.flickzz.desk.vo.request.RequestConfigRequestVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.flickzz.desk.exception.FlickzzDeskException;
import com.flickzz.desk.mapper.CommonMapper;
import com.flickzz.desk.model.RequestConfig;
import com.flickzz.desk.repo.RequestConfigRepository;
import com.flickzz.desk.vo.RequestConfigVO;

@Service
public class RequestService {

	private static final Logger log = LoggerFactory.getLogger(RequestService.class);

	@Autowired
	private RequestConfigRepository requestConfigRepository;

	@Autowired
	private CompanyMasterRepository companyMasterRepository;

	@Autowired
	private CommonMapper mapper;

	public String getRequestNumber(String requestType) {
		log.info(generateLog(ENTRY, this.getClass().getName()));
		try {
			// Logic to generate request number based on request type
			// This is a placeholder implementation and should be replaced with actual logic
			String requestNumber = requestType.toUpperCase() + "-" + System.currentTimeMillis();
			return requestNumber;
		} catch (FlickzzDeskException e) {
			throw e;
		} catch (Exception e) {
			log.error("Exception in createAgent method in FlickzzDeskService");
			throw new FlickzzDeskException(DEFAULT_ERROR_CODE);
		}
	}

	public RequestConfigVO createRequestConfig(RequestConfigRequestVO request) {
		log.info(generateLog(ENTRY, this.getClass().getName()));
		try {

			if(request == null || request.getOrgId() == null) {
				throw new FlickzzDeskException(INVALID_REQUEST,
						getDescription(INVALID_REQUEST.getDescription(), "Invalid request data"));
			}

			validateRangeValues(request.getRangeFrom(), request.getRangeTo());

			CompanyMaster company = companyMasterRepository.findById(request.getOrgId())
					.orElseThrow(() -> new FlickzzDeskException(DOES_NOT_EXIST,
							getDescription(DOES_NOT_EXIST.getDescription(),
									"Company with ID " + request.getOrgId())));

			validateNoOverlappingRange(request.getRequestType(), request.getRequestPrefix(),
					request.getRangeFrom(), request.getRangeTo(), request.getOrgId());

			Integer nextVersion = requestConfigRepository.findMaxRevisionByCompanyId(request.getOrgId()) + 1;

			RequestConfig requestConfig = RequestConfig.builder().requestType(request.getRequestType())
					.requestPrefix(request.getRequestPrefix()).revision(nextVersion).callHorizonPercentage(request.getCallHorizonPercentage())
					.callHorizonDays(request.getCallHorizonDays()).rangeFrom(request.getRangeFrom()).rangeTo(request.getRangeTo()).company(company).isCreatorAdmin(request.getIsCreatedByAdmin())
					.calculateBackward(request.getCalculateBackward()).createdBy(request.getCreatedBy()).build();
			if (nextVersion == 1) {
				requestConfig.setIsEnabled(ACTIVE);
			}
			requestConfig = requestConfigRepository.save(requestConfig);
			return mapper.toRequestConfigVO(requestConfig);

		} catch (FlickzzDeskException e) {
			throw e;
		} catch (Exception e) {
			log.error("Exception in createAgent method in FlickzzDeskService");
			throw new FlickzzDeskException(DEFAULT_ERROR_CODE);
		}
	}

	private void validateRangeValues(Integer rangeFrom, Integer rangeTo) {
		if (rangeFrom == null || rangeTo == null) {
			throw new FlickzzDeskException(INVALID_REQUEST,
					getDescription(INVALID_REQUEST.getDescription(), "Range values cannot be null"));
		}
		if (rangeFrom <= 0 || rangeTo <= 0) {
			throw new FlickzzDeskException(INVALID_REQUEST,
					getDescription(INVALID_REQUEST.getDescription(), "Range values must be positive integers"));
		}
	}

	private void validateNoOverlappingRange(String requestType, String requestPrefix, Integer rangeFrom, Integer rangeTo, Long orgId) {
		validateNoOverlappingRange(requestType, requestPrefix, rangeFrom, rangeTo, orgId, null);
	}

	private void validateNoOverlappingRange(String requestType, String requestPrefix, Integer rangeFrom, Integer rangeTo, Long orgId, Long configIdToExclude) {
		List<RequestConfig> existingConfigs = requestConfigRepository.findAllByIsActiveTrue();
		
		for (RequestConfig existingConfig : existingConfigs) {
			if (configIdToExclude != null && existingConfig.getConfigId() != null
					&& existingConfig.getConfigId().equals(configIdToExclude)) {
				continue;
			}
			if (existingConfig.getRequestType().equals(requestType)
				&& existingConfig.getRequestPrefix().equals(requestPrefix)
				&& existingConfig.getCompany().getCompanyId().equals(orgId)) {
				boolean rangesOverlap = rangeFrom <= existingConfig.getRangeTo() && rangeTo >= existingConfig.getRangeFrom();
				if (rangesOverlap) {
					throw new FlickzzDeskException(ALREADY_EXISTS,
							getDescription(ALREADY_EXISTS.getDescription(), 
									"Request config with " + requestType + " + " + requestPrefix + 
									" already exists with overlapping range " + existingConfig.getRangeFrom() + 
									"-" + existingConfig.getRangeTo()));
				}
			}
		}
	}

	public RequestConfigVO getRequestConfig(String requestType, Long orgId) {
		log.info(generateLog(ENTRY, this.getClass().getName()));
		try {
			RequestConfig requestConfig = requestConfigRepository
					.findByRequestTypeAndCompany_CompanyId(requestType, orgId)
					.orElseThrow(() -> new FlickzzDeskException(DOES_NOT_EXIST,
							getDescription(DOES_NOT_EXIST.getDescription(),
									"Request Config with type " + requestType + " and company ID " + orgId)));

			return mapper.toRequestConfigVO(requestConfig);
		} catch (FlickzzDeskException e) {
			throw e;
		} catch (Exception e) {
			log.error("Exception in createAgent method in FlickzzDeskService");
			throw new FlickzzDeskException(DEFAULT_ERROR_CODE);
		}
	}

	public RequestConfigVO updateRequestConfig(RequestConfigRequestVO requestConfigVO) {
		log.info(generateLog(ENTRY, this.getClass().getName()));
		try {
			RequestConfig existingConfig = requestConfigRepository
					.findById(requestConfigVO.getConfigId())
					.orElseThrow(() -> new FlickzzDeskException(DOES_NOT_EXIST,
							getDescription(DOES_NOT_EXIST.getDescription(),
									"Request Config with ID " + requestConfigVO.getConfigId())));

			validateRangeValues(requestConfigVO.getRangeFrom(), requestConfigVO.getRangeTo());

			validateNoOverlappingRange(existingConfig.getRequestType(), existingConfig.getRequestPrefix(),
					requestConfigVO.getRangeFrom(), requestConfigVO.getRangeTo(), existingConfig.getCompany().getCompanyId(),
					existingConfig.getConfigId());

            existingConfig.setRangeFrom(requestConfigVO.getRangeFrom());
            existingConfig.setRangeTo(requestConfigVO.getRangeTo());
            existingConfig.setCalculateBackward(requestConfigVO.getCalculateBackward());
			existingConfig.setCallHorizonDays(requestConfigVO.getCallHorizonDays());
            existingConfig.setCallHorizonPercentage(requestConfigVO.getCallHorizonPercentage());
            existingConfig.setUpdatedBy(requestConfigVO.getUpdatedBy());
            existingConfig.setIsUpdaterAdmin(requestConfigVO.getIsUpdatedByAdmin());
			existingConfig = requestConfigRepository.save(existingConfig);
			return mapper.toRequestConfigVO(existingConfig);
		} catch (FlickzzDeskException e) {
			throw e;
		} catch (Exception e) {
			log.error("Exception in createAgent method in FlickzzDeskService");
			throw new FlickzzDeskException(DEFAULT_ERROR_CODE);
		}
	}

	public List<RequestConfigVO> getAllRequestConfigs(Long orgId) {
		log.info(generateLog(ENTRY, this.getClass().getName()));
		try {
			List<RequestConfig> requestConfigs = requestConfigRepository.findByCompany_CompanyId(orgId);
			return mapper.toRequestConfigVOList(requestConfigs);
		} catch (FlickzzDeskException e) {
			throw e;
		} catch (Exception e) {
			log.error("Exception in createAgent method in FlickzzDeskService");
			throw new FlickzzDeskException(DEFAULT_ERROR_CODE);
		}
	}

	public void deleteRequestConfig(Long configId) {
		log.info(generateLog(ENTRY, this.getClass().getName()));
		try {
			RequestConfig existingConfig = requestConfigRepository.findById(configId)
					.orElseThrow(() -> new FlickzzDeskException(DOES_NOT_EXIST,
							getDescription(DOES_NOT_EXIST.getDescription(), "Request Config with ID " + configId)));

            if(existingConfig.getIsActive() && existingConfig.getIsEnabled()) {
                throw new FlickzzDeskException(INVALID_REQUEST,
                        getDescription(INVALID_REQUEST.getDescription(), "Deletion failed: Number range is currently in use."));
            }

            existingConfig.setIsActive(INACTIVE);
			requestConfigRepository.save(existingConfig);
		} catch (FlickzzDeskException e) {
			throw e;
		} catch (Exception e) {
			log.error("Exception in createAgent method in FlickzzDeskService");
			throw new FlickzzDeskException(DEFAULT_ERROR_CODE);
		}
	}

}
