package com.vendo.auto_search_service.adapter.auto_search.in.dto;

import com.vendo.auto_search_service.domain.product.Product;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class ProductDataBuilder {

    public static Product withAllFields() {
        return new Product(
                String.valueOf(UUID.randomUUID()),
                "title",
                1,
                BigDecimal.ONE,
                String.valueOf(UUID.randomUUID()),
                String.valueOf(UUID.randomUUID()),
                true,
                true,
                List.of(),
                Instant.now()
        );
    }

}
