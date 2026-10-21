package com.vendo.auto_search_service.adapter.auto_search.in.dto;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record UpdateAutoSearchRequest(

        UpdateSearchStatus status,
        LocalDateTime expirationDate

) {
}
