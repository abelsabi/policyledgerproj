package com.hdfclife.ledger.validation;

import com.hdfclife.ledger.domain.PolicyType;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PolicyTypeValidator implements ConstraintValidator<PolicyTypeConstraint, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isBlank()) {
            return true;
        }
        for (PolicyType type : PolicyType.values()) {
            if (type.name().equals(value)) {
                return true;
            }
        }
        return false;
    }
}
