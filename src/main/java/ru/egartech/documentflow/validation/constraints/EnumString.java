package ru.egartech.documentflow.validation.constraints;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import ru.egartech.documentflow.validation.EnumStringValidator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = EnumStringValidator.class)
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface EnumString {

    String message() default "must be enum string";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    Class<? extends Enum<?>> enumClass();

}
