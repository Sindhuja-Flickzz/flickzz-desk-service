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
import com.flickzz.desk.model.*;
import com.flickzz.desk.repo.*;
import com.flickzz.desk.vo.*;

@Service
public class EpicService {

	private static final Logger log = LoggerFactory.getLogger(EpicService.class);

	@Autowired
	private EpicRepository epicRepository;

	@Autowired
	private CommonMapper mapper;

	public EpicVO getEpicInfo(String epicId) {
		log.info(generateLog(ENTRY, this.getClass().getName()));
		try {
			Optional<Epic> epic = epicRepository.findById(Long.valueOf(epicId));
			if (!epic.isPresent()) {
				throw new FlickzzDeskException(DOES_NOT_EXIST, getDescription(DOES_NOT_EXIST.getDescription(), EPIC));
			}
			return mapper.toEpicVO(epic.get());
		} catch (FlickzzDeskException e) {
			throw e;
		} catch (Exception e) {
			log.error("Exception in getEpicInfo method in ProjectService", e);
			throw new FlickzzDeskException(DEFAULT_ERROR_CODE);
		} finally {
			log.info(generateLog(EXIT, this.getClass().getName()));
		}
	}
}
