package com.vendo.auto_search_service.adapter.auto_search.out.mapper;

import com.vendo.auto_search_service.adapter.auto_search.in.dto.AutoSearchResponse;
import com.vendo.auto_search_service.adapter.auto_search.in.dto.CreateAutoSearchRequest;
import com.vendo.auto_search_service.adapter.auto_search.in.dto.UpdateAutoSearchRequest;
import com.vendo.auto_search_service.domain.auto_search.AutoSearch;
import com.vendo.auto_search_service.infrastructure.config.MapStructConfig;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(config = MapStructConfig.class)
public interface DtoAutoSearchMapper {

    AutoSearch toEntity(CreateAutoSearchRequest request);

    AutoSearch toEntity(UpdateAutoSearchRequest request);

    AutoSearchResponse toResponse(AutoSearch autoSearch);

    List<AutoSearchResponse> toResponses(List<AutoSearch> autoSearches);

}
