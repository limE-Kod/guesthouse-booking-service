package com.example.guesthousebookingsystem;

import com.example.guesthousebookingsystem.models.Booking;
import com.example.guesthousebookingsystem.repositories.BookingRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.resttestclient.TestRestTemplate;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class BookingApiControllerIntegrationTest {

    @Autowired
    TestRestTemplate restTemplate;

    @Autowired
    BookingRepository bookingRepository;

    @Test
    void hasActiveBookings_returnsTrue_whenCustomerHasABooking() {
        Booking booking = new Booking();
        booking.setCheckIn(LocalDate.of(2026, 6, 1));
        booking.setCheckOut(LocalDate.of(2026, 6, 5));
        booking.setCustomerid(42L);
        booking.setRoomid(1L);
        bookingRepository.save(booking);

        Boolean result = restTemplate.getForObject("/api/bookings/customers/42/has-active",
                Boolean.class);

        assertTrue(result);
    }

    @Test
    void hasActiveBookings_returnsFalse_whenCustomerHasNoBookings() {
        Boolean result = restTemplate.getForObject("/api/bookings/customers/999/has-active",
                Boolean.class);

        assertFalse(result);
    }
}