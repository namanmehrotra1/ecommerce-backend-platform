package com.lcwd.electronic.store.validators;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = ImageNameValidator.class)
public @interface ImageNameValid {
    // Error Message
    String message() default "Image Name is blank";

    // Represent group of constraints
    Class<?>[] groups() default {};

    // Additional information about annotation
    Class<? extends Payload>[] payload() default {};
}
