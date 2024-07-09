package com.phoenix_sat.phoenix_sat_backend.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import liquibase.util.StringUtil;

import java.util.regex.Pattern;

public class PasswordValidator implements ConstraintValidator<Password, String> {
    public static final String PASSWORD_PATTERN = "^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[#?!@$%^&*-]).{8,}$";
    private boolean nullable;
    private boolean isEmpty;
    @Override
    public void initialize(Password constraintAnnotation) {
        this.nullable = constraintAnnotation.nullable();
        this.isEmpty = constraintAnnotation.isEmpty();
    }

    @Override
    public boolean isValid(String password, ConstraintValidatorContext context) {
        if (password == null) {
            return nullable;
        }
        if (StringUtil.isEmpty(password)) {
            return isEmpty;
        }
        return Pattern.compile(PASSWORD_PATTERN).matcher(password).matches();
    }
}
