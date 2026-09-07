package com.vendo.auto_search_service.adapter.auto_search.in.dto;

import jakarta.validation.Valid;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record UpdateAutoSearchRequest(

        String categoryId,

        @Valid
        PriceRangeFilter priceRange,

        String address,

        UpdateSearchStatus status,

        LocalDateTime expirationDate

) {
}
