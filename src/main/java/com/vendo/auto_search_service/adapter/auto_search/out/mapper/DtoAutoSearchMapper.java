package com.vendo.auto_search_service.adapter.auto_search.out.mapper;

import com.vendo.auto_search_service.adapter.auto_search.in.dto.AutoSearchResponse;
import com.vendo.auto_search_service.adapter.auto_search.in.dto.CreateAutoSearchRequest;
import com.vendo.auto_search_service.adapter.auto_search.in.dto.UpdateAutoSearchRequest;
import com.vendo.auto_search_service.domain.auto_search.AutoSearch;
import com.vendo.auto_search_service.infrastructure.config.MapStructConfig;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(config = MapStructConfig.class)
public interface DtoAutoSearchMapper {

    @Mapping(target = "minPrice", source = "priceRange.minPrice")
    @Mapping(target = "maxPrice", source = "priceRange.maxPrice")
    AutoSearch toEntity(CreateAutoSearchRequest request);

    @Mapping(target = "minPrice", source = "priceRange.minPrice")
    @Mapping(target = "maxPrice", source = "priceRange.maxPrice")
    AutoSearch toEntity(UpdateAutoSearchRequest request);

    List<AutoSearchResponse> toResponses(List<AutoSearch> autoSearches);

}
