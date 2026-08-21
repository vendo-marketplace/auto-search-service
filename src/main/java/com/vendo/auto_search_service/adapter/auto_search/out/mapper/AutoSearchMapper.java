package com.vendo.auto_search_service.adapter.auto_search.out.mapper;

import com.vendo.auto_search_service.adapter.auto_search.out.persistence.MongoAutoSearch;
import com.vendo.auto_search_service.domain.auto_search.AutoSearch;
import com.vendo.auto_search_service.infrastructure.config.MapStructConfig;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(config = MapStructConfig.class)
public interface AutoSearchMapper {

    MongoAutoSearch toEntity(AutoSearch autoSearch);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntity(@MappingTarget MongoAutoSearch entity, AutoSearch autoSearch);

    AutoSearch toAutoSearch(MongoAutoSearch entity);

    List<AutoSearch> toAutoSearches(List<MongoAutoSearch> entities);

}
