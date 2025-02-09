package com.examtest.demo;

import com.examtest.demo.dto.user.LoginRequestDto;
import com.examtest.demo.dto.user.LoginResponseDto;
import com.examtest.demo.model.User;
import com.examtest.demo.repository.UserRepository;
import com.examtest.demo.security.JwtUtil;
import com.examtest.demo.service.impl.AuthenticationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthenticationServiceImplTest {

    @InjectMocks
    private AuthenticationServiceImpl authenticationService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private UserRepository userRepository;

    @Mock
    private Authentication authentication;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void authenticate_ShouldReturnLoginResponseDto_WhenAuthenticationIsSuccessful() {
        String email = "test@example.com";
        String password = "password123";
        String generatedToken = "mockedJwtToken";
        LoginRequestDto loginRequestDto = new LoginRequestDto(email, password);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(authentication.getName()).thenReturn(email);
        when(jwtUtil.generateToken(email)).thenReturn(generatedToken);

        LoginResponseDto responseDto = authenticationService.authenticate(loginRequestDto);

        assertNotNull(responseDto);
        assertEquals(generatedToken, responseDto.token());
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtUtil).generateToken(email);
    }

    @Test
    void authenticate_ShouldThrowException_WhenAuthenticationFails() {
        String email = "invalid@example.com";
        String password = "wrongPassword";
        LoginRequestDto loginRequestDto = new LoginRequestDto(email, password);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Invalid credentials"));

        assertThrows(BadCredentialsException.class, () -> authenticationService.authenticate(loginRequestDto));
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    void authenticateWithTelegram_ShouldUpdateUserWithTelegramId_WhenAuthenticationIsSuccessful() {
        String email = "test@example.com";
        String password = "password123";
        Long telegramId = 123456789L;
        LoginRequestDto loginRequestDto = new LoginRequestDto(email, password);

        User user = new User();
        user.setEmail(email);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(user);

        authenticationService.authenticateWithTelegram(loginRequestDto, telegramId);

        assertEquals(telegramId, user.getTelegramChatId());
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userRepository).save(user);
    }
}
