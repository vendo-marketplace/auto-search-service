package com.vendo.auto_search_service.adapter.auto_search.persistence;

import com.vendo.auto_search_service.domain.auto_search.SearchStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.ListPagingAndSortingRepository;

import java.util.List;

public interface MongoAutoSearchRepository extends ListCrudRepository<MongoAutoSearch, String>, ListPagingAndSortingRepository<MongoAutoSearch, String> {

    List<MongoAutoSearch> findAllByUserId(String userId);

    Page<MongoAutoSearch> findAllByStatus(SearchStatus status, Pageable pageable);
}
