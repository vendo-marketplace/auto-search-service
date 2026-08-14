package com.vendo.auto_search_service.port.auto_search;

import com.vendo.auto_search_service.domain.auto_search.AutoSearch;

public interface AutoSearchValidationPort {

    void validateOwner(AutoSearch autoSearch);

    void validateCategoryExists(String categoryId);

}
