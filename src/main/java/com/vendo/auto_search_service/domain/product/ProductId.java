package com.vendo.auto_search_service.domain.product;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public record ProductId(String id) {

    public static Set<String> getProductIds(List<ProductId> data) {
        return data.stream().map(ProductId::id).collect(Collectors.toSet());
    }

}
