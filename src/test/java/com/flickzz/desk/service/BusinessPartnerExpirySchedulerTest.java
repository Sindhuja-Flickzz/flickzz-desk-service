package com.flickzz.desk.service;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import com.flickzz.desk.scheduler.BusinessPartnerExpiryScheduler;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.flickzz.desk.model.BusinessPartner;
import com.flickzz.desk.repo.BusinessPartnerRepository;

@ExtendWith(MockitoExtension.class)
class BusinessPartnerExpirySchedulerTest {

	@Mock
	private BusinessPartnerRepository businessPartnerRepository;

	@InjectMocks
	private BusinessPartnerExpiryScheduler businessPartnerExpiryScheduler;

	@Test
	void deactivateExpiredBusinessPartners_shouldMarkExpiredPartnersInactive() {
		BusinessPartner expiredPartner = new BusinessPartner();
		expiredPartner.setBusinessPartnerId(1L);
		expiredPartner.setIsActive(true);
		expiredPartner.setValidTo(Date.from(LocalDate.of(2024, 1, 1).atStartOfDay(ZoneId.systemDefault()).toInstant()));

		when(businessPartnerRepository.findExpiredActivePartners(any(Date.class))).thenReturn(List.of(expiredPartner));

		businessPartnerExpiryScheduler.deactivateExpiredBusinessPartners();

		assertFalse(expiredPartner.getIsActive());
		verify(businessPartnerRepository).findExpiredActivePartners(any(Date.class));
		verify(businessPartnerRepository).saveAll(List.of(expiredPartner));
	}
}
