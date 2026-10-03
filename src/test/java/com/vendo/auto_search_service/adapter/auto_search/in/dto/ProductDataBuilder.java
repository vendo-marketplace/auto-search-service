package com.vendo.auto_search_service.adapter.auto_search.in.dto;

import com.vendo.auto_search_service.domain.product.Product;
import com.vendo.auto_search_service.shared.Address;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class ProductDataBuilder {

    public static Product.ProductBuilder withAllFields() {
        return Product.builder()
                .id(String.valueOf(UUID.randomUUID()))
                .title("title")
                .price(BigDecimal.ONE)
                .address(new Address("city"))
                .categoryId(String.valueOf(UUID.randomUUID()))
                .ownerId(String.valueOf(UUID.randomUUID()))
                .active(true)
                .isNew(true)
                .imageKeys(List.of())
                .createdAt(Instant.now());
    }

}
