package com.vendo.auto_search_service.domain.auto_search;

import com.vendo.auto_search_service.domain.auto_search.nested.Owner;
import com.vendo.auto_search_service.domain.auto_search.type.SearchStatus;
import com.vendo.auto_search_service.shared.Address;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Set;

public class AutoSearchDataBuilder {

    public static AutoSearch.AutoSearchBuilder withAllFields() {
        Address address = new Address("region", "city", new Address.Location(1,2));

        return AutoSearch.builder()
                .id("auto-search-id")
                .owner(Owner.from("user-id", "user-email"))
                .categoryId("category-id")
                .minPrice(BigDecimal.TEN)
                .maxPrice(BigDecimal.valueOf(100))
                .address(address)
                .status(SearchStatus.ACTIVE)
                .expirationDate(LocalDateTime.now().plusDays(3))
                .products(Set.of("id1", "id2", "id3"))
                .createdAt(Instant.now())
                .updatedAt(Instant.now());
    }
}
