package com.vendo.auto_search_service.application.auto_search;

import com.vendo.auto_search_service.domain.auto_search.AutoSearch;
import com.vendo.auto_search_service.port.auto_search.usecase.AutoSearchCommandUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
class AutoSearchCommandService implements AutoSearchCommandUseCase {

    @Override
    public void create(AutoSearch autoSearch) {

    }

    @Override
    public void update(String id, AutoSearch autoSearch) {

    }

    @Override
    public void delete(String id) {

    }
}
