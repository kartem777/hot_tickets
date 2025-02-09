package com.examtest.demo.dto.userorder;

import com.examtest.demo.dto.booking.BookingRequestDto;
import com.examtest.demo.dto.booking.BookingResponseDto;
import com.examtest.demo.model.User;

import java.util.Date;
import java.util.List;
import java.util.UUID;

public class UserOrderResponseDto{
        UUID id;
        User user;
        List<BookingResponseDto> bookings;
        Date createdAt;

    public UUID getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public List<BookingResponseDto> getBookings() {
        return bookings;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setBookings(List<BookingResponseDto> bookings) {
        this.bookings = bookings;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
    public UserOrderResponseDto(){}

    public UserOrderResponseDto(UUID id, User user, List<BookingResponseDto> bookings, Date createdAt) {
        this.id = id;
        this.user = user;
        this.bookings = bookings;
        this.createdAt = createdAt;
    }
}
