package com.examtest.demo.service;

import com.examtest.demo.dto.booking.BookingRequestDto;
import com.examtest.demo.dto.booking.BookingResponseDto;
import com.examtest.demo.model.Booking;
import com.examtest.demo.model.UserOrder;

import java.util.List;
import java.util.UUID;

public interface BookingService {
    List<BookingResponseDto> getAllBooking();
    BookingResponseDto addBooking(BookingRequestDto bookingDto);
    BookingResponseDto getBookingById(UUID id);
    void deleteBooking(UUID id);
    BookingResponseDto updateBooking(UUID id, BookingRequestDto bookingDto);
    Booking getBookingByNameBasic(String name);
    BookingResponseDto getBookingByName(String name);
    Booking updateBookingBasic(Booking booking, UserOrder order);
}
