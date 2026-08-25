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
        List<AutoSearch> outdatedRequests = fetchAllOutdatedRequests();
        outdatedRequests.forEach(this::expire);

        if (!outdatedRequests.isEmpty()) {
            log.info("Expired {} outdated auto search request(s).", outdatedRequests.size());
        }
    }

    private List<AutoSearch> fetchAllOutdatedRequests() {
        LocalDateTime now = LocalDateTime.now();
        List<AutoSearch> outdatedRequests = new ArrayList<>();

        int pageNumber = 0;
        AutoSearchDataCommand page;
        do {
            page = queryPort.findOutdatedActiveRequests(now, pageNumber++, schedulerProps.getPageSize());
            outdatedRequests.addAll(page.data());
        } while (page.hasNext());

        return outdatedRequests;
    }

    private void expire(AutoSearch autoSearch) {
        commandPort.update(autoSearch.id(), autoSearch.expire());
    }
}
