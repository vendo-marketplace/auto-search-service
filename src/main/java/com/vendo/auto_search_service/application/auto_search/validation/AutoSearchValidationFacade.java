package com.vendo.auto_search_service.application.auto_search.validation;

import com.vendo.auto_search_service.domain.auto_search.AutoSearch;
import com.vendo.auto_search_service.domain.auto_search.exception.AutoSearchNotFoundException;
import com.vendo.auto_search_service.domain.auto_search.exception.CategoryNotFoundException;
import com.vendo.auto_search_service.port.auth.AuthUserPort;
import com.vendo.auto_search_service.port.auto_search.AutoSearchValidationPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AutoSearchValidationFacade implements AutoSearchValidationPort {

    private final AuthUserPort authUserPort;

    @Override
    public void validateOwner(AutoSearch autoSearch) {
        String userId = authUserPort.getAuthUser().id();
        if (!autoSearch.userId().equals(userId)) {
            throw new AutoSearchNotFoundException("Auto search request not found.");
        }
    }

    @Override
    public void validateCategoryExists(String categoryId) {
        // TODO replace with a real cross-service category lookup once product-service
        // exposes an internal endpoint for category validation (maybe we need a new ticket).
        if (categoryId == null || categoryId.isBlank()) {
            throw new CategoryNotFoundException("Category not found.");
        }
    }
}
