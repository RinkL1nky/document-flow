package ru.egartech.documentflow.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import ru.egartech.documentflow.validation.constraints.EnumString;

import java.util.Arrays;
import java.util.List;

public class EnumStringValidator implements ConstraintValidator<EnumString, String> {

    private List<String> enumStrings;

    @Override
    public void initialize(EnumString constraint) {
        enumStrings = Arrays.stream(constraint.enumClass().getEnumConstants())
                .map(Enum::toString)
                .toList();
    }

    @Override
    public boolean isValid(String string, ConstraintValidatorContext constraintValidatorContext) {
        return enumStrings.contains(string);
    }

}
