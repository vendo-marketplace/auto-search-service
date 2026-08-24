package com.vendo.auto_search_service.adapter.search.in.dto;

import com.vendo.auto_search_service.adapter.auto_search.in.dto.PriceRangeFilter;

public record SearchRequest(
        String categoryId,
        String address,
        PriceRangeFilter priceRangeFilter
) {
}
