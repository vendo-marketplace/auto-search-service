package com.vendo.auto_search_service.application.auto_search;

import com.vendo.auto_search_service.application.auto_search.command.AutoSearchDataCommand;
import com.vendo.auto_search_service.domain.auto_search.AutoSearch;
import com.vendo.auto_search_service.domain.auto_search.AutoSearchDataBuilder;
import com.vendo.auto_search_service.domain.auto_search.SearchStatus;
import com.vendo.auto_search_service.infrastructure.props.AutoSearchSchedulerProps;
import com.vendo.auto_search_service.port.auto_search.AutoSearchCommandPort;
import com.vendo.auto_search_service.port.auto_search.AutoSearchQueryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AutoSearchExpirationServiceTest {

    @InjectMocks
    private AutoSearchExpirationService expirationService;

    @Mock
    private AutoSearchQueryPort queryPort;
    @Mock
    private AutoSearchCommandPort commandPort;
    @Mock
    private AutoSearchSchedulerProps schedulerProps;

    @BeforeEach
    void setUp() {
        when(schedulerProps.getPageSize()).thenReturn(10);
    }

    @Test
    void expireOutdatedRequests_shouldUpdateStatusToExpired_whenExpirationDateIsInThePast() {
        AutoSearch outdated = AutoSearchDataBuilder.withAllFields()
                .id("outdated-id")
                .expirationDate(LocalDateTime.now().minusDays(1))
                .build();

        when(queryPort.findActiveRequests(0, 10))
                .thenReturn(new AutoSearchDataCommand(List.of(outdated), false));

        expirationService.expireOutdatedRequests();

        ArgumentCaptor<AutoSearch> captor = ArgumentCaptor.forClass(AutoSearch.class);
        verify(commandPort).update(eq("outdated-id"), captor.capture());
        assertThat(captor.getValue().status()).isEqualTo(SearchStatus.EXPIRED);
    }

    @Test
    void expireOutdatedRequests_shouldSkipRequest_whenNotYetExpired() {
        AutoSearch stillActive = AutoSearchDataBuilder.withAllFields()
                .expirationDate(LocalDateTime.now().plusDays(1))
                .build();

        when(queryPort.findActiveRequests(0, 10))
                .thenReturn(new AutoSearchDataCommand(List.of(stillActive), false));

        expirationService.expireOutdatedRequests();

        verify(commandPort, never()).update(anyString(), any());
    }

    @Test
    void expireOutdatedRequests_shouldIterateAllPages() {
        AutoSearch outdatedOnFirstPage = AutoSearchDataBuilder.withAllFields()
                .id("first-page-id")
                .expirationDate(LocalDateTime.now().minusDays(1))
                .build();
        AutoSearch outdatedOnSecondPage = AutoSearchDataBuilder.withAllFields()
                .id("second-page-id")
                .expirationDate(LocalDateTime.now().minusHours(1))
                .build();

        when(queryPort.findActiveRequests(0, 10))
                .thenReturn(new AutoSearchDataCommand(List.of(outdatedOnFirstPage), true));
        when(queryPort.findActiveRequests(1, 10))
                .thenReturn(new AutoSearchDataCommand(List.of(outdatedOnSecondPage), false));

        expirationService.expireOutdatedRequests();

        verify(commandPort).update(eq("first-page-id"), any());
        verify(commandPort).update(eq("second-page-id"), any());
        verify(queryPort, never()).findActiveRequests(eq(2), anyInt());
    }

    @Test
    void expireOutdatedRequests_shouldDoNothing_whenNoActiveRequests() {
        when(queryPort.findActiveRequests(0, 10))
                .thenReturn(new AutoSearchDataCommand(List.of(), false));

        expirationService.expireOutdatedRequests();

        verifyNoInteractions(commandPort);
    }
}
