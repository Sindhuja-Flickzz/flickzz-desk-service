package com.flickzz.desk.service;

import static com.flickzz.desk.config.FlickzzDeskConstants.ENTRY;
import static com.flickzz.desk.config.FlickzzDeskUtility.generateLog;
import static com.flickzz.desk.config.FlickzzDeskUtility.getDescription;
import static com.flickzz.desk.exception.FlickzzDeskErrorCodes.DEFAULT_ERROR_CODE;
import static com.flickzz.desk.exception.FlickzzDeskErrorCodes.DOES_NOT_EXIST;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.flickzz.desk.exception.FlickzzDeskException;
import com.flickzz.desk.mapper.CommonMapper;
import com.flickzz.desk.model.RequestConfig;
import com.flickzz.desk.repo.PlantMasterRepository;
import com.flickzz.desk.repo.RequestConfigRepository;
import com.flickzz.desk.vo.RequestConfigVO;

@Service
public class RequestService {

	private static final Logger log = LoggerFactory.getLogger(RequestService.class);

	@Autowired
	private RequestConfigRepository requestConfigRepository;

	@Autowired
	private PlantMasterRepository plantMasterRepository;

	@Autowired
	private CommonMapper mapper;

	public String getRequestNumber(String requestType) {
		log.debug(generateLog(ENTRY, this.getClass().getName()));
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

//	public RequestConfigVO createRequestConfig(RequestConfigRequestVO request) {
//		log.debug(generateLog(ENTRY, this.getClass().getName()));
//		try {
//
//			PlantMaster plant = plantMasterRepository.findByPlantId(request.getPlantId())
//					.orElseThrow(() -> new FlickzzDeskException(ALREADY_EXISTS,
//							getDescription(ALREADY_EXISTS.getDescription(), "Plant with ID " + request.getPlantId())));
//
//			requestConfigRepository.findByRequestTypeAndPlant_PlantId(request.getRequestType(), request.getPlantId())
//					.ifPresent(existingConfig -> {
//						throw new FlickzzDeskException(ALREADY_EXISTS,
//								getDescription(ALREADY_EXISTS.getDescription(), request.getRequestType()));
//					});
//
//			RequestConfig requestConfig = RequestConfig.builder().requestType(request.getRequestType())
//					.requestPrefix(request.getRequestPrefix()).revision(request.getRevision())
//					.rangeFrom(request.getRangeFrom()).rangeTo(request.getRangeTo())
//					.calculateBackward(request.getCalculateBackward()).createdBy(request.getCreatedBy()).build();
//			requestConfig = requestConfigRepository.save(requestConfig);
//			return mapper.toRequestConfigVO(requestConfig);
//
//		} catch (FlickzzDeskException e) {
//			throw e;
//		} catch (Exception e) {
//			log.error("Exception in createAgent method in FlickzzDeskService");
//			throw new FlickzzDeskException(DEFAULT_ERROR_CODE);
//		}
//	}
//
//	public RequestConfigVO getRequestConfig(String requestType, Long plantId) {
//		log.debug(generateLog(ENTRY, this.getClass().getName()));
//		try {
//			RequestConfig requestConfig = requestConfigRepository
//					.findByRequestTypeAndPlant_PlantId(requestType, plantId)
//					.orElseThrow(() -> new FlickzzDeskException(DOES_NOT_EXIST,
//							getDescription(DOES_NOT_EXIST.getDescription(),
//									"Request Config with type " + requestType + " and plant ID " + plantId)));
//
//			return mapper.toRequestConfigVO(requestConfig);
//		} catch (FlickzzDeskException e) {
//			throw e;
//		} catch (Exception e) {
//			log.error("Exception in createAgent method in FlickzzDeskService");
//			throw new FlickzzDeskException(DEFAULT_ERROR_CODE);
//		}
//	}
//
//	public RequestConfigVO updateRequestConfig(RequestConfigRequestVO requestConfigVO) {
//		log.debug(generateLog(ENTRY, this.getClass().getName()));
//		try {
//			RequestConfig existingConfig = requestConfigRepository
//					.findByRequestTypeAndPlant_PlantId(requestConfigVO.getRequestType(), requestConfigVO.getPlantId())
//					.orElseThrow(() -> new FlickzzDeskException(DOES_NOT_EXIST,
//							getDescription(DOES_NOT_EXIST.getDescription(),
//									"Request Config with type " + requestConfigVO.getRequestType() + " and plant ID "
//											+ requestConfigVO.getPlantId())));
//
//			existingConfig.setRevision(requestConfigVO.getRevision());
//			existingConfig.setRangeFrom(requestConfigVO.getRangeFrom());
//			existingConfig.setRangeTo(requestConfigVO.getRangeTo());
//			existingConfig.setCalculateBackward(requestConfigVO.getCalculateBackward());
//			existingConfig.setUpdatedBy(requestConfigVO.getUpdatedBy());
//			existingConfig = requestConfigRepository.save(existingConfig);
//			return mapper.toRequestConfigVO(existingConfig);
//		} catch (FlickzzDeskException e) {
//			throw e;
//		} catch (Exception e) {
//			log.error("Exception in createAgent method in FlickzzDeskService");
//			throw new FlickzzDeskException(DEFAULT_ERROR_CODE);
//		}
//	}

	public List<RequestConfigVO> getAllRequestConfigs() {
		log.debug(generateLog(ENTRY, this.getClass().getName()));
		try {
			List<RequestConfig> requestConfigs = requestConfigRepository.findAll();
			return mapper.toRequestConfigVOList(requestConfigs);
		} catch (FlickzzDeskException e) {
			throw e;
		} catch (Exception e) {
			log.error("Exception in createAgent method in FlickzzDeskService");
			throw new FlickzzDeskException(DEFAULT_ERROR_CODE);
		}
	}

	public void deleteRequestConfig(Long configId) {
		log.debug(generateLog(ENTRY, this.getClass().getName()));
		try {
			RequestConfig existingConfig = requestConfigRepository.findById(configId)
					.orElseThrow(() -> new FlickzzDeskException(DOES_NOT_EXIST,
							getDescription(DOES_NOT_EXIST.getDescription(), "Request Config with ID " + configId)));

			requestConfigRepository.delete(existingConfig);
		} catch (FlickzzDeskException e) {
			throw e;
		} catch (Exception e) {
			log.error("Exception in createAgent method in FlickzzDeskService");
			throw new FlickzzDeskException(DEFAULT_ERROR_CODE);
		}
	}

}
