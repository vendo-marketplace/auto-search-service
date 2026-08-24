package com.vendo.auto_search_service.application.auto_search;

import com.vendo.auto_search_service.application.auto_search.command.AutoSearchDataCommand;
import com.vendo.auto_search_service.domain.auto_search.AutoSearch;
import com.vendo.auto_search_service.infrastructure.props.AutoSearchSchedulerProps;
import com.vendo.auto_search_service.port.auto_search.AutoSearchCommandPort;
import com.vendo.auto_search_service.port.auto_search.AutoSearchQueryPort;
import com.vendo.auto_search_service.port.auto_search.usecase.AutoSearchExpirationUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
class AutoSearchExpirationService implements AutoSearchExpirationUseCase {

    private final AutoSearchQueryPort queryPort;
    private final AutoSearchCommandPort commandPort;

    private final AutoSearchSchedulerProps schedulerProps;

    @Override
    public void expireOutdatedRequests() {
        List<AutoSearch> outdated = collectOutdatedRequests();
        outdated.forEach(autoSearch -> commandPort.update(autoSearch.id(), autoSearch.expire()));

        if (!outdated.isEmpty()) {
            log.info("Expired {} outdated auto search request(s).", outdated.size());
        }
    }

    private List<AutoSearch> collectOutdatedRequests() {
        LocalDateTime now = LocalDateTime.now();
        List<AutoSearch> outdated = new ArrayList<>();

        int page = 0;
        AutoSearchDataCommand result;
        do {
            result = queryPort.findActiveRequests(page++, schedulerProps.getPageSize());
            result.data().stream()
                    .filter(autoSearch -> autoSearch.isOutdated(now))
                    .forEach(outdated::add);
        } while (result.hasNext());

        return outdated;
    }
}
