package com.vendo.auto_search_service.port.auto_search;

import com.vendo.auto_search_service.domain.auto_search.AutoSearch;

import java.time.LocalDateTime;

public interface AutoSearchCommandPort {

    void save(AutoSearch autoSearch);
    void update(String id, AutoSearch autoSearch);
    void delete(String id);

    long expireOutdatedRequests(LocalDateTime referenceTime);

}
