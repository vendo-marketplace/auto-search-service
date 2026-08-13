package com.vendo.auto_search_service.application.auto_search.command;

import com.vendo.auto_search_service.domain.auto_search.AutoSearch;

import java.util.List;

public record AutoSearchDataCommand(List<AutoSearch> data, boolean hasNext) {
}
