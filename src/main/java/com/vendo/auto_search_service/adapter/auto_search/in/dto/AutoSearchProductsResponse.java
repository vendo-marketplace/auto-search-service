package com.vendo.auto_search_service.adapter.auto_search.in.dto;

import com.vendo.auto_search_service.application.search.command.SearchMetadata;
import com.vendo.auto_search_service.application.search.command.SearchResponseCommand;
import com.vendo.auto_search_service.domain.product.Product;

import java.util.List;

public record AutoSearchProductsResponse(List<Product> data, SearchMetadata metadata) {

    public static AutoSearchProductsResponse from(SearchResponseCommand response) {
        return new AutoSearchProductsResponse(response.data(), response.metadata());
    }

}
