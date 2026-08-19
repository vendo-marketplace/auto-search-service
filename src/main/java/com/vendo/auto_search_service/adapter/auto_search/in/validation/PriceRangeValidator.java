package com.vendo.auto_search_service.adapter.auto_search.in.validation;

import com.vendo.core_lib.utils.ObjectUtils;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PriceRangeValidator implements ConstraintValidator<ValidPriceRange, PriceRange> {

    @Override
    public boolean isValid(PriceRange value, ConstraintValidatorContext context) {
        if (ObjectUtils.isNull(value) || !ObjectUtils.isAllNotNull(value.minPrice(), value.maxPrice())) {
            return true;
        }

        return value.minPrice().compareTo(value.maxPrice()) <= 0;
    }
}
