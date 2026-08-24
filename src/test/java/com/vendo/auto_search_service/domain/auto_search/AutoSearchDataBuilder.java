package com.vendo.auto_search_service.domain.auto_search;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Set;

public class AutoSearchDataBuilder {

    public static AutoSearch.AutoSearchBuilder withAllFields() {
        return AutoSearch.builder()
                .id("auto-search-id")
                .userId("user-id")
                .categoryId("category-id")
                .minPrice(BigDecimal.TEN)
                .maxPrice(BigDecimal.valueOf(100))
                .address("Kyiv")
                .status(SearchStatus.ACTIVE)
                .expirationDate(LocalDateTime.now().plusDays(3))
                .products(Set.of())
                .createdAt(Instant.now())
                .updatedAt(Instant.now());
    }
}
