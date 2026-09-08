package com.example.guesthousebookingsystem.controllers;

import com.example.guesthousebookingsystem.repositories.BookingRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/bookings")
public class BookingApiController {

    private final BookingRepository bookingRepository;

    public BookingApiController(BookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
    }

    @GetMapping("/customers/{customerId}/has-active")
    public boolean hasActiveBookings(@PathVariable Long customerId) {
        return bookingRepository.existsByCustomerId(customerId);
    }
}