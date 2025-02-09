package com.examtest.demo.dto.booking;

import jakarta.validation.constraints.*;

import java.util.UUID;
public class BookingRequestDto{
    @NotBlank
    @Size(min = 5, max = 40)
    String name;
    @NotBlank
    @Size(min = 20, max = 255)
    String description;
    @NotNull
    @Min(25)
    int price;

    public @NotBlank @Size(min = 5, max = 40) String getName() {
        return name;
    }

    public @NotBlank @Size(min = 20, max = 255) String getDescription() {
        return description;
    }

    @NotNull
    @Min(25)
    public int getPrice() {
        return price;
    }

    public void setName(@NotBlank @Size(min = 5, max = 40) String name) {
        this.name = name;
    }

    public void setDescription(@NotBlank @Size(min = 20, max = 255) String description) {
        this.description = description;
    }

    public void setPrice(@NotNull @Min(25) int price) {
        this.price = price;
    }
    public BookingRequestDto(){}
    public BookingRequestDto(String name, String description, int price) {
        this.name = name;
        this.description = description;
        this.price = price;
    }
}
