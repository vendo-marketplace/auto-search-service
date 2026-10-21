package com.vendo.auto_search_service.adapter.auto_search.in.dto;

import lombok.Builder;

import java.time.LocalDate;

@Builder
public record UpdateAutoSearchRequest(

        UpdateSearchStatus status,
        LocalDate expirationDate

) {
}
