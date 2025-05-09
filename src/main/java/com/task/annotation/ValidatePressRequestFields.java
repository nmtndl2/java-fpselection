package com.task.annotation;

import com.task.validation.PressRequestValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = PressRequestValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidatePressRequestFields {
    String message() default "Some required fields are missing based on availability flags.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
