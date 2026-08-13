package com.vendo.auto_search_service.port.auto_search.usecase;

import com.vendo.auto_search_service.domain.auto_search.AutoSearch;

public interface AutoSearchCommandUseCase {

    void create(AutoSearch autoSearch);

    void update(String id, AutoSearch autoSearch);

    void delete(String id);

}
