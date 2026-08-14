package com.vendo.auto_search_service.adapter.auto_search.in.dto;

import com.vendo.auto_search_service.domain.auto_search.SearchStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class UpdateAutoSearchRequestDataBuilder {

    public static UpdateAutoSearchRequest.UpdateAutoSearchRequestBuilder withAllFields() {
        return UpdateAutoSearchRequest.builder()
                .categoryId("category-id")
                .minPrice(BigDecimal.TEN)
                .maxPrice(BigDecimal.valueOf(100))
                .address("Kyiv")
                .status(SearchStatus.ACTIVE)
                .expirationDate(LocalDateTime.now().plusDays(3));
    }
}
