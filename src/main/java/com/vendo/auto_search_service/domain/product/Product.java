package com.vendo.auto_search_service.domain.product;

import com.vendo.auto_search_service.shared.Address;
import com.vendo.core_lib.utils.ObjectUtils;
import com.vendo.core_lib.utils.StringUtils;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Builder
public record Product(
        String id,
        String title,
        Integer quantity,
        BigDecimal price,
        Address address,

        String ownerId,
        String categoryId,

        Boolean isNew,
        Boolean active,

        List<String> images,

        Instant createdAt
) {

    public boolean isOwner(String ownerId) {
        return StringUtils.isNotEmpty(this.ownerId) && this.ownerId.equals(ownerId);
    }

    public static Set<String> getExcludedUserProductIds(String userId, List<Product> data) {
        return data.stream()
                .filter(product -> ObjectUtils.isNotNull(product) && !StringUtils.isEmpty(product.ownerId))
                .filter(product -> !product.ownerId.equals(userId))
                .map(Product::id)
                .collect(Collectors.toSet());
    }
}
