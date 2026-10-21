package com.vendo.auto_search_service.application.search.command;

public record SortCommand(ProductSortField sortBy, SortDirection direction) {
}
