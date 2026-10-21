package com.vendo.auto_search_service.application.auto_search.command;

import com.vendo.auto_search_service.application.search.command.SortCommand;

public record ProductPageRequest(Integer page, Integer size, SortCommand sort) {
}
