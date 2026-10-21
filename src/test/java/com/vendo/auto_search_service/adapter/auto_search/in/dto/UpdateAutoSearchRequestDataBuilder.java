package com.vendo.auto_search_service.adapter.auto_search.in.dto;

import java.time.LocalDateTime;

public class UpdateAutoSearchRequestDataBuilder {

    public static UpdateAutoSearchRequest.UpdateAutoSearchRequestBuilder withAllFields() {
        return UpdateAutoSearchRequest.builder()
                .status(UpdateSearchStatus.ACTIVE)
                .expirationDate(LocalDateTime.now().plusDays(3));
    }

}
