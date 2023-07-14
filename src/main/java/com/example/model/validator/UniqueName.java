package com.example.model.validator;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Documented
@Constraint(validatedBy = UniqueNameValidator.class)
@Target({ FIELD })
@Retention(RUNTIME)
public @interface UniqueName {
    String message() default "The provided value is already in use. Please enter a unique value.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}