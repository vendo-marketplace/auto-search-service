package com.vendo.auto_search_service;

public record AutoSearchMatchingEvent(String autoSearchId) {

    public static AutoSearchMatchingEvent from(String autoSearchId) {
        return new AutoSearchMatchingEvent(autoSearchId);
    }

}
