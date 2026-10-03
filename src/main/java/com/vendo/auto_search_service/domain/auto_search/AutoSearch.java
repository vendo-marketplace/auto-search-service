package com.vendo.auto_search_service.domain.auto_search;

import com.vendo.auto_search_service.domain.auto_search.exception.InvalidExpirationDateException;
import com.vendo.auto_search_service.shared.Address;
import com.vendo.auto_search_service.domain.auto_search.nested.Owner;
import com.vendo.auto_search_service.domain.auto_search.type.SearchStatus;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.Instant;
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

    public static void validateExpirationDate(LocalDateTime expirationDate, int minHours, int maxDays) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime earliest = now.plusHours(minHours), latest = now.plusDays(maxDays);

        if (expirationDate.isBefore(earliest) || expirationDate.isAfter(latest)) {
            throw new InvalidExpirationDateException(
                    "Expiration date must be at least " + minHours + " hour(s) from now and not later than " + maxDays + " day(s) from now."
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
