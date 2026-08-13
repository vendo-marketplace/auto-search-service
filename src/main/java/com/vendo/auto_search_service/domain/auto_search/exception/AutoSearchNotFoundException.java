package com.vendo.auto_search_service.domain.auto_search.exception;

public class AutoSearchNotFoundException extends RuntimeException {
    public AutoSearchNotFoundException(String message) {
        super(message);
    }
}
