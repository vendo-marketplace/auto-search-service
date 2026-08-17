package com.vendo.auto_search_service.domain.auto_search.exception;

public class InvalidExpirationDateException extends RuntimeException {
    public InvalidExpirationDateException(String message) {
        super(message);
    }
}
