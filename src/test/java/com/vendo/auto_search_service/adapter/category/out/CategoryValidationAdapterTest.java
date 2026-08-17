package com.vendo.auto_search_service.adapter.category.out;

import com.vendo.auto_search_service.domain.category.exception.CategoryNotFoundException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatCode;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

class CategoryValidationAdapterTest {

    private final CategoryValidationAdapter adapter = new CategoryValidationAdapter();

    @Test
    void validateCategoryExists_shouldPass_whenCategoryIdNotBlank() {
        assertThatCode(() -> adapter.validateCategoryExists("category-id")).doesNotThrowAnyException();
    }

    @Test
    void validateCategoryExists_shouldThrow_whenCategoryIdBlank() {
        assertThatThrownBy(() -> adapter.validateCategoryExists(" "))
                .isInstanceOf(CategoryNotFoundException.class);
    }
}
