package com.flickzz.desk.service;

import static com.flickzz.desk.config.FlickzzDeskConstants.ENTRY;
import static com.flickzz.desk.config.FlickzzDeskUtility.generateLog;
import static com.flickzz.desk.config.FlickzzDeskUtility.getDescription;
import static com.flickzz.desk.exception.FlickzzDeskErrorCodes.ALREADY_EXISTS;
import static com.flickzz.desk.exception.FlickzzDeskErrorCodes.DEFAULT_ERROR_CODE;
import static com.flickzz.desk.exception.FlickzzDeskErrorCodes.DOES_NOT_EXIST;
import static com.flickzz.desk.exception.FlickzzDeskErrorCodes.INVALID_FIELD;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.flickzz.desk.exception.FlickzzDeskException;
import com.flickzz.desk.mapper.CommonMapper;
import com.flickzz.desk.model.BusinessService;
import com.flickzz.desk.model.ServiceOffering;
import com.flickzz.desk.repo.BusinessServiceRepository;
import com.flickzz.desk.repo.ServiceOfferingRepository;
import com.flickzz.desk.vo.BusinessServiceRequestVO;
import com.flickzz.desk.vo.BusinessServiceVO;

@Service
public class SettingsService {

	private static final Logger log = LoggerFactory.getLogger(SettingsService.class);

	@Autowired
	private BusinessServiceRepository businessServiceRepository;

	@Autowired
	private ServiceOfferingRepository serviceOfferingRepository;

	@Autowired
	private CommonMapper mapper;

	public BusinessServiceVO createBusinessService(BusinessServiceRequestVO businessServiceRequestVO) {
		log.debug(generateLog(ENTRY, this.getClass().getName()));
		try {
			if (businessServiceRequestVO == null || businessServiceRequestVO.getServiceName() == null) {
				throw new FlickzzDeskException(INVALID_FIELD,
						getDescription(INVALID_FIELD.getDescription(), "Service Name"));
			} else if (businessServiceRequestVO.getServiceOfferings() == null
					|| businessServiceRequestVO.getServiceOfferings().size() == 0) {
				throw new FlickzzDeskException(INVALID_FIELD,
						getDescription(INVALID_FIELD.getDescription(), "Service Offerings"));
			}

			businessServiceRepository.findByServiceName(businessServiceRequestVO.getServiceName()).ifPresent(s -> {
				throw new FlickzzDeskException(ALREADY_EXISTS,
						getDescription(ALREADY_EXISTS.getDescription(), "Business Service"));
			});

			BusinessService saved = businessServiceRepository
					.save(mapper.toBusinessServiceEntity(businessServiceRequestVO));

			businessServiceRequestVO.getServiceOfferings().stream().forEach(so -> {
				ServiceOffering serviceOffering = mapper.toServiceOfferingEntity(so, saved);
				serviceOfferingRepository.save(serviceOffering);
			});

			return mapper.toBusinessServiceVO(saved);

		} catch (FlickzzDeskException e) {
			throw e;
		} catch (Exception e) {
			log.error("Exception in createBusinessService method in SettingsService");
			throw new FlickzzDeskException(DEFAULT_ERROR_CODE);
		}
	}

	public BusinessServiceVO updateBusinessService(BusinessServiceRequestVO businessServiceRequestVO) {
		log.debug(generateLog(ENTRY, this.getClass().getName()));
		try {
			if (businessServiceRequestVO == null || businessServiceRequestVO.getServiceId() == null) {
				throw new FlickzzDeskException(INVALID_FIELD,
						getDescription(INVALID_FIELD.getDescription(), "Service Id"));
			} else if (businessServiceRequestVO.getServiceName() == null) {
				throw new FlickzzDeskException(INVALID_FIELD,
						getDescription(INVALID_FIELD.getDescription(), "Service Name"));
			} else if (businessServiceRequestVO.getServiceOfferings() == null
					|| businessServiceRequestVO.getServiceOfferings().size() == 0) {
				throw new FlickzzDeskException(INVALID_FIELD,
						getDescription(INVALID_FIELD.getDescription(), "Service Offerings"));
			}

			BusinessService existing = businessServiceRepository
					.findByServiceId(businessServiceRequestVO.getServiceId())
					.orElseThrow(() -> new FlickzzDeskException(DOES_NOT_EXIST,
							getDescription(DOES_NOT_EXIST.getDescription(), "Business Service")));

			existing.getServiceOfferings().clear();
			existing.setUpdatedBy(businessServiceRequestVO.getUpdatedBy());
			BusinessService saved = businessServiceRepository.save(existing);

			businessServiceRequestVO.getServiceOfferings().stream().forEach(so -> {
				ServiceOffering serviceOffering = mapper.toServiceOfferingEntity(so, saved);
				serviceOfferingRepository.save(serviceOffering);
			});

			return mapper.toBusinessServiceVO(saved);

		} catch (FlickzzDeskException e) {
			throw e;
		} catch (Exception e) {
			log.error("Exception in updateBusinessService method in SettingsService");
			throw new FlickzzDeskException(DEFAULT_ERROR_CODE);
		}
	}

	public List<BusinessServiceVO> listBusinessServices() {
		log.debug(generateLog(ENTRY, this.getClass().getName()));
		try {
			return businessServiceRepository.findAll().stream().map(mapper::toBusinessServiceVO).toList();
		} catch (FlickzzDeskException e) {
			throw e;
		} catch (Exception e) {
			log.error("Exception in listBusinessServices method in SettingsService");
			throw new FlickzzDeskException(DEFAULT_ERROR_CODE);
		}
	}

	public BusinessServiceVO getBusinessServiceInfo(String serviceId) {
		log.debug(generateLog(ENTRY, this.getClass().getName()));
		try {
			BusinessService existing = businessServiceRepository.findByServiceId(Long.valueOf(serviceId))
					.orElseThrow(() -> new FlickzzDeskException(DOES_NOT_EXIST,
							getDescription(DOES_NOT_EXIST.getDescription(), "Business Service")));
			return mapper.toBusinessServiceVO(existing);
		} catch (FlickzzDeskException e) {
			throw e;
		} catch (Exception e) {
			log.error("Exception in getBusinessServiceInfo method in SettingsService");
			throw new FlickzzDeskException(DEFAULT_ERROR_CODE);
		}
	}

	public void deleteBusinessService(String serviceId) {
		log.debug(generateLog(ENTRY, this.getClass().getName()));
		try {
			BusinessService existing = businessServiceRepository.findByServiceId(Long.valueOf(serviceId))
					.orElseThrow(() -> new FlickzzDeskException(DOES_NOT_EXIST,
							getDescription(DOES_NOT_EXIST.getDescription(), "Business Service")));
			businessServiceRepository.delete(existing);
		} catch (FlickzzDeskException e) {
			throw e;
		} catch (Exception e) {
			log.error("Exception in deleteBusinessService method in SettingsService");
			throw new FlickzzDeskException(DEFAULT_ERROR_CODE);
		}
	}
}
