package com.vendo.auto_search_service.application.search.command;

public record SearchMetadata(
        Integer page,
        Integer size,
        long totalPages,
        long totalElements,
        boolean hasPrevious,
        boolean hasNext
) {

    public static SearchMetadata empty(Integer page, Integer size) {
        return new SearchMetadata(page, size, 0, 0, false, false);
    }

}
