package com.vendo.auto_search_service.adapter.category.out.mapper;

import com.vendo.auto_search_service.adapter.category.out.dto.CategoryResponse;
import com.vendo.auto_search_service.domain.category.Category;
import com.vendo.auto_search_service.infrastructure.config.MapStructConfig;
import org.mapstruct.Mapper;

@Mapper(config = MapStructConfig.class)
public interface CategoryMapper {

    Category toCategory(CategoryResponse response);

}
