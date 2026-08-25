package com.vendo.auto_search_service.adapter.auto_search.out.persistence;

import com.vendo.auto_search_service.adapter.auto_search.out.mapper.AutoSearchMapper;
import com.vendo.auto_search_service.application.auto_search.command.AutoSearchDataCommand;
import com.vendo.auto_search_service.domain.auto_search.AutoSearch;
import com.vendo.auto_search_service.domain.auto_search.SearchStatus;
import com.vendo.auto_search_service.domain.auto_search.exception.AutoSearchNotFoundException;
import com.vendo.auto_search_service.port.auto_search.AutoSearchQueryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
class AutoSearchQueryAdapter implements AutoSearchQueryPort {

    private final AutoSearchMapper mapper;
    private final MongoAutoSearchRepository repository;

    @Override
    public List<AutoSearch> findByUserId(String userId) {
        List<MongoAutoSearch> entities = repository.findAllByUserId(userId);
        return mapper.toAutoSearches(entities);
    }

    @Override
    public AutoSearch findById(String id) {
        return repository.findById(id)
                .map(mapper::toAutoSearch)
                .orElseThrow(() -> new AutoSearchNotFoundException("Auto search request not found."));
    }

    @Override
    public AutoSearchDataCommand findOutdatedActiveRequests(LocalDateTime referenceTime, int page, int size) {
        Page<MongoAutoSearch> paged = repository.findAllByStatusAndExpirationDateBefore(
                SearchStatus.ACTIVE, referenceTime, PageRequest.of(page, size));
        return new AutoSearchDataCommand(mapper.toAutoSearches(paged.toList()), paged.hasNext());
    }
}
