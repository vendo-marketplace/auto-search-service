package com.vendo.auto_search_service.adapter.search.in.dto;

import com.vendo.auto_search_service.adapter.auto_search.in.dto.PriceRangeFilter;

import java.util.List;

public record SearchRequest(
        String categoryId,
        String address,
        List<String> ids,
        PriceRangeFilter priceRangeFilter
) {
}
