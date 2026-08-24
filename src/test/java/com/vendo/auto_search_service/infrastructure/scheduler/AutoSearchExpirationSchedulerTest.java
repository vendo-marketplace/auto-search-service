package com.vendo.auto_search_service.infrastructure.scheduler;

import com.vendo.auto_search_service.port.auto_search.usecase.AutoSearchExpirationUseCase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AutoSearchExpirationSchedulerTest {

    @InjectMocks
    private AutoSearchExpirationScheduler scheduler;

    @Mock
    private AutoSearchExpirationUseCase expirationUseCase;

    @Test
    void expireOutdatedRequests_shouldDelegateToUseCase() {
        scheduler.expireOutdatedRequests();

        verify(expirationUseCase).expireOutdatedRequests();
    }
}
