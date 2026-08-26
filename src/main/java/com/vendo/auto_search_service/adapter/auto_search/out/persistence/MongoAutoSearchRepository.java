package com.vendo.auto_search_service.adapter.auto_search.out.persistence;

import com.vendo.auto_search_service.domain.auto_search.SearchStatus;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.Update;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.ListPagingAndSortingRepository;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

public interface MongoAutoSearchRepository extends ListCrudRepository<MongoAutoSearch, String>, ListPagingAndSortingRepository<MongoAutoSearch, String> {

    List<MongoAutoSearch> findAllByUserId(String userId);

    @Query("{ 'status': ?0, 'expirationDate': { '$lt': ?1 } }")
    @Update("{ '$set': { 'status': ?2, 'updatedAt': ?3 } }")
    long updateStatusForOutdatedRequests(SearchStatus currentStatus, LocalDateTime expirationDateBefore, SearchStatus newStatus, Instant updatedAt);
}
