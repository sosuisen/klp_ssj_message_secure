package com.example.model.validator;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Documented
@Constraint(validatedBy = RoleValidator.class)
@Target({ FIELD })
@Retention(RUNTIME)
public @interface ValidRole {
    String message() default "Invalid role";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}