package com.phoenix_sat.phoenix_sat_backend.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import liquibase.util.StringUtil;

import java.util.regex.Pattern;

public class EmailValidator implements ConstraintValidator<Email, String> {

    public static final String EMAIL_PATTERN = "^((\"[\\w-\\s]+\")|([\\w-]+(?:\\.[\\w-]+)*)|(\"[\\w-\\s]+\")([\\w-]+(?:\\.[\\w-]+)*))(@((?:[\\w-]+\\.)*\\w[\\w-]{0,66})\\.([a-z]{2,6}(?:\\.[a-z]{2})?)$)|(@\\[?((25[0-5]\\.|2[0-4][0-9]\\.|1[0-9]{2}\\.|[0-9]{1,2}\\.))((25[0-5]|2[0-4][0-9]|1[0-9]{2}|[0-9]{1,2})\\.){2}(25[0-5]|2[0-4][0-9]|1[0-9]{2}|[0-9]{1,2})\\])";
    private boolean nullable;
    private boolean isEmpty;

    @Override
    public void initialize(Email constraintAnnotation) {
        this.nullable = constraintAnnotation.nullable();
        this.isEmpty = constraintAnnotation.isEmpty();
    }

    @Override
    public boolean isValid(String email, ConstraintValidatorContext context) {
        if (email == null) {
            return nullable;
        }
        if (StringUtil.isEmpty(email)) {
            return isEmpty;
        }
        return Pattern.compile(EMAIL_PATTERN).matcher(email).matches();
    }
}
