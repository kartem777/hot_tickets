package com.examtest.demo.dto.userorder;

import com.examtest.demo.dto.booking.BookingRequestDto;
import com.examtest.demo.dto.booking.BookingResponseDto;
import com.examtest.demo.model.User;
import jakarta.validation.constraints.NotNull;

import java.util.Date;
import java.util.List;
import java.util.UUID;

public class UserOrderRequestDto{
        @NotNull
        User user;
        List<BookingResponseDto> bookings;
        Date createdAt;

        public @NotNull User getUser() {
                return user;
        }

        public void setUser(@NotNull User user) {
                this.user = user;
        }

        public List<BookingResponseDto> getBookings() {
                return bookings;
        }

        public void setBookings(List<BookingResponseDto> bookings) {
                this.bookings = bookings;
        }

        public Date getCreatedAt() {
                return createdAt;
        }

        public void setCreatedAt(Date createdAt) {
                this.createdAt = createdAt;
        }

        public UserOrderRequestDto(){}

        public UserOrderRequestDto(User user, List<BookingResponseDto> bookings) {
                this.user = user;
                this.bookings = bookings;
        }

        public UserOrderRequestDto(User user, List<BookingResponseDto> bookings, Date createdAt) {
                this.user = user;
                this.bookings = bookings;
                this.createdAt = createdAt != null ? createdAt : new Date();
        }
}
