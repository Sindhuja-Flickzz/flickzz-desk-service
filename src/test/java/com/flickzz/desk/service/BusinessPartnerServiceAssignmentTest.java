package com.flickzz.desk.service;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.flickzz.desk.exception.FlickzzDeskException;
import com.flickzz.desk.mapper.CommonMapper;
import com.flickzz.desk.model.BPAssignment;
import com.flickzz.desk.model.BPCategory;
import com.flickzz.desk.model.BPConfiguration;
import com.flickzz.desk.model.BPSubCategory;
import com.flickzz.desk.model.BPSupportGroup;
import com.flickzz.desk.model.BusinessPartner;
import com.flickzz.desk.repo.BPAssignmentRepository;
import com.flickzz.desk.repo.BPConfigurationRepository;
import com.flickzz.desk.repo.BPSubCategoryRepository;
import com.flickzz.desk.repo.BPSupportGroupRepository;
import com.flickzz.desk.repo.BusinessPartnerRepository;
import com.flickzz.desk.vo.BpConfigRequestVO;

class BusinessPartnerServiceAssignmentTest {

	@Test
	void createBusinessPartnerAssignmentConfigurationShouldRejectDuplicateSupportGroupAssignment() {
		BusinessPartnerService service = new BusinessPartnerService();
		service.businessPartnerRepository = mock(BusinessPartnerRepository.class);
		service.bPConfigurationRepository = mock(BPConfigurationRepository.class);
		service.bpAssignmentRepository = mock(BPAssignmentRepository.class);
		service.bpSupportGroupRepository = mock(BPSupportGroupRepository.class);
		service.bpSubCategoryRepository = mock(BPSubCategoryRepository.class);
		service.mapper = mock(CommonMapper.class);

		BusinessPartner partner = new BusinessPartner();
		partner.setBusinessPartnerId(10L);

		BPConfiguration configuration = new BPConfiguration();
		configuration.setConfigurationId(100L);

		BPSupportGroup supportGroup = new BPSupportGroup();
		supportGroup.setSupportGroupId(200L);
		supportGroup.setConfiguration(configuration);

		BPSubCategory subCategory = new BPSubCategory();
		subCategory.setSubCategoryId(300L);
		BPCategory category = new BPCategory();
		category.setConfiguration(configuration);
		subCategory.setCategory(category);

		when(service.businessPartnerRepository.findByBusinessPartnerIdAndIsActive(10L, true)).thenReturn(Optional.of(partner));
		when(service.bPConfigurationRepository.findByBusinessPartnerBusinessPartnerIdAndIsActive(10L, true))
				.thenReturn(Optional.of(configuration));
		when(service.bpSupportGroupRepository.findBySupportGroupIdAndIsActive(200L, true))
				.thenReturn(Optional.of(supportGroup));
		when(service.bpSubCategoryRepository.findBySubCategoryIdAndIsActive(300L, true))
				.thenReturn(Optional.of(subCategory));
		when(service.bpAssignmentRepository.findByConfigurationConfigurationIdAndSupportGroupSupportGroupIdAndIsActive(100L,
				200L, true)).thenReturn(Optional.of(new com.flickzz.desk.model.BPAssignment()));

		BpConfigRequestVO request = BpConfigRequestVO.builder().businessPartnerId(10L).supportGroupId(200L)
				.subCategoryId(300L).createdBy(1L).build();

		assertThrows(FlickzzDeskException.class,
				() -> service.createBusinessPartnerAssignmentConfiguration(request));
	}

	@Test
	void updateBusinessPartnerAssignmentConfigurationShouldRejectDuplicateSubCategoryAssignment() {
		BusinessPartnerService service = new BusinessPartnerService();
		service.businessPartnerRepository = mock(BusinessPartnerRepository.class);
		service.bPConfigurationRepository = mock(BPConfigurationRepository.class);
		service.bpAssignmentRepository = mock(BPAssignmentRepository.class);
		service.bpSupportGroupRepository = mock(BPSupportGroupRepository.class);
		service.bpSubCategoryRepository = mock(BPSubCategoryRepository.class);
		service.mapper = mock(CommonMapper.class);

		BPConfiguration configuration = new BPConfiguration();
		configuration.setConfigurationId(100L);

		BPAssignment existingAssignment = new BPAssignment();
		existingAssignment.setAssignmentId(1L);
		existingAssignment.setConfiguration(configuration);

		when(service.bpAssignmentRepository.findBySupportGroupSupportGroupIdAndIsActive(200L, true))
				.thenReturn(Optional.of(existingAssignment));
		when(service.bpSubCategoryRepository.findBySubCategoryIdAndIsActive(300L, true))
				.thenReturn(Optional.of(new BPSubCategory()));
		when(service.bpAssignmentRepository.findByConfigurationConfigurationIdAndSubCategorySubCategoryIdAndIsActive(100L,
				300L, true)).thenReturn(Optional.of(new BPAssignment()));

		BpConfigRequestVO request = BpConfigRequestVO.builder().supportGroupId(200L).subCategoryId(300L).updatedBy(1L)
				.build();

		assertThrows(FlickzzDeskException.class,
				() -> service.updateBusinessPartnerAssignmentConfiguration(request));
	}
}
