package com.vendo.auto_search_service.domain.auto_search;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Set;

public record AutoSearch(
        String id,

        String userId,

        String categoryId,
        BigDecimal minPrice,
        BigDecimal maxPrice,
        String address,

        SearchStatus status,

        LocalDateTime expirationDate,
        Set<String> notifiedProducts,

        Instant createdAt,
        Instant updatedAt
) {
}
