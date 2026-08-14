package com.vendo.auto_search_service.adapter.auto_search.in.validation;

import com.vendo.auto_search_service.domain.auto_search.SearchStatus;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class UpdateStatusValidator implements ConstraintValidator<ValidUpdateStatus, SearchStatus> {

    @Override
    public boolean isValid(SearchStatus value, ConstraintValidatorContext context) {
        return value == null || value == SearchStatus.ACTIVE || value == SearchStatus.CANCELLED;
    }
}
