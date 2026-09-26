package com.vendo.auto_search_service.application.search.command;

import com.vendo.auto_search_service.adapter.auto_search.in.dto.PriceRangeFilter;
import com.vendo.auto_search_service.shared.Address;
import lombok.Builder;

import java.util.Set;

@Builder
public record SearchRequestCommand(
        String categoryId,
        Address address,
        Set<String> ids,
        PriceRangeFilter priceRangeFilter
) {
}
