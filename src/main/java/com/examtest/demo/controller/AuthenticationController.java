package com.examtest.demo.controller;

import com.examtest.demo.dto.user.LoginRequestDto;
import com.examtest.demo.dto.user.LoginResponseDto;
import com.examtest.demo.dto.user.UserDetailedDto;
import com.examtest.demo.dto.user.UserRegistrationDto;
import com.examtest.demo.service.AuthenticationService;
import com.examtest.demo.service.UserService;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@Validated
public class AuthenticationController {

    private final UserService userService;
    private final AuthenticationService authenticationService;

    public AuthenticationController(UserService userService, AuthenticationService authenticationService) {
        this.userService = userService;
        this.authenticationService = authenticationService;
    }

    @PostMapping("/register")
    public UserDetailedDto register(@RequestBody @Valid UserRegistrationDto UserDto) {
        return userService.register(UserDto);
    }

    @PostMapping("/login")
    public LoginResponseDto login(@RequestBody @Valid LoginRequestDto loginRequestDto) {
        return authenticationService.authenticate(loginRequestDto);
    }
}
