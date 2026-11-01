package com.vendo.auto_search_service.application.search.command;

import com.vendo.auto_search_service.adapter.auto_search.in.dto.PriceRangeFilter;
import com.vendo.auto_search_service.domain.auto_search.AutoSearch;
import com.vendo.auto_search_service.shared.Address;
import com.vendo.core_lib.utils.ObjectUtils;
import com.vendo.core_lib.utils.StringUtils;
import lombok.Builder;

import java.util.Set;

@Builder
public record SearchRequestCommand(
        String categoryId,
        Set<String> ids,

        Address addressFilter,
        PriceRangeFilter priceRangeFilter,

        Integer page,
        Integer size,
        SortCommand sort
) {

    public static SearchRequestCommandBuilder withFilters(AutoSearch autoSearch) {
        SearchRequestCommandBuilder builder = SearchRequestCommand.builder();

        if (ObjectUtils.isAnyNonNull(autoSearch.minPrice(), autoSearch.maxPrice())) {
            builder.priceRangeFilter(PriceRangeFilter.from(autoSearch.minPrice(), autoSearch.maxPrice()));
        }

        if (ObjectUtils.isNotNull(autoSearch.address()) && !StringUtils.isEmpty(autoSearch.address().city())) {
            builder.addressFilter(Address.from(autoSearch.address().city()));
        }

        return builder.categoryId(autoSearch.categoryId());
    }

}
