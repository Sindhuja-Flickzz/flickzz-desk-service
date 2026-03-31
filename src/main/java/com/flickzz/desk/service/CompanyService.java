package com.flickzz.desk.service;

import static com.flickzz.desk.config.FlickzzDeskConstants.COMPANY;
import static com.flickzz.desk.config.FlickzzDeskConstants.COMPANY_NAME;
import static com.flickzz.desk.config.FlickzzDeskConstants.CURRENCY;
import static com.flickzz.desk.config.FlickzzDeskConstants.ENTRY;
import static com.flickzz.desk.config.FlickzzDeskConstants.REGISTERED_NUMBER;
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
import com.flickzz.desk.model.CompanyMaster;
import com.flickzz.desk.repo.CompanyMasterRepository;
import com.flickzz.desk.vo.CompanyMasterRequestVO;
import com.flickzz.desk.vo.CompanyMasterVO;

@Service
public class CompanyService {
	
	private static final Logger log = LoggerFactory.getLogger(CompanyService.class);
	
	@Autowired
	CompanyMasterRepository companyMasterRepository;
	
	@Autowired
	private CommonMapper mapper;
	
	public CompanyMasterVO createCompany(CompanyMasterRequestVO request) {
		log.debug(generateLog(ENTRY, this.getClass().getName()));
		try {
			if (request == null || request.getCompanyName() == null) {
		 		throw new FlickzzDeskException(INVALID_FIELD, getDescription(INVALID_FIELD.getDescription(), COMPANY_NAME));
		 	}
					
		 	if (request == null || request.getRegisteredNumber() == null) {
		 		throw new FlickzzDeskException(INVALID_FIELD, getDescription(INVALID_FIELD.getDescription(), REGISTERED_NUMBER));
		 	}
						
		 	if (request == null || request.getCurrency() == null) {
		 		throw new FlickzzDeskException(INVALID_FIELD, getDescription(INVALID_FIELD.getDescription(), CURRENCY));
		 	}
			
		 	companyMasterRepository.findByCompanyName(request.getCompanyName()).ifPresent(c -> {
		 		throw new FlickzzDeskException(ALREADY_EXISTS, getDescription(ALREADY_EXISTS.getDescription(), COMPANY_NAME));
		 	});
			
		 	CompanyMaster entity = mapper.toCompanyMasterEntity(request);
		 	return mapper.toCompanyMasterVO(companyMasterRepository.save(entity));
		} catch (FlickzzDeskException e) {
			throw e;
		} catch (Exception e) {
			log.error("Exception in createCompany method in FlickzzDeskService");
			throw new FlickzzDeskException(DEFAULT_ERROR_CODE);
		}
	}

	public CompanyMasterVO getCompanyInfo(String companyId) {
		log.debug(generateLog(ENTRY, this.getClass().getName()));
		try {
			CompanyMaster entity = companyMasterRepository.findById(Long.valueOf(companyId))
					.orElseThrow(() -> new FlickzzDeskException(DOES_NOT_EXIST, getDescription(DOES_NOT_EXIST.getDescription(), COMPANY)));
			return mapper.toCompanyMasterVO(entity);
		} catch (FlickzzDeskException e) {
			throw e;
		} catch (Exception e) {
			log.error("Exception in getCompanyInfo method in FlickzzDeskService");
			throw new FlickzzDeskException(DEFAULT_ERROR_CODE);
		}
	}

	public CompanyMasterVO updateCompany(CompanyMasterRequestVO request) {
		log.debug(generateLog(ENTRY, this.getClass().getName()));
		try {
			if (request == null || request.getCompanyId() == null) {
				throw new FlickzzDeskException(DOES_NOT_EXIST, getDescription(DOES_NOT_EXIST.getDescription(), COMPANY));
			}

			CompanyMaster existing = companyMasterRepository.findById(request.getCompanyId())
					.orElseThrow(() -> new FlickzzDeskException(DOES_NOT_EXIST, getDescription(DOES_NOT_EXIST.getDescription(), COMPANY)));

			CompanyMaster entity = mapper.toCompanyMasterEntity(request);
			entity.setCompanyId(existing.getCompanyId());
			entity.setCompanyName(existing.getCompanyName());
			entity.setUpdatedBy(request.getUpdatedBy());
			return mapper.toCompanyMasterVO(companyMasterRepository.save(entity));
		} catch (FlickzzDeskException e) {
			throw e;
		} catch (Exception e) {
			log.error("Exception in updateCompany method in FlickzzDeskService");
			throw new FlickzzDeskException(DEFAULT_ERROR_CODE);
		}
	}

	public void deleteCompany(String companyId) {
		log.debug(generateLog(ENTRY, this.getClass().getName()));
        try {
            CompanyMaster existing = companyMasterRepository.findById(companyId != null ? Long.valueOf(companyId) : null)
                    .orElseThrow(() -> new FlickzzDeskException(DOES_NOT_EXIST, getDescription(DOES_NOT_EXIST.getDescription(), COMPANY)));
            companyMasterRepository.delete(existing);
		} catch (FlickzzDeskException e) {
			throw e;
		} catch (Exception e) {
			log.error("Exception in deleteCompany method in FlickzzDeskService");
			throw new FlickzzDeskException(DEFAULT_ERROR_CODE);
		}
	}

	public List<CompanyMasterVO> listCompanies() {
		log.debug(generateLog(ENTRY, this.getClass().getName()));
		try {
			return companyMasterRepository.findAll().stream().map(mapper::toCompanyMasterVO).toList();
		} catch (FlickzzDeskException e) {
			throw e;
		} catch (Exception e) {
			log.error("Exception in listCompanies method in FlickzzDeskService");
			throw new FlickzzDeskException(DEFAULT_ERROR_CODE);
		}
	}
}
