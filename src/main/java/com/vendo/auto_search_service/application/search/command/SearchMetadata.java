package com.vendo.auto_search_service.application.search.command;

public record SearchMetadata(
        int page,
        int size,
        long totalPages,
        long totalElements,
        boolean hasPrevious,
        boolean hasNext
) {

    public static SearchMetadata empty(int page, int size) {
        return new SearchMetadata(page, size, 0, 0, false, false);
    }

}
