package com.vendo.auto_search_service.adapter.auto_search.out.persistence;

import com.vendo.auto_search_service.adapter.auto_search.out.mapper.AutoSearchMapper;
import com.vendo.auto_search_service.domain.auto_search.AutoSearch;
import com.vendo.auto_search_service.domain.auto_search.SearchStatus;
import com.vendo.auto_search_service.domain.auto_search.exception.AutoSearchNotFoundException;
import com.vendo.auto_search_service.port.auto_search.AutoSearchCommandPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
class AutoSearchCommandAdapter implements AutoSearchCommandPort {

    private final AutoSearchMapper mapper;
    private final MongoAutoSearchRepository repository;

    @Override
    public void save(AutoSearch autoSearch) {
        repository.save(mapper.toEntity(autoSearch));
    }

    @Override
    public void update(String id, AutoSearch autoSearch) {
        MongoAutoSearch entity = findOrThrow(id);
        mapper.updateEntity(entity, autoSearch);
        repository.save(entity);
    }

    @Override
    public void delete(String id) {
        MongoAutoSearch entity = findOrThrow(id);
        repository.delete(entity);
    }

    @Override
    public long expireOutdatedRequests(LocalDateTime referenceTime) {
        return repository.updateStatusForOutdatedRequests(SearchStatus.ACTIVE, referenceTime, SearchStatus.EXPIRED, Instant.now());
    }

    private MongoAutoSearch findOrThrow(String id) {
        return repository.findById(id)
                .orElseThrow(() -> new AutoSearchNotFoundException("Auto search request not found."));
    }
}
