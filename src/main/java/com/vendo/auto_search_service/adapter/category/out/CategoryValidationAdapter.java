package com.vendo.auto_search_service.adapter.category.out;

import com.vendo.auto_search_service.domain.category.exception.CategoryNotFoundException;
import com.vendo.auto_search_service.port.category.CategoryValidationPort;
import org.springframework.stereotype.Component;

@Component
public class CategoryValidationAdapter implements CategoryValidationPort {

    @Override
    public void validateCategoryExists(String categoryId) {
        // TODO replace with a real cross-service category lookup once product-service
        // exposes an internal endpoint for category validation (maybe we need a new ticket).
        // Once that lookup returns the full category, also verify it is a leaf/child
        // category before allowing an auto-search request against it.
        if (categoryId == null || categoryId.isBlank()) {
            throw new CategoryNotFoundException("Category not found.");
        }
    }
}
