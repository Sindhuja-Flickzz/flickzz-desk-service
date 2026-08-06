package com.flickzz.desk.scheduler;

import java.time.Instant;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.flickzz.desk.model.BusinessPartner;
import com.flickzz.desk.repo.BusinessPartnerRepository;

@Service
public class BusinessPartnerExpiryScheduler {

	private static final Logger log = LoggerFactory.getLogger(BusinessPartnerExpiryScheduler.class);

	private final BusinessPartnerRepository businessPartnerRepository;

	public BusinessPartnerExpiryScheduler(BusinessPartnerRepository businessPartnerRepository) {
		this.businessPartnerRepository = businessPartnerRepository;
	}

	@Scheduled(cron = "0 0 0 * * ?")
	@Transactional
	public void deactivateExpiredBusinessPartners() {
		Date now = Date.from(Instant.now());
		List<BusinessPartner> expiredPartners = businessPartnerRepository.findExpiredActivePartners(now);

		if (expiredPartners.isEmpty()) {
			log.info("No expired business partners found to deactivate. - Checked at {}", now);
			return;
		}

		expiredPartners.forEach(partner -> partner.setIsActive(false));
		businessPartnerRepository.saveAll(expiredPartners);
		log.info("Deactivated {} expired business partner(s).", expiredPartners.size());
	}
}
