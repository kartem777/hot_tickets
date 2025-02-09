package com.examtest.demo;

import com.examtest.demo.dto.user.RoleDto;
import com.examtest.demo.dto.user.UserBasicDto;
import com.examtest.demo.dto.user.UserDetailedDto;
import com.examtest.demo.dto.user.UserRegistrationDto;
import com.examtest.demo.exception.RegistrationException;
import com.examtest.demo.mapper.UserMapper;
import com.examtest.demo.model.User;
import com.examtest.demo.model.User.Role;
import com.examtest.demo.repository.UserRepository;
import com.examtest.demo.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import jakarta.persistence.EntityNotFoundException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceImplTest {

    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getAllUsers_ShouldReturnListOfUsers() {
        List<User> users = List.of(new User(), new User());
        when(userRepository.findAll()).thenReturn(users);
        when(userMapper.toDto(any())).thenReturn(new UserBasicDto());

        List<UserBasicDto> result = userService.getAllUsers();

        assertEquals(2, result.size());
        verify(userRepository).findAll();
    }

    @Test
    void getUserById_ShouldReturnUser_WhenFound() {
        UUID id = UUID.randomUUID();
        User user = new User();
        when(userRepository.findById(id)).thenReturn(Optional.of(user));
        when(userMapper.toDetailedDto(user)).thenReturn(new UserDetailedDto());

        UserDetailedDto result = userService.getUserById(id);

        assertNotNull(result);
        verify(userRepository).findById(id);
    }

    @Test
    void getUserById_ShouldThrowException_WhenNotFound() {
        UUID id = UUID.randomUUID();
        when(userRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> userService.getUserById(id));
        verify(userRepository).findById(id);
    }

    @Test
    void register_ShouldSaveNewUser_WhenEmailIsNotRegistered() {
        UserRegistrationDto userRegistrationDto = new UserRegistrationDto("test@example.com", "password123", "password123");
        User user = new User();
        when(userRepository.existsByEmail(userRegistrationDto.email())).thenReturn(false);
        when(userMapper.toModel(userRegistrationDto)).thenReturn(user);
        when(passwordEncoder.encode(userRegistrationDto.password())).thenReturn("encodedPassword");
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toDetailedDto(user)).thenReturn(new UserDetailedDto());

        UserDetailedDto result = userService.register(userRegistrationDto);

        assertNotNull(result);
        verify(userRepository).save(user);
    }

    @Test
    void register_ShouldThrowException_WhenEmailIsAlreadyRegistered() {
        UserRegistrationDto userRegistrationDto = new UserRegistrationDto("test@example.com", "password123", "password123");
        when(userRepository.existsByEmail(userRegistrationDto.email())).thenReturn(true);

        assertThrows(RegistrationException.class, () -> userService.register(userRegistrationDto));
        verify(userRepository, never()).save(any());
    }

    @Test
    void deleteUser_ShouldDeleteUser_WhenUserExists() {
        UUID id = UUID.randomUUID();
        when(userRepository.existsById(id)).thenReturn(true);

        userService.deleteUser(id);

        verify(userRepository).deleteById(id);
    }

    @Test
    void deleteUser_ShouldThrowException_WhenUserDoesNotExist() {
        UUID id = UUID.randomUUID();
        when(userRepository.existsById(id)).thenReturn(false);

        assertThrows(RuntimeException.class, () -> userService.deleteUser(id));
        verify(userRepository, never()).deleteById(id);
    }

    @Test
    void updateUser_ShouldUpdateUser_WhenUserExists() {
        UUID id = UUID.randomUUID();
        UserRegistrationDto userRegistrationDto = new UserRegistrationDto("new@example.com", "newPassword", "newPassword");
        User existingUser = new User();
        when(userRepository.findById(id)).thenReturn(Optional.of(existingUser));
        when(passwordEncoder.encode(userRegistrationDto.password())).thenReturn("encodedNewPassword");
        when(userRepository.save(existingUser)).thenReturn(existingUser);
        when(userMapper.toDetailedDto(existingUser)).thenReturn(new UserDetailedDto());

        UserDetailedDto result = userService.updateUser(id, userRegistrationDto);

        assertNotNull(result);
        verify(userRepository).save(existingUser);
    }

    @Test
    void updateUser_ShouldThrowException_WhenUserDoesNotExist() {
        UUID id = UUID.randomUUID();
        UserRegistrationDto userRegistrationDto = new UserRegistrationDto("new@example.com", "newPassword", "newPassword");
        when(userRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> userService.updateUser(id, userRegistrationDto));
        verify(userRepository, never()).save(any());
    }

    @Test
    void updateUserRole_ShouldUpdateUserRole_WhenUserExists() {
        UUID id = UUID.randomUUID();
        RoleDto roleDto = new RoleDto("ADMIN");
        User existingUser = new User();
        when(userRepository.findById(id)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(existingUser)).thenReturn(existingUser);
        when(userMapper.toDto(existingUser)).thenReturn(new UserBasicDto());

        UserBasicDto result = userService.updateUserRole(id, roleDto);

        assertNotNull(result);
        verify(userRepository).save(existingUser);
    }

    @Test
    void updateUserRole_ShouldThrowException_WhenUserNotFound() {
        UUID id = UUID.randomUUID();
        RoleDto roleDto = new RoleDto("ADMIN");
        when(userRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> userService.updateUserRole(id, roleDto));
        verify(userRepository, never()).save(any());
    }
}
