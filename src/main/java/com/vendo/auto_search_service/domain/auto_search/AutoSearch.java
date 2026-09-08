package com.vendo.auto_search_service.domain.auto_search;

import com.vendo.auto_search_service.domain.auto_search.exception.InvalidExpirationDateException;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.Instant;
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
        Set<String> products,

        Instant createdAt,
        Instant updatedAt
) {

    public static void validateExpirationDate(LocalDateTime expirationDate, int minHours, int maxDays) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime earliest = now.plusHours(minHours), latest = now.plusDays(maxDays);

        if (expirationDate.isBefore(earliest) || expirationDate.isAfter(latest)) {
            throw new InvalidExpirationDateException(
                    "Expiration date must be at least " + minHours + " hour(s) from now and not later than " + maxDays + " day(s) from now."
            );
        }
    }

    public AutoSearch toNew(String userId, LocalDateTime expirationDate) {
        return this.toBuilder()
                .userId(userId)
                .status(SearchStatus.ACTIVE)
                .expirationDate(expirationDate)
                .products(Set.of())
                .build();
    }
}
