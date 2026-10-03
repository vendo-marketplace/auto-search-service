package com.vendo.auto_search_service.domain.auto_search.exception;

public class AutoSearchLimitExceededException extends RuntimeException {
    public AutoSearchLimitExceededException(String message) {
        super(message);
    }
}
