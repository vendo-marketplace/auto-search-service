package com.vendo.auto_search_service.adapter.auto_search.in.dto;

import com.vendo.auto_search_service.shared.Address;

import java.math.BigDecimal;
import java.time.LocalDate;

public class CreateAutoSearchRequestDataBuilder {

    public static CreateAutoSearchRequest.CreateAutoSearchRequestBuilder withAllFields() {
        Address address = new Address("city");

        return CreateAutoSearchRequest.builder()
                .categoryId("category-id")
                .priceRange(PriceRangeFilter.builder()
                        .minPrice(BigDecimal.TEN)
                        .maxPrice(BigDecimal.valueOf(100))
                        .build())
                .address(address)
                .expirationDate(LocalDate.now().plusDays(3));
    }
}
