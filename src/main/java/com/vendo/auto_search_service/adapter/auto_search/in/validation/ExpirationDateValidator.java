package com.vendo.auto_search_service.adapter.auto_search.in.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class ExpirationDateValidator implements ConstraintValidator<ValidExpirationDate, LocalDateTime> {

    @Override
    public boolean isValid(LocalDateTime value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        LocalDate today = LocalDate.now();
        LocalDate expirationDate = value.toLocalDate();

        return !expirationDate.isBefore(today.plusDays(1)) && !expirationDate.isAfter(today.plusDays(7));
    }
}
