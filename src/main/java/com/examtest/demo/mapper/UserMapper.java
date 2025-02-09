package com.examtest.demo.mapper;

import com.examtest.demo.dto.user.UserBasicDto;
import com.examtest.demo.dto.user.UserDetailedDto;
import com.examtest.demo.dto.user.UserRegistrationDto;
import com.examtest.demo.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.MapperConfig;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserBasicDto toDto(User user);
    UserDetailedDto toDetailedDto(User user);
    @Mapping(target = "email", source = "email")
    @Mapping(target = "password", source = "password")
    @Mapping(target = "role", ignore = true) // Ignore roles during registration
    @Mapping(target = "orders", ignore = true) // Ignore orders during registration
    User toModel(UserRegistrationDto registrationDto);
}