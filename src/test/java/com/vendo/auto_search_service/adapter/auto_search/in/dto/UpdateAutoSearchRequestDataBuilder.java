package com.vendo.auto_search_service.adapter.auto_search.in.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class UpdateAutoSearchRequestDataBuilder {

    public static UpdateAutoSearchRequest.UpdateAutoSearchRequestBuilder withAllFields() {
        return UpdateAutoSearchRequest.builder()
                .categoryId("category-id")
                .priceRange(PriceRangeFilter.builder()
                        .minPrice(BigDecimal.TEN)
                        .maxPrice(BigDecimal.valueOf(100))
                        .build())
                .address("Kyiv")
                .status(UpdateSearchStatus.ACTIVE)
                .expirationDate(LocalDateTime.now().plusDays(3));
    }

}
