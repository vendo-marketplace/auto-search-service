package com.vendo.auto_search_service.adapter.auto_search.in.dto;

import com.vendo.auto_search_service.domain.auto_search.SearchStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;

public record AutoSearchResponse(
        String id,
        String categoryId,
        BigDecimal minPrice,
        BigDecimal maxPrice,
        String address,
        SearchStatus status,
        LocalDateTime expirationDate,
        Instant createdAt,
        Instant updatedAt
) {
}
