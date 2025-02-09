package com.examtest.demo.service.impl;

import com.examtest.demo.dto.user.LoginRequestDto;
import com.examtest.demo.dto.user.LoginResponseDto;
import com.examtest.demo.model.User;
import com.examtest.demo.repository.UserRepository;
import com.examtest.demo.security.JwtUtil;
import com.examtest.demo.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    @Autowired
    public AuthenticationServiceImpl(AuthenticationManager authenticationManager, JwtUtil jwtUtil, UserRepository userRepository) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
    }

    @Override
    public LoginResponseDto authenticate(LoginRequestDto loginRequestDto) {
        Authentication authentication =
                new UsernamePasswordAuthenticationToken(loginRequestDto.email(), loginRequestDto.password());
        Authentication authenticate = authenticationManager.authenticate(authentication);
        String token = jwtUtil.generateToken(authenticate.getName());
        return new LoginResponseDto(token);
    }

    @Override
    public void authenticateWithTelegram(LoginRequestDto requestDto, Long telegramId) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(requestDto.email(),
                        requestDto.password())
        );
        User user = (User) authentication.getPrincipal();
        user.setTelegramChatId(telegramId);
        userRepository.save(user);
    }
}
