package com.flickzz.desk.service;

import static com.flickzz.desk.config.FlickzzDeskConstants.COUNTRY;
import static com.flickzz.desk.config.FlickzzDeskConstants.ENTRY;
import static com.flickzz.desk.config.FlickzzDeskUtility.generateLog;
import static com.flickzz.desk.config.FlickzzDeskUtility.getDescription;
import static com.flickzz.desk.exception.FlickzzDeskErrorCodes.DEFAULT_ERROR_CODE;
import static com.flickzz.desk.exception.FlickzzDeskErrorCodes.DOES_NOT_EXIST;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.flickzz.desk.exception.FlickzzDeskException;
import com.flickzz.desk.mapper.CommonMapper;
import com.flickzz.desk.model.CountryMaster;
import com.flickzz.desk.repo.CountryMasterRepository;
import com.flickzz.desk.vo.CountryMasterVO;

@Service
public class CountryService {
	
	private static final Logger log = LoggerFactory.getLogger(CountryService.class);
	
	@Autowired
	private CountryMasterRepository countryMasterRepository;
	
	@Autowired
	private CommonMapper mapper;
	
	public CountryMasterVO getCountyInfo(String countryId) {
		log.debug(generateLog(ENTRY, this.getClass().getName()));
		try {
			Optional<CountryMaster> countryMaster = countryMasterRepository.findById(Long.valueOf(countryId));
			if (countryMaster == null) {
				throw new FlickzzDeskException(DOES_NOT_EXIST,
						getDescription(DOES_NOT_EXIST.getDescription(), COUNTRY));
			}
			return mapper.toCountryMasterVO(countryMaster.get());
		} catch (FlickzzDeskException e) {
			throw e;
		} catch (Exception e) {
			log.error("Exception in getPlantInfo method in FlickzzDeskService");
			throw new FlickzzDeskException(DEFAULT_ERROR_CODE);
		}
	}

	public List<CountryMasterVO> getCountyList() {
		log.debug(generateLog(ENTRY, this.getClass().getName()));
		try {
			return countryMasterRepository.findAll().stream().map(mapper::toCountryMasterVO).toList();
		} catch (FlickzzDeskException e) {
			throw e;
		} catch (Exception e) {
			log.error("Exception in getPlantInfo method in FlickzzDeskService");
			throw new FlickzzDeskException(DEFAULT_ERROR_CODE);
		}
	}
}
