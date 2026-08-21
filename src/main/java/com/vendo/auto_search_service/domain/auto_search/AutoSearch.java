package com.vendo.auto_search_service.domain.auto_search;

import com.vendo.auto_search_service.domain.auto_search.exception.InvalidExpirationDateException;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

@Builder(toBuilder = true)
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

    public static void validateExpirationDate(LocalDateTime expirationDate, int minDays, int maxDays) {
        LocalDate today = LocalDate.now(), expirationLocalDate = expirationDate.toLocalDate();
        LocalDate earliest = today.plusDays(minDays), latest = today.plusDays(maxDays);

        if (expirationLocalDate.isBefore(earliest) || expirationLocalDate.isAfter(latest)) {
            throw new InvalidExpirationDateException(
                    "Expiration date must be at least a day after today and not later than a week from now."
            );
        }
    }

    public AutoSearch fromNew(String userId, LocalDateTime expirationDate) {
        return this.toBuilder()
                .userId(userId)
                .status(SearchStatus.ACTIVE)
                .expirationDate(expirationDate)
                .notifiedProducts(Set.of())
                .build();
    }
}
