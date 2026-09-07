package com.vendo.auto_search_service.adapter.search.out;

import com.vendo.auto_search_service.adapter.search.in.dto.SearchResponse;
import com.vendo.auto_search_service.adapter.search.out.mapper.SearchMapper;
import com.vendo.auto_search_service.application.search.command.SearchRequestCommand;
import com.vendo.auto_search_service.application.search.command.SearchResponseCommand;
import com.vendo.auto_search_service.port.search.SearchPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
class SearchAdapter implements SearchPort {

    private final SearchClient client;
    private final SearchMapper mapper;

    @Override
    public SearchResponseCommand search(SearchRequestCommand request) {
        SearchResponse response = client.search(mapper.toRequest(request));
        return mapper.toResponse(response);
    }

}
