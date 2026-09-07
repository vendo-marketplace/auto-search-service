package com.vendo.auto_search_service.port.search;

import com.vendo.auto_search_service.application.search.command.SearchRequestCommand;
import com.vendo.auto_search_service.application.search.command.SearchResponseCommand;

public interface SearchPort {

    SearchResponseCommand search(SearchRequestCommand request);

}
