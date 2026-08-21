package com.vendo.auto_search_service.adapter.auto_search.in.dto;

import com.vendo.auto_search_service.adapter.auto_search.in.validation.PriceRange;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class UpdateAutoSearchRequestDataBuilder {

    public static UpdateAutoSearchRequest.UpdateAutoSearchRequestBuilder withAllFields() {
        return UpdateAutoSearchRequest.builder()
                .categoryId("category-id")
                .priceRange(PriceRange.builder()
                        .minPrice(BigDecimal.TEN)
                        .maxPrice(BigDecimal.valueOf(100))
                        .build())
                .address("Kyiv")
                .status(UpdateSearchStatus.ACTIVE)
                .expirationDate(LocalDateTime.now().plusDays(3));
    }

}
