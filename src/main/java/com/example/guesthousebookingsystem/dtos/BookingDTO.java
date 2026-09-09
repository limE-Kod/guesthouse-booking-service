package com.example.guesthousebookingsystem.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingDTO {
    private Long id;
    @NotNull(message = "Check-in date is required")
    private LocalDate checkIn;
    @NotNull(message = "Check-out datae is required")
    private LocalDate checkOut;
    @NotNull(message = "Customer id is required")
    private Long customerId;
    @NotNull(message = "Room id is required")
    private Long roomId;
}
