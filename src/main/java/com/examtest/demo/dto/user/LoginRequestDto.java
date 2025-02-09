package com.examtest.demo.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LoginRequestDto(
        @Email
        @Size(max = 255, message = "The email cannot exceed the limit of 255 characters")
        @NotBlank(message = "Username cannot be blank")
        String email,
        @NotBlank(message = "Password cannot be blank")
        @Size(min = 8, max = 50, message = "Password is too short or long")
        String password) {
}
