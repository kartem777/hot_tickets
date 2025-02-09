package com.examtest.demo.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Arrays;

public class EnumValidator implements ConstraintValidator<EnumValue, String> {

    private String[] validValues;

    @Override
    public void initialize(EnumValue constraintAnnotation) {
        validValues = constraintAnnotation.enumValues();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return false;
        }
        return Arrays.stream(validValues)
                .anyMatch(validValue -> validValue.equalsIgnoreCase(value));
    }
}
