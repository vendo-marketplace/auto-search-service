package com.vendo.auto_search_service.adapter.category.out;

import com.vendo.auto_search_service.adapter.category.out.dto.CategoryResponse;
import com.vendo.auto_search_service.domain.category.Category;
import com.vendo.auto_search_service.domain.category.CategoryType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CategoryQueryAdapterTest {

    @InjectMocks
    private CategoryQueryAdapter adapter;

    @Mock
    private CategoryClient client;

    @Test
    void findById_shouldReturnMappedCategory() {
        when(client.findById("category-id")).thenReturn(new CategoryResponse("category-id", CategoryType.CHILD));

        Category category = adapter.findById("category-id");

        assertThat(category).isEqualTo(Category.builder().id("category-id").type(CategoryType.CHILD).build());
    }
}
