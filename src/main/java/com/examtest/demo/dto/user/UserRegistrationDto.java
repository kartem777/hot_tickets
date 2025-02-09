package com.examtest.demo.dto.user;

import com.examtest.demo.validation.FieldsMatch;
import jakarta.validation.constraints.Email;

@FieldsMatch(field = "password", fieldMatch = "repeatPassword", message = "Passwords do not match!")
public record UserRegistrationDto(
        @Email
        String email,
        String password,
        String repeatPassword
) {
}