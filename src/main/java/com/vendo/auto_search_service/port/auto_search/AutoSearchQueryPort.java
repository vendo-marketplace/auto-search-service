package com.vendo.auto_search_service.port.auto_search;

import com.vendo.auto_search_service.application.auto_search.command.AutoSearchDataCommand;
import com.vendo.auto_search_service.domain.auto_search.AutoSearch;

import java.util.List;

public interface AutoSearchQueryPort {

    List<AutoSearch> findByUserId(String userId);

    AutoSearchDataCommand findActiveRequests(int page, int size);

}
