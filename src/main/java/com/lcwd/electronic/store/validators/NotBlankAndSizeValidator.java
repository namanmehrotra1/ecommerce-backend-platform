package com.lcwd.electronic.store.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NotBlankAndSizeValidator implements ConstraintValidator<NotBlankAndSizeValidation, String> {
    Logger logger = LoggerFactory.getLogger(NotBlankAndSizeValidator.class);

    private int minSize;
    private int maxSize;
    private String message;

    @Override
    public void initialize(NotBlankAndSizeValidation constraintAnnotation) {
        this.minSize = constraintAnnotation.min();
        this.maxSize = constraintAnnotation.max();
        this.message = constraintAnnotation.message();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext constraintValidatorContext) {
        if (value == null) {
            return true; // Allow nulls for partial updates (PATCH)
        }

        if (value.trim().isEmpty()) {
            constraintValidatorContext.disableDefaultConstraintViolation();
            constraintValidatorContext.buildConstraintViolationWithTemplate("Field can't be blank, please input the value").addConstraintViolation();
            return false;
        }
        if (value.length() < minSize || value.length() > maxSize) {
            constraintValidatorContext.disableDefaultConstraintViolation();
            constraintValidatorContext.buildConstraintViolationWithTemplate(message).addConstraintViolation();
            return false;
        }
        return true;
    }
}
