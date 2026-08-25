package com.vendo.auto_search_service.port.auto_search;

import com.vendo.auto_search_service.application.auto_search.command.AutoSearchDataCommand;
import com.vendo.auto_search_service.domain.auto_search.AutoSearch;

import java.time.LocalDateTime;
import java.util.List;

public interface AutoSearchQueryPort {

    List<AutoSearch> findByUserId(String userId);

    AutoSearch findById(String id);

    AutoSearchDataCommand findOutdatedActiveRequests(LocalDateTime referenceTime, int page, int size);

}
