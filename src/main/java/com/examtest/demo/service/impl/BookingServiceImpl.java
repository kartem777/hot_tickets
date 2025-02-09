package com.examtest.demo.service.impl;

import com.examtest.demo.dto.booking.BookingRequestDto;
import com.examtest.demo.dto.booking.BookingResponseDto;
import com.examtest.demo.exception.RegistrationException;
import com.examtest.demo.mapper.BookingMapper;
import com.examtest.demo.model.Booking;
import com.examtest.demo.model.UserOrder;
import com.examtest.demo.repository.BookingRepository;
import com.examtest.demo.repository.UserOrderRepository;
import com.examtest.demo.service.BookingService;
import jakarta.persistence.Id;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final BookingMapper bookingMapper;

    @Autowired
    public BookingServiceImpl(BookingRepository bookingRepository, UserOrderRepository userOrderRepository, BookingMapper bookingMapper) {
        this.bookingRepository = bookingRepository;
        this.bookingMapper = bookingMapper;
    }

    @Override
    public List<BookingResponseDto> getAllBooking() {
        List<Booking> bookings = bookingRepository.findAll();
        return bookings.stream()
                .map(bookingMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public BookingResponseDto addBooking(BookingRequestDto bookingDto) {
        String name = bookingDto.getName();
        if (bookingRepository.existsByName(name)) {
            throw new RegistrationException("The booking with this name is already registered");
        }
        Booking booking = bookingMapper.toModel(bookingDto);

        Booking savedBooking = bookingRepository.save(booking);

        return bookingMapper.toDto(savedBooking);
    }


    @Override
    public BookingResponseDto getBookingById(UUID id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found"));
        return bookingMapper.toDto(booking);
    }

    @Override
    public void deleteBooking(UUID id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found"));
        bookingRepository.delete(booking);
    }
    @Override
    public BookingResponseDto updateBooking(UUID id, BookingRequestDto bookingDto) {
        Optional<Booking> bookingOpt = bookingRepository.findById(id);
        if (bookingOpt.isPresent()) {
            Booking booking = bookingOpt.get();

            Booking updatedBooking = bookingMapper.toModel(bookingDto);
            updatedBooking.setId(booking.getId());

            updatedBooking = bookingRepository.save(updatedBooking);
            return bookingMapper.toDto(updatedBooking);
        } else {
            throw new RuntimeException("Booking not found");
        }
    }
    @Override
    public BookingResponseDto getBookingByName(String name){
        Booking booking = bookingRepository.findByName(name).get();
        return bookingMapper.toDto(booking);
    }
    @Override
    public Booking getBookingByNameBasic(String name){
        Booking booking = bookingRepository.findByName(name).get();
        return booking;
    }
    @Override
    public Booking updateBookingBasic(Booking booking, UserOrder order) {
        booking.setOrder(order);
        bookingRepository.save(booking);
        return booking;
    }
}