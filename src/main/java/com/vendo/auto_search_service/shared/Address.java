package com.vendo.auto_search_service.shared;

public record Address(
        String region,
        String city,
        Location location
) {

    public record Location(
            double lat,
            double lon
    ) {}

}