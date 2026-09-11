package com.shenq.courtbooking.user.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RegisterRequestValidationTest {

    private ValidatorFactory validatorFactory;
    private Validator validator;

    @BeforeEach
    void setUp() {
        validatorFactory =
                Validation.buildDefaultValidatorFactory();

        validator = validatorFactory.getValidator();
    }

    @AfterEach
    void tearDown() {
        validatorFactory.close();
    }

    @Test
    void shouldAcceptValidRegisterRequest() {
        RegisterRequest request = new RegisterRequest(
                "Zhang Shen",
                "zhang@example.com",
                "Password123!"
        );

        Set<ConstraintViolation<RegisterRequest>> violations =
                validator.validate(request);

        assertTrue(violations.isEmpty());
    }

    @Test
    void shouldRejectBlankName() {
        RegisterRequest request = new RegisterRequest(
                "",
                "zhang@example.com",
                "Password123!"
        );

        Set<ConstraintViolation<RegisterRequest>> violations =
                validator.validate(request);

        assertEquals(1, violations.size());
    }

    @Test
    void shouldRejectInvalidEmail() {
        RegisterRequest request = new RegisterRequest(
                "Zhang Shen",
                "not-an-email",
                "Password123!"
        );

        Set<ConstraintViolation<RegisterRequest>> violations =
                validator.validate(request);

        assertEquals(1, violations.size());
    }

    @Test
    void shouldRejectShortPassword() {
        RegisterRequest request = new RegisterRequest(
                "Zhang Shen",
                "zhang@example.com",
                "123"
        );

        Set<ConstraintViolation<RegisterRequest>> violations =
                validator.validate(request);

        assertEquals(1, violations.size());
    }
}