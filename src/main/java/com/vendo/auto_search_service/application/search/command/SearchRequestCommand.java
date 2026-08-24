package com.vendo.auto_search_service.application.search.command;

import com.vendo.auto_search_service.adapter.auto_search.in.dto.PriceRangeFilter;

public record SearchRequestCommand(

        String categoryId,
        String address,
        PriceRangeFilter priceRangeFilter

) {

    public static SearchRequestCommand from(String categoryId, String address, PriceRangeFilter priceRangeFilter) {
        return new SearchRequestCommand(categoryId, address, priceRangeFilter);
    }

}
