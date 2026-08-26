package com.vendo.auto_search_service.application.auto_search;

import com.vendo.auto_search_service.port.auto_search.AutoSearchCommandPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AutoSearchExpirationServiceTest {

    @InjectMocks
    private AutoSearchExpirationService expirationService;

    @Mock
    private AutoSearchCommandPort commandPort;

    @Test
    void expireOutdatedRequests_shouldDelegateToCommandPort_withCurrentTimeAsReference() {
        when(commandPort.expireOutdatedRequests(any())).thenReturn(3L);

        expirationService.expireOutdatedRequests();

        ArgumentCaptor<LocalDateTime> referenceTimeCaptor = ArgumentCaptor.forClass(LocalDateTime.class);
        verify(commandPort).expireOutdatedRequests(referenceTimeCaptor.capture());
        assertThat(Duration.between(referenceTimeCaptor.getValue(), LocalDateTime.now())).isLessThan(Duration.ofSeconds(5));
    }

    @Test
    void expireOutdatedRequests_shouldNotThrow_whenNoRequestsWereExpired() {
        when(commandPort.expireOutdatedRequests(any())).thenReturn(0L);

        expirationService.expireOutdatedRequests();

        verify(commandPort).expireOutdatedRequests(any());
    }
}
