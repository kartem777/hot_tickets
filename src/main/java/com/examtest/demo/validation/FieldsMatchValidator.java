package com.examtest.demo.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.BeanWrapperImpl;

import java.util.Objects;

public class FieldsMatchValidator implements ConstraintValidator<FieldsMatch, Object> {
    private String field;
    private String fieldMatch;

    @Override
    public void initialize(FieldsMatch constraintAnnotation) {
        field = constraintAnnotation.field();
        fieldMatch = constraintAnnotation.fieldMatch();
    }

    @Override
    public boolean isValid(Object obj, ConstraintValidatorContext constraintValidatorContext) {
        Object fieldValue = new BeanWrapperImpl(obj).getPropertyValue(this.field);
        Object fieldMatchValue = new BeanWrapperImpl(obj).getPropertyValue(this.fieldMatch);
        return Objects.equals(fieldValue, fieldMatchValue);
    }
}
