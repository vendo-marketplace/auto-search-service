package com.vendo.auto_search_service.adapter.auto_search.in.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class CreateAutoSearchRequestDataBuilder {

    public static CreateAutoSearchRequest.CreateAutoSearchRequestBuilder withAllFields() {
        return CreateAutoSearchRequest.builder()
                .categoryId("category-id")
                .minPrice(BigDecimal.TEN)
                .maxPrice(BigDecimal.valueOf(100))
                .address("Kyiv")
                .expirationDate(LocalDateTime.now().plusDays(3));
    }
}
