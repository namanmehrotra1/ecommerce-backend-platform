package com.lcwd.electronic.store.validators;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = NotBlankAndSizeValidator.class)
public @interface NotBlankAndSizeValidation {
    // Error Message
    String message() default "Field Name is blank";

    // Min and Max Size to be passed dynamically
    int min() default 2;

    int max() default 100;

    // Represent group of constraints
    Class<?>[] groups() default {};

    // Additional information about annotation
    Class<? extends Payload>[] payload() default {};
}
