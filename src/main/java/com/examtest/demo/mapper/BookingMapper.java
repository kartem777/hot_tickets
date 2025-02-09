package com.examtest.demo.mapper;

import com.examtest.demo.config.MapperConfig;
import com.examtest.demo.dto.booking.BookingRequestDto;
import com.examtest.demo.dto.booking.BookingResponseDto;
import com.examtest.demo.model.Booking;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapperConfig.class)
public interface BookingMapper {
    BookingResponseDto toDto(Booking booking);
    Booking toModel(BookingRequestDto requestDto);
}
