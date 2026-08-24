package com.vendo.auto_search_service.adapter.search.out;

import com.vendo.auto_search_service.adapter.category.out.config.ProductFeignConfig;
import com.vendo.auto_search_service.adapter.search.in.dto.SearchRequest;
import com.vendo.auto_search_service.adapter.search.in.dto.SearchResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
        name = "search-service",
        path = "/search",
        configuration = ProductFeignConfig.class)
interface SearchClient {

    @PostMapping
    SearchResponse search(@RequestBody SearchRequest request);

}
