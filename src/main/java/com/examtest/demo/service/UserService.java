package com.examtest.demo.service;

import com.examtest.demo.dto.user.RoleDto;
import com.examtest.demo.dto.user.UserBasicDto;
import com.examtest.demo.dto.user.UserDetailedDto;
import com.examtest.demo.dto.user.UserRegistrationDto;
import com.examtest.demo.exception.RegistrationException;
import com.examtest.demo.model.User;
import com.examtest.demo.model.User.Role;
import java.util.List;
import java.util.UUID;

public interface UserService {
    List<UserBasicDto> getAllUsers();
    UserDetailedDto getUserById(UUID id);
    UserDetailedDto getUserByEmail(String email);
    User getUserByEmailBasic(String email);
    UserDetailedDto register(UserRegistrationDto userDto) throws RegistrationException;
    void deleteUser(UUID id);
    UserDetailedDto updateUser(UUID id, UserRegistrationDto userRegistrationDto);
    UserBasicDto updateUserRole(UUID id, RoleDto role);
}