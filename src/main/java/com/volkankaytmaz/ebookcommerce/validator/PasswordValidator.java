package com.volkankaytmaz.ebookcommerce.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordValidator implements ConstraintValidator<ValidPassword, String> {

    @Override
    public boolean isValid(String password, ConstraintValidatorContext context) {
        if (password == null) {
            return false;
        }

        // En az 6 karakter ve en az 1 sayı içermeli
        return password.length() >= 6 && password.matches(".*\\d.*");
    }
}

