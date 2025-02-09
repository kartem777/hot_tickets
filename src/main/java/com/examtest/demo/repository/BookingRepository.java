package com.examtest.demo.repository;

import com.examtest.demo.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface BookingRepository extends JpaRepository<Booking, UUID> {
    Optional<Booking> findByName(String name);
    boolean existsByName(String name);
}