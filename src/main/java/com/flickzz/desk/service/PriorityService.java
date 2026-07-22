package com.flickzz.desk.service;

import org.slf4j.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.*;

import com.flickzz.desk.mapper.*;
import com.flickzz.desk.repo.*;

@Service
@SuppressWarnings({ "unused", "null" })
public class PriorityService {

	private static final Logger log = LoggerFactory.getLogger(PriorityService.class);

	@Autowired
	BPPriorityRepository bPPriorityRepository;

	@Autowired
	private CompanyMasterRepository companyMasterRepository;

	@Autowired
	private TicketTypeMasterRepository ticketTypeMasterRepository;

	@Autowired
	CommonMapper mapper;

//	public BPPriorityVO createPriority(BpConfigRequestVO request) {
//		log.info(generateLog(ENTRY, this.getClass().getName()));
//		try {
//			if (request == null || request.getPriorityName() == null) {
//				throw new FlickzzDeskException(INVALID_FIELD,
//						getDescription(INVALID_FIELD.getDescription(), PRIORITY_NAME));
//			} else if (request.getLevel() == null) {
//				throw new FlickzzDeskException(INVALID_FIELD, getDescription(INVALID_FIELD.getDescription(), RANK));
//			} else if (request.getResponseSla() == null) {
//				throw new FlickzzDeskException(INVALID_FIELD,
//						getDescription(INVALID_FIELD.getDescription(), RESPONSE_SLA));
//			} else if (request.getResolutionSla() == null) {
//				throw new FlickzzDeskException(INVALID_FIELD,
//						getDescription(INVALID_FIELD.getDescription(), RESOLUTION_SLA));
//			}
//
//			Optional<CompanyMaster> company = companyMasterRepository.findById(request.getOrgId());
//			if (company == null) {
//				throw new FlickzzDeskException(DOES_NOT_EXIST,
//						getDescription(DOES_NOT_EXIST.getDescription(), COMPANY));
//			}
//
//			Optional<TicketTypeMaster> ticketType = ticketTypeMasterRepository
//					.findByTicketTypeIdAndIsActiveTrue(request.getTicketTypeId());
//			if (ticketType == null) {
//				throw new FlickzzDeskException(DOES_NOT_EXIST,
//						getDescription(DOES_NOT_EXIST.getDescription(), TICKET_TYPE));
//			}
//
//			Long ifPriorityExist = bPPriorityRepository.validatePriority(request.getOrgId(),
//					request.getTicketTypeId(), request.getLevel(), request.getPriorityName());
//			if (ifPriorityExist > 0) {
//				throw new FlickzzDeskException(ALREADY_EXISTS,
//						getDescription(ALREADY_EXISTS.getDescription(), PRIORITY));
//			}
//
//			BPPriority bPPriority = mapper.toPriorityMaster(request, company.get(), ticketType.get());
//
//			return mapper.toPriorityMasterVo(bPPriority);
////			return mapper.toPriorityMasterVo(bPPriorityRepository.save(priorityMaster));
//		} catch (
//
//		FlickzzDeskException e) {
//			throw e;
//		} catch (Exception e) {
//			log.error("Exception in createPriority method in PriorityService");
//			throw new FlickzzDeskException(DEFAULT_ERROR_CODE);
//		}
//	}
//
//	public BPPriorityVO getPriorityInfo(String priorityId) {
//		log.info(generateLog(ENTRY, this.getClass().getName()));
//		try {
//			Optional<BPPriority> bPPriority = bPPriorityRepository.findById(Long.valueOf(priorityId));
//			if (bPPriority == null) {
//				throw new FlickzzDeskException(DOES_NOT_EXIST,
//						getDescription(DOES_NOT_EXIST.getDescription(), PRIORITY));
//			}
//
//			return mapper.toPriorityMasterVo(bPPriority.get());
//		} catch (FlickzzDeskException e) {
//			throw e;
//		} catch (Exception e) {
//			log.error("Exception in getPriorityInfo method in PriorityService");
//			throw new FlickzzDeskException(DEFAULT_ERROR_CODE);
//		}
//	}
//
//	public BPPriorityVO updatePriority(BpConfigRequestVO request) {
//		log.info(generateLog(ENTRY, this.getClass().getName()));
//		try {
//			Optional<BPPriority> existing = bPPriorityRepository.findById(request.getPriorityId());
//			if (existing == null) {
//				throw new FlickzzDeskException(DOES_NOT_EXIST,
//						getDescription(DOES_NOT_EXIST.getDescription(), PRIORITY));
//			}
//
//			if (request == null || request.getPriorityName() == null) {
//				throw new FlickzzDeskException(INVALID_FIELD,
//						getDescription(INVALID_FIELD.getDescription(), PRIORITY_NAME));
//			} else if (request.getLevel() == null) {
//				throw new FlickzzDeskException(INVALID_FIELD, getDescription(INVALID_FIELD.getDescription(), RANK));
//			} else if (request.getResponseSla() == null) {
//				throw new FlickzzDeskException(INVALID_FIELD,
//						getDescription(INVALID_FIELD.getDescription(), RESPONSE_SLA));
//			} else if (request.getResolutionSla() == null) {
//				throw new FlickzzDeskException(INVALID_FIELD,
//						getDescription(INVALID_FIELD.getDescription(), RESOLUTION_SLA));
//			}
//
//			Optional<CompanyMaster> company = companyMasterRepository.findById(request.getOrgId());
//			if (company == null) {
//				throw new FlickzzDeskException(DOES_NOT_EXIST,
//						getDescription(DOES_NOT_EXIST.getDescription(), COMPANY));
//			}
//
//			BPPriority bPPriority = existing.get();
////			bPPriority.setPriorityName(request.getPriorityName());
//			bPPriority.setLevel(request.getLevel());
//			bPPriority.setDescription(request.getDescription());
////			bPPriority.setFrequency(request.getFrequency());
////			bPPriority.setResponseSla(request.getResponseSla());
////			bPPriority.setResolutionSla(request.getResolutionSla());
////			bPPriority.setColorCode(request.getColorCode());
//			return mapper.toPriorityMasterVo(bPPriorityRepository.save(bPPriority));
//		} catch (FlickzzDeskException e) {
//			throw e;
//		} catch (Exception e) {
//			log.error("Exception in updatePriority method in PriorityService");
//			throw new FlickzzDeskException(DEFAULT_ERROR_CODE);
//		}
//	}
//
//	public void deletePriority(String priorityId) {
//		log.info(generateLog(ENTRY, this.getClass().getName()));
//		try {
//			Optional<BPPriority> existing = bPPriorityRepository.findById(Long.valueOf(priorityId));
//			if (existing == null) {
//				throw new FlickzzDeskException(DOES_NOT_EXIST,
//						getDescription(DOES_NOT_EXIST.getDescription(), PRIORITY));
//			}
//			bPPriorityRepository.delete(existing.get());
//		} catch (FlickzzDeskException e) {
//			throw e;
//		} catch (Exception e) {
//			log.error("Exception in deletePriority method in PriorityService");
//			throw new FlickzzDeskException(DEFAULT_ERROR_CODE);
//		}
//	}
//
//	public List<BPPriorityVO> getPriorityList() {
//		log.info(generateLog(ENTRY, this.getClass().getName()));
//		try {
//			return bPPriorityRepository.findAll().stream().map(priority -> mapper.toPriorityMasterVo(priority))
//					.toList();
//		} catch (Exception e) {
//			log.error("Exception in getPriorityList method in PriorityService");
//			throw new FlickzzDeskException(DEFAULT_ERROR_CODE);
//		}
//	}

}
