package com.example.guesthousebookingsystem.controllers;

import com.example.guesthousebookingsystem.dtos.BookingDTO;
import com.example.guesthousebookingsystem.services.BookingConflictException;
import com.example.guesthousebookingsystem.services.BookingService;
import com.example.guesthousebookingsystem.services.CustomerNotFoundException;
import com.example.guesthousebookingsystem.services.CustomerServiceUnavailableException;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/bookings")
public class BookingApiController {

    private final BookingService bookingService;

    public BookingApiController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @GetMapping("/customers/{customerId}/has-active")
    public boolean hasActiveBookings(@PathVariable Long customerId) {
        return bookingService.hasActiveBookings(customerId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookingDTO create(@Valid @RequestBody BookingDTO bookingDTO) {
        try {
            return bookingService.save(bookingDTO);
        } catch (CustomerNotFoundException | NoSuchElementException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (BookingConflictException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        } catch (CustomerServiceUnavailableException e) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
                    "Kunde inte kontrollera kunden just nu, försök igen senare");
        }
    }
}