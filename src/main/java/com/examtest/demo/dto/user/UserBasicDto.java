package com.examtest.demo.dto.user;

import com.examtest.demo.model.User.Role;

import java.util.UUID;

public class UserBasicDto{
    UUID id;
    String email;

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


    public UserBasicDto(){}

    public UserBasicDto(UUID id, String email) {
        this.id = id;
        this.email = email;
    }
}
