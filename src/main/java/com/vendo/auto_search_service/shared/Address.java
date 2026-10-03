package com.vendo.auto_search_service.shared;

public record Address(String city) {

    public static Address from(String city) {
        return new Address(city);
    }


}