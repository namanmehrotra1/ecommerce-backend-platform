package com.lcwd.electronic.store.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.regex.Pattern;

public class ImageNameValidator implements ConstraintValidator<ImageNameValid, String> {

    private static final String IMAGE_NAME_REGEX = "^.+\\.(jpeg|png)$";
    private Logger logger = LoggerFactory.getLogger(ImageNameValidator.class);

    @Override
    public boolean isValid(String value, ConstraintValidatorContext constraintValidatorContext) {
//        logic-> Image name is blank
        if (value.isBlank()) {
            constraintValidatorContext.disableDefaultConstraintViolation();
            constraintValidatorContext.buildConstraintViolationWithTemplate("Image Name is blank, Please input the image name").addConstraintViolation();
            return false;
        }
        if (!Pattern.matches(IMAGE_NAME_REGEX, value)) {
            constraintValidatorContext.disableDefaultConstraintViolation();
            constraintValidatorContext.buildConstraintViolationWithTemplate("Only .jpeg and .png files are allowed").addConstraintViolation();
            return false;
        }
//        else {
//            return true;
//        }
//        SIMPLIFIED ->
        //        return !value.isBlank()
        logger.info("Message from invalid : {} ", value);
        return true;
    }
}
