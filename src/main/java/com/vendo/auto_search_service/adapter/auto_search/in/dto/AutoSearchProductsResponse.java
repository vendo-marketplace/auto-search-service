package com.vendo.auto_search_service.adapter.auto_search.in.dto;

import com.vendo.auto_search_service.domain.product.Product;

import java.util.List;

public record AutoSearchProductsResponse(List<Product> data) {

    public static AutoSearchProductsResponse from(List<Product> data) {
        return new AutoSearchProductsResponse(data);
    }

}
