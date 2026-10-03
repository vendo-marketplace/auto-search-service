package com.vendo.auto_search_service.domain.auto_search;

import com.vendo.auto_search_service.domain.auto_search.exception.AutoSearchLimitExceededException;
import com.vendo.auto_search_service.domain.auto_search.exception.InvalidExpirationDateException;
import com.vendo.auto_search_service.shared.Address;
import com.vendo.auto_search_service.domain.auto_search.nested.Owner;
import com.vendo.auto_search_service.domain.auto_search.type.SearchStatus;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

@Builder(toBuilder = true)
public record AutoSearch(
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

    public static final int MAX_ACTIVE_REQUESTS = 3;

    public static void validateActiveRequestsLimit(long activeRequests) {
        if (activeRequests >= MAX_ACTIVE_REQUESTS) {
            throw new AutoSearchLimitExceededException(
                    "You can have at most " + MAX_ACTIVE_REQUESTS + " active auto search requests."
            );
        }
    }

    public static void validateExpirationDate(LocalDateTime expirationDate, int minDays, int maxDays) {
        LocalDate today = LocalDate.now(), expirationLocalDate = expirationDate.toLocalDate();
        LocalDate earliest = today.plusDays(minDays), latest = today.plusDays(maxDays);

        if (expirationLocalDate.isBefore(earliest) || expirationLocalDate.isAfter(latest)) {
            throw new InvalidExpirationDateException(
                    "Expiration date must be at least a day after today and not later than a week from now."
            );
        }
    }

    public AutoSearch toNew(Owner owner, LocalDateTime expirationDate) {
        return this.toBuilder()
                .owner(owner)
                .status(SearchStatus.ACTIVE)
                .expirationDate(expirationDate)
                .products(Set.of())
                .build();
    }

    public Set<String> collectProducts(String productId) {
        products.add(productId);
        return products;
    }
}
