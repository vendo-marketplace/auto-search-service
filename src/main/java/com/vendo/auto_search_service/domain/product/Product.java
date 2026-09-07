package com.vendo.auto_search_service.domain.product;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public record Product(
        String id,
        String title,
        Integer quantity,
        BigDecimal price,

        String ownerId,
        String categoryId,

        Boolean isNew,
        Boolean active,

        List<String> images,

        Instant createdAt

) {

    public static Set<String> getProductIds(List<Product> data) {
        return data.stream().map(Product::id).collect(Collectors.toSet());
    }

}
