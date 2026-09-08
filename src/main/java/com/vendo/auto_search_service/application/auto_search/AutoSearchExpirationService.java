package com.vendo.auto_search_service.application.auto_search;

import com.vendo.auto_search_service.port.auto_search.AutoSearchCommandPort;
import com.vendo.auto_search_service.port.auto_search.usecase.AutoSearchExpirationUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
class AutoSearchExpirationService implements AutoSearchExpirationUseCase {

    private final AutoSearchCommandPort commandPort;

    @Override
    public void expireOutdatedRequests() {
        long expiredCount = commandPort.expireOutdatedRequests(LocalDateTime.now());

        if (expiredCount > 0) {
            log.info("Expired {} outdated auto search request(s).", expiredCount);
        }
    }
}
