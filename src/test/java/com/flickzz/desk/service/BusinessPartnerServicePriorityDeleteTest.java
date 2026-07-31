package com.flickzz.desk.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.flickzz.desk.exception.FlickzzDeskException;
import com.flickzz.desk.mapper.CommonMapper;
import com.flickzz.desk.model.BPConfiguration;
import com.flickzz.desk.model.BPPriority;
import com.flickzz.desk.repo.BPConfigurationRepository;
import com.flickzz.desk.repo.BPPriorityRepository;
import com.flickzz.desk.repo.BPSlaRepository;
import com.flickzz.desk.repo.BusinessPartnerRepository;
import tools.jackson.databind.ObjectMapper;

class BusinessPartnerServicePriorityDeleteTest {

    @Mock
    private BPConfigurationRepository bPConfigurationRepository;

    @Mock
    private BPPriorityRepository bPPriorityRepository;

    @Mock
    private BPSlaRepository bpSlaRepository;

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
    void deletePriorityThrowsWhenSlaExists() {
        BPConfiguration configuration = new BPConfiguration();
        configuration.setConfigurationId(10L);

        BPPriority priority = new BPPriority();
        priority.setPriorityId(20L);
        priority.setCode("P1");
        priority.setConfiguration(configuration);

        when(bPPriorityRepository.findByPriorityIdAndIsActive(20L, true)).thenReturn(Optional.of(priority));
        when(bpSlaRepository.existsByConfigurationConfigurationIdAndPriorityPriorityIdAndIsActive(10L, 20L, true))
                .thenReturn(true);

        FlickzzDeskException exception = assertThrows(FlickzzDeskException.class,
                () -> service.deleteBusinessPartnerPriorityConfiguration("20", 1L));

        verify(bpSlaRepository).existsByConfigurationConfigurationIdAndPriorityPriorityIdAndIsActive(10L, 20L, true);
        assert exception.getDescription().contains("cannot be be deleted");
    }
}
