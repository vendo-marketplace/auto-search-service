package com.vendo.auto_search_service.adapter.category.out;

import com.vendo.auto_search_service.adapter.category.out.dto.CategoryResponse;
import com.vendo.auto_search_service.adapter.category.out.mapper.CategoryMapper;
import com.vendo.auto_search_service.domain.category.Category;
import com.vendo.auto_search_service.port.category.CategoryQueryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CategoryQueryAdapter implements CategoryQueryPort {

    private final CategoryClient client;
    private final CategoryMapper mapper;

    @Override
    public Category findById(String id) {
        CategoryResponse response = client.findById(id);
        return mapper.toCategory(response);
    }
}
