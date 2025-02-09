package com.examtest.demo.service.impl;

import com.examtest.demo.dto.user.RoleDto;
import com.examtest.demo.dto.user.UserBasicDto;
import com.examtest.demo.dto.user.UserDetailedDto;
import com.examtest.demo.dto.user.UserRegistrationDto;
import com.examtest.demo.exception.RegistrationException;
import com.examtest.demo.mapper.UserMapper;
import com.examtest.demo.model.User;
import com.examtest.demo.model.User.Role;
import com.examtest.demo.repository.UserRepository;
import com.examtest.demo.service.UserService;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, UserMapper userMapper, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public List<UserBasicDto> getAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream()
                .map(userMapper::toDto)
                .toList();
    }

    @Override
    public UserDetailedDto getUserById(UUID id) {
        User user = userRepository.findById(id).get();
        return userMapper.toDetailedDto(user);
    }

    @Override
    public UserDetailedDto getUserByEmail(String email) {
        User user = userRepository.findByEmail(email).get();
        return userMapper.toDetailedDto(user);
    }

    @Override
    public User getUserByEmailBasic(String email) {
        User user = userRepository.findByEmail(email).get();
        return user;
    }


    @Override
    public UserDetailedDto register(UserRegistrationDto userDto) throws RegistrationException {
        String email = userDto.email();
        if (userRepository.existsByEmail(email)) {
            throw new RegistrationException("Email is already registered");
        }

        User user = userMapper.toModel(userDto);

        user.setPassword(passwordEncoder.encode(userDto.password()));

        User savedUser = userRepository.save(user);
        return userMapper.toDetailedDto(savedUser);
    }
    @Override
    public void deleteUser(UUID id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("User not found");
        }
        userRepository.deleteById(id);
    }

    @Override
    public UserDetailedDto updateUser(UUID id, UserRegistrationDto userRegistrationDto) {
        User existingUser = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));

        existingUser.setEmail(userRegistrationDto.email());
        existingUser.setPassword(passwordEncoder.encode(userRegistrationDto.password()));
        User updatedUser = userRepository.save(existingUser);
        return userMapper.toDetailedDto(updatedUser);
    }
    @Override
    public UserBasicDto updateUserRole(UUID id, RoleDto dto) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + id));
        Role role = Role.valueOf(dto.role());
        existingUser.setRole(role);
        User updatedUser = userRepository.save(existingUser);
        return userMapper.toDto(updatedUser);
    }
}
