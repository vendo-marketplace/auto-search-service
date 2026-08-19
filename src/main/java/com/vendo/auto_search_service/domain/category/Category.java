package com.vendo.auto_search_service.domain.category;

import com.vendo.auto_search_service.domain.category.exception.CategoryTypeException;
import lombok.Builder;

@Builder
public record Category(
        String id,
        CategoryType type
) {

    public void throwIfNotDesiredType(CategoryType desiredType) {
        if (type != desiredType) {
            throw new CategoryTypeException("Category type should be " + desiredType.name().toLowerCase() + ".");
        }
    }

}
