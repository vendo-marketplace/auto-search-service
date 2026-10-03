package com.vendo.auto_search_service.adapter.auto_search.out.persistence;

import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.Update;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.ListPagingAndSortingRepository;

import java.util.List;
import java.util.Set;

public interface MongoAutoSearchRepository extends ListCrudRepository<MongoAutoSearch, String>, ListPagingAndSortingRepository<MongoAutoSearch, String> {

    List<MongoAutoSearch> findAllByOwner_Id(String ownerId);

    @Query("{ '_id': ?0 }")
    @Update("{ '$addToSet': { 'products': { '$each': ?1 } } }")
    void addProducts(String id, Set<String> productIds);

    @Query("{ '_id': ?0 }")
    @Update("{ '$addToSet': { 'notifiedProductIds': { '$each': ?1 } } }")
    void addNotifiedProductIds(String id, Set<String> productIds);

}
