package com.examtest.demo.dto.user;

import com.examtest.demo.validation.EnumValue;

public record RoleDto(
        @EnumValue(enumValues = {"ADMIN", "CUSTOMER"}, message = "Invalid role")
        String role
) {}
