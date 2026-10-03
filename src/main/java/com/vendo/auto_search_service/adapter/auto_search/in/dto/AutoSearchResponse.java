package com.vendo.auto_search_service.adapter.auto_search.in.dto;

import com.vendo.auto_search_service.domain.auto_search.nested.Owner;
import com.vendo.auto_search_service.domain.auto_search.type.SearchStatus;
import com.vendo.auto_search_service.shared.Address;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Set;

public record AutoSearchResponse(
        String id,

        Owner owner,

        String categoryId,
        BigDecimal minPrice,
        BigDecimal maxPrice,
        Address address,

        SearchStatus status,

        LocalDateTime expirationDate,
        Set<String> products,

        Instant createdAt,
        Instant updatedAt
) {
}
