package net.fvogel.chronosbackend.domain.common;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = ChronosDateSpecValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ChronosDateSpec {
    String message() default "Invalid date spec";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
