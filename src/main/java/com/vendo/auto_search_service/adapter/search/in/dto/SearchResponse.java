package com.vendo.auto_search_service.adapter.search.in.dto;

import com.vendo.auto_search_service.domain.product.Product;

import java.util.List;

public record SearchResponse(List<Product> data) {
}
