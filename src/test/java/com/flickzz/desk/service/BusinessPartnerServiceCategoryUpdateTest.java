package com.flickzz.desk.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.flickzz.desk.exception.FlickzzDeskException;
import com.flickzz.desk.mapper.CommonMapper;
import com.flickzz.desk.model.BPAssignment;
import com.flickzz.desk.model.BPCategory;
import com.flickzz.desk.model.BPConfiguration;
import com.flickzz.desk.model.BPSubCategory;
import com.flickzz.desk.repo.BPAssignmentRepository;
import com.flickzz.desk.repo.BPCategoryRepository;
import com.flickzz.desk.repo.BPConfigurationRepository;
import com.flickzz.desk.repo.BPSubCategoryRepository;
import com.flickzz.desk.repo.BusinessPartnerRepository;
import tools.jackson.databind.ObjectMapper;

class BusinessPartnerServiceCategoryUpdateTest {

    @Mock
    private BPConfigurationRepository bPConfigurationRepository;

    @Mock
    private BPCategoryRepository bpCategoryRepository;

    @Mock
    private BPSubCategoryRepository bpSubCategoryRepository;

    @Mock
    private BPAssignmentRepository bpAssignmentRepository;

    @Mock
    private BusinessPartnerRepository businessPartnerRepository;

    @Mock
    private CommonMapper mapper;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private BusinessPartnerService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void validateSubCategoryAssignmentsForRemovalThrowsWhenAssignedSubCategoryRemoved() {
        BPConfiguration configuration = new BPConfiguration();
        configuration.setConfigurationId(10L);

        BPCategory category = new BPCategory();
        category.setCategoryId(20L);
        category.setConfiguration(configuration);

        BPSubCategory subCategory = new BPSubCategory();
        subCategory.setSubCategoryId(30L);
        subCategory.setSubCategoryName("Support");

        when(bpSubCategoryRepository.findByCategoryCategoryIdAndIsActive(20L, true)).thenReturn(List.of(subCategory));
        when(bpAssignmentRepository.findByConfigurationConfigurationIdAndSubCategorySubCategoryIdAndIsActive(10L, 30L, true))
                .thenReturn(Optional.of(new BPAssignment()));

        FlickzzDeskException exception = assertThrows(FlickzzDeskException.class,
                () -> service.validateSubCategoryAssignmentsForRemoval(category, Set.of("Other")));

        verify(bpAssignmentRepository).findByConfigurationConfigurationIdAndSubCategorySubCategoryIdAndIsActive(10L, 30L, true);
        assert exception.getDescription().contains("cannot be removed as it has existing assignment");
    }

    @Test
    void upsertSubCategoryReactivatesExistingInactiveSubCategory() {
        BPCategory category = new BPCategory();
        category.setCategoryId(20L);

        BPSubCategory inactiveSubCategory = new BPSubCategory();
        inactiveSubCategory.setSubCategoryId(30L);
        inactiveSubCategory.setSubCategoryName("Technical");
        inactiveSubCategory.setIsActive(false);

        when(bpSubCategoryRepository.findByCategoryCategoryIdAndSubCategoryName(20L, "Technical"))
                .thenReturn(Optional.of(inactiveSubCategory));

        service.upsertSubCategory(category, "Technical", 1L, 2L);

        assertTrue(inactiveSubCategory.getIsActive());
        verify(bpSubCategoryRepository).save(inactiveSubCategory);
    }
}
