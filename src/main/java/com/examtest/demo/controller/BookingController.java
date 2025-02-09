package com.examtest.demo.controller;

import com.examtest.demo.dto.booking.BookingRequestDto;
import com.examtest.demo.dto.booking.BookingResponseDto;
import com.examtest.demo.service.BookingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/bookings")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @Operation(
            summary = "Get all bookings",
            responses = {
                    @ApiResponse(responseCode = "200", description = "List of bookings retrieved"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized")
            }
    )
    @GetMapping
    public ResponseEntity<List<BookingResponseDto>> getAllBookings() {
        List<BookingResponseDto> bookings = bookingService.getAllBooking();
        return ResponseEntity.ok(bookings);
    }

    @Operation(
            summary = "Get booking by ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Booking retrieved"),
                    @ApiResponse(responseCode = "404", description = "Booking not found")
            }
    )
    @GetMapping("/{id}")
    public ResponseEntity<BookingResponseDto> getBookingById(@PathVariable UUID id) {
        BookingResponseDto booking = bookingService.getBookingById(id);
        return ResponseEntity.ok(booking);
    }

    @Operation(
            summary = "Create a new booking",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Booking created successfully"),
                    @ApiResponse(responseCode = "400", description = "Invalid request data")
            }
    )
    @PostMapping
    public ResponseEntity<BookingResponseDto> addBooking(@Valid @RequestBody BookingRequestDto bookingRequestDto) {
        BookingResponseDto newBooking = bookingService.addBooking(bookingRequestDto);
        return ResponseEntity.status(201).body(newBooking);
    }

    @Operation(
            summary = "Delete a booking by ID",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Booking deleted successfully"),
                    @ApiResponse(responseCode = "404", description = "Booking not found")
            }
    )
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBooking(@PathVariable UUID id) {
        bookingService.deleteBooking(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Update a booking by ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Booking updated successfully"),
                    @ApiResponse(responseCode = "404", description = "Booking not found")
            }
    )
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<BookingResponseDto> updateBooking(@PathVariable UUID id, @Valid @RequestBody BookingRequestDto bookingRequestDto) {
        BookingResponseDto updatedBooking = bookingService.updateBooking(id, bookingRequestDto);
        return ResponseEntity.ok(updatedBooking);
    }

    @Operation(
            summary = "Get booking by name",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Booking retrieved"),
                    @ApiResponse(responseCode = "404", description = "Booking not found")
            }
    )
    @GetMapping("/name/{name}")
    public ResponseEntity<BookingResponseDto> getBookingByName(@PathVariable String name) {
        BookingResponseDto booking = bookingService.getBookingByName(name);
        return ResponseEntity.ok(booking);
    }
}
