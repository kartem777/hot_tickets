package com.examtest.demo.dto.user;

import com.examtest.demo.dto.userorder.UserOrderResponseDto;
import com.examtest.demo.validation.EnumValue;

import java.util.List;
import java.util.UUID;

public class UserDetailedDto{
    UUID id;
    String email;
    List<UserOrderResponseDto> bookings;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<UserOrderResponseDto> getOrders() {
        return bookings;
    }

    public void setBookings(List<UserOrderResponseDto> bookings) {
        this.bookings = bookings;
    }

    public UserDetailedDto(){}

    public UserDetailedDto(UUID id, String email, List<UserOrderResponseDto> bookings) {
        this.id = id;
        this.email = email;
        this.bookings = bookings;
    }
}
