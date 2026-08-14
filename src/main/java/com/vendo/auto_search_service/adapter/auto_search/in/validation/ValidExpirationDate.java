package com.vendo.auto_search_service.adapter.auto_search.in.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Constraint(validatedBy = ExpirationDateValidator.class)
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidExpirationDate {

    String message() default "Expiration date must be at least a day after today and not later than a week from now.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
