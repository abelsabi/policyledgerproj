package com.hdfclife.ledger.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Constraint(validatedBy = PolicyTypeValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER, ElementType.RECORD_COMPONENT})
@Retention(RetentionPolicy.RUNTIME)
public @interface PolicyTypeConstraint {

    String message() default "must be TERM, ULIP, or ENDOWMENT";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
