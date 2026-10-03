package com.vendo.auto_search_service.infrastructure.scheduler;

import com.vendo.auto_search_service.port.auto_search.usecase.AutoSearchExpirationUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AutoSearchExpirationScheduler {

    private final AutoSearchExpirationUseCase expirationUseCase;

    @Scheduled(cron = "${auto-search.expiration.scheduler.cron}")
    public void expireOutdatedRequests() {
        expirationUseCase.expireOutdatedRequests();
    }

}
