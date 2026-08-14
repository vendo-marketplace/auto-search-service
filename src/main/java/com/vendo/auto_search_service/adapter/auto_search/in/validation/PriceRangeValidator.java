package com.vendo.auto_search_service.adapter.auto_search.in.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PriceRangeValidator implements ConstraintValidator<ValidPriceRange, PriceRange> {

    @Override
    public boolean isValid(PriceRange value, ConstraintValidatorContext context) {
        if (value == null || value.minPrice() == null || value.maxPrice() == null) {
            return true;
        }

        return value.minPrice().compareTo(value.maxPrice()) <= 0;
    }
}
