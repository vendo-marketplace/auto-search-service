package com.vendo.auto_search_service.adapter.category.out.dto;

import com.vendo.auto_search_service.domain.category.CategoryType;

public record CategoryResponse(
        String id,
        CategoryType type
) {
}
