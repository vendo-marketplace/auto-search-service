package com.vendo.auto_search_service.adapter.auto_search.in.dto;

import com.vendo.auto_search_service.adapter.auto_search.in.validation.PriceRange;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record CreateAutoSearchRequest(

        @NotBlank(message = "Category ID is required.")
        String categoryId,

        @Valid
        PriceRange priceRange,

        String address,

        LocalDateTime expirationDate

) {
}
