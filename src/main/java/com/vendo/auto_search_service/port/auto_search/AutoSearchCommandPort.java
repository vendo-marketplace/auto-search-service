package com.vendo.auto_search_service.port.auto_search;

import com.vendo.auto_search_service.domain.auto_search.AutoSearch;

import java.time.LocalDateTime;
import java.util.Set;

public interface AutoSearchCommandPort {

    String save(AutoSearch autoSearch);
    void update(String id, AutoSearch autoSearch);
    void addProducts(String id, Set<String> productIds);
    void addNotifiedProducts(String id, Set<String> productIds);
    void delete(String id);

    long expireOutdatedRequests(LocalDateTime referenceTime);

}
