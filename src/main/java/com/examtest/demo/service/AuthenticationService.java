package com.examtest.demo.service;

import com.examtest.demo.dto.user.LoginRequestDto;
import com.examtest.demo.dto.user.LoginResponseDto;

public interface AuthenticationService {

    LoginResponseDto authenticate(LoginRequestDto loginRequestDto);
    void authenticateWithTelegram(LoginRequestDto requestDto, Long telegramId);
}
