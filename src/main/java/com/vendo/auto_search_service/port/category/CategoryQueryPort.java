package com.vendo.auto_search_service.port.category;

import com.vendo.auto_search_service.domain.category.Category;

public interface CategoryQueryPort {

    Category findById(String id);

}
