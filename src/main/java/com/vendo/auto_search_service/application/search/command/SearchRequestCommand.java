package com.vendo.auto_search_service.application.search.command;

import com.vendo.auto_search_service.adapter.auto_search.in.dto.PriceRangeFilter;
import lombok.Builder;

import java.util.Set;

@Builder
public record SearchRequestCommand(
        String categoryId,
        String address,
        Set<String> ids,
        PriceRangeFilter priceRangeFilter
) {
}
