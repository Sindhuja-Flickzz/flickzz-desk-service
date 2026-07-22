package com.flickzz.desk.service;

import static com.flickzz.desk.config.FlickzzDeskConstants.*;
import static com.flickzz.desk.config.FlickzzDeskUtility.*;
import static com.flickzz.desk.exception.FlickzzDeskErrorCodes.*;

import java.util.*;

import org.slf4j.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.*;

import com.flickzz.desk.exception.*;
import com.flickzz.desk.mapper.*;
import com.flickzz.desk.repo.*;
import com.flickzz.desk.vo.*;

@Service
public class TicketService {

	private static final Logger log = LoggerFactory.getLogger(TicketService.class);

	@Autowired
	private TicketTypeMasterRepository ticketTypeMasterRepository;

	@Autowired
	CommonMapper mapper;

	public List<TicketTypeMasterVO> getTicketTypeMasterList() {
		log.info(generateLog(ENTRY, this.getClass().getName()));
		try {
			return ticketTypeMasterRepository.findByIsActiveTrue().stream()
					.map(ticketType -> mapper.toTicketTypeMasterVo(ticketType)).toList();
		} catch (Exception e) {
			log.error("Exception in getTicketTypeMasterList method in TicketService");
			throw new FlickzzDeskException(DEFAULT_ERROR_CODE);
		}
	}

}
