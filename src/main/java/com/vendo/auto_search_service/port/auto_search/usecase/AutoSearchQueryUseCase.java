package com.vendo.auto_search_service.port.auto_search.usecase;

import com.vendo.auto_search_service.domain.auto_search.AutoSearch;

import java.util.List;

public interface AutoSearchQueryUseCase {

    List<AutoSearch> getUserRequests();

}
