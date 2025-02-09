package com.examtest.demo;

import com.examtest.demo.dto.booking.BookingRequestDto;
import com.examtest.demo.dto.booking.BookingResponseDto;
import com.examtest.demo.exception.RegistrationException;
import com.examtest.demo.mapper.BookingMapper;
import com.examtest.demo.model.Booking;
import com.examtest.demo.repository.BookingRepository;
import com.examtest.demo.service.impl.BookingServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BookingServiceImplTest {

    @InjectMocks
    private BookingServiceImpl bookingService;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private BookingMapper bookingMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getAllBooking_ShouldReturnListOfBookings() {
        List<Booking> bookings = Stream.of(new Booking(), new Booking()).collect(Collectors.toList());
        when(bookingRepository.findAll()).thenReturn(bookings);
        when(bookingMapper.toDto(any())).thenReturn(new BookingResponseDto());

        List<BookingResponseDto> result = bookingService.getAllBooking();

        assertEquals(2, result.size());
        verify(bookingRepository).findAll();
    }

    @Test
    void addBooking_ShouldSaveBooking_WhenNameIsUnique() {
        BookingRequestDto bookingRequestDto = new BookingRequestDto("New Booking", "New Desc", 99);
        Booking booking = new Booking();
        when(bookingRepository.existsByName(bookingRequestDto.getName())).thenReturn(false);
        when(bookingMapper.toModel(bookingRequestDto)).thenReturn(booking);
        when(bookingRepository.save(booking)).thenReturn(booking);
        when(bookingMapper.toDto(booking)).thenReturn(new BookingResponseDto());

        BookingResponseDto result = bookingService.addBooking(bookingRequestDto);

        assertNotNull(result);
        verify(bookingRepository).save(booking);
    }

    @Test
    void addBooking_ShouldThrowException_WhenNameAlreadyExists() {
        BookingRequestDto bookingRequestDto = new BookingRequestDto("Existing Booking", "New Desc", 989);
        when(bookingRepository.existsByName(bookingRequestDto.getName())).thenReturn(true);

        assertThrows(RegistrationException.class, () -> bookingService.addBooking(bookingRequestDto));
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void getBookingById_ShouldReturnBooking_WhenFound() {
        UUID id = UUID.randomUUID();
        Booking booking = new Booking();
        when(bookingRepository.findById(id)).thenReturn(Optional.of(booking));
        when(bookingMapper.toDto(booking)).thenReturn(new BookingResponseDto());

        BookingResponseDto result = bookingService.getBookingById(id);

        assertNotNull(result);
        verify(bookingRepository).findById(id);
    }

    @Test
    void getBookingById_ShouldThrowException_WhenNotFound() {
        UUID id = UUID.randomUUID();
        when(bookingRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> bookingService.getBookingById(id));
        verify(bookingRepository).findById(id);
    }

    @Test
    void deleteBooking_ShouldDeleteBooking_WhenFound() {
        UUID id = UUID.randomUUID();
        Booking booking = new Booking();
        when(bookingRepository.findById(id)).thenReturn(Optional.of(booking));

        bookingService.deleteBooking(id);

        verify(bookingRepository).delete(booking);
    }

    @Test
    void updateBooking_ShouldUpdateBooking_WhenFound() {
        UUID id = UUID.randomUUID();
        BookingRequestDto bookingRequestDto = new BookingRequestDto("Updated Booking", "New Desc", 999);
        Booking existingBooking = new Booking();
        Booking updatedBooking = new Booking();

        when(bookingRepository.findById(id)).thenReturn(Optional.of(existingBooking));
        when(bookingMapper.toModel(bookingRequestDto)).thenReturn(updatedBooking);
        when(bookingRepository.save(updatedBooking)).thenReturn(updatedBooking);
        when(bookingMapper.toDto(updatedBooking)).thenReturn(new BookingResponseDto());

        BookingResponseDto result = bookingService.updateBooking(id, bookingRequestDto);

        assertNotNull(result);
        verify(bookingRepository).save(updatedBooking);
    }

    @Test
    void updateBooking_ShouldThrowException_WhenNotFound() {
        UUID id = UUID.randomUUID();
        BookingRequestDto bookingRequestDto = new BookingRequestDto("Non-Existent Booking", "New Desc", 97);

        when(bookingRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> bookingService.updateBooking(id, bookingRequestDto));
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void getBookingByName_ShouldReturnBooking_WhenFound() {
        String name = "Test Booking";
        Booking booking = new Booking();
        when(bookingRepository.findByName(name)).thenReturn(Optional.of(booking));
        when(bookingMapper.toDto(booking)).thenReturn(new BookingResponseDto());

        BookingResponseDto result = bookingService.getBookingByName(name);

    }
}