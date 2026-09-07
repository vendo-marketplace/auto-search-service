package com.vendo.auto_search_service.adapter.search.out.mapper;

import com.vendo.auto_search_service.adapter.search.in.dto.SearchRequest;
import com.vendo.auto_search_service.adapter.search.in.dto.SearchResponse;
import com.vendo.auto_search_service.application.search.command.SearchRequestCommand;
import com.vendo.auto_search_service.application.search.command.SearchResponseCommand;
import com.vendo.auto_search_service.infrastructure.mapper.MapStructConfig;
import org.mapstruct.Mapper;

@Mapper(config = MapStructConfig.class)
public interface SearchMapper {

    SearchRequest toRequest(SearchRequestCommand request);

    SearchResponseCommand toResponse(SearchResponse response);

}
