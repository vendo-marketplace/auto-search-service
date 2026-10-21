package com.vendo.auto_search_service.adapter.auto_search.in.dto;

import java.time.LocalDate;

public class UpdateAutoSearchRequestDataBuilder {

    public static UpdateAutoSearchRequest.UpdateAutoSearchRequestBuilder withAllFields() {
        return UpdateAutoSearchRequest.builder()
                .status(UpdateSearchStatus.ACTIVE)
                .expirationDate(LocalDate.now().plusDays(3));
    }

}
