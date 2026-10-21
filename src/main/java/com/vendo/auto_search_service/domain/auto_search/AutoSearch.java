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

        LocalDate expirationDate,
        Set<String> products,

        Instant createdAt,
        Instant updatedAt
) {

    public static void validateActiveRequestsLimit(long activeRequests, int maxActiveRequests) {
        if (activeRequests >= maxActiveRequests) {
            throw new AutoSearchLimitExceededException(
                    "You can have at most " + maxActiveRequests + " active auto search requests."
            );
        }
    }

    public static void validateExpirationDate(LocalDate expirationDate, int minDays, int maxDays) {
        LocalDate now = LocalDate.now();
        LocalDate earliest = now.plusDays(minDays), latest = now.plusDays(maxDays);

        if (expirationDate.isBefore(earliest) || expirationDate.isAfter(latest)) {
            throw new InvalidExpirationDateException(
                    "Expiration date must be at least " + minDays + " day(s) from now and not later than " + maxDays + " day(s) from now."
            );
        }
    }

    public AutoSearch toNew(Owner owner, LocalDate expirationDate) {
        return this.toBuilder()
                .owner(owner)
                .status(SearchStatus.ACTIVE)
                .expirationDate(expirationDate)
                .products(Set.of())
                .build();
    }
}
