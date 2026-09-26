package com.vendo.auto_search_service.adapter.auto_search.out.persistence;

import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.ListPagingAndSortingRepository;

import java.util.List;

public interface MongoAutoSearchRepository extends ListCrudRepository<MongoAutoSearch, String>, ListPagingAndSortingRepository<MongoAutoSearch, String> {

    List<MongoAutoSearch> findAllByOwner_Id(String ownerId);

}
