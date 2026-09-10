package com.vendo.auto_search_service.domain.auto_search.nested;

public record Owner(String id, String email) {

    public static Owner from(String id, String email) {
        return new Owner(id, email);
    }

}
