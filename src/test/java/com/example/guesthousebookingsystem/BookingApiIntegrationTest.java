package com.example.guesthousebookingsystem;

import com.example.guesthousebookingsystem.dtos.BookingDTO;
import com.example.guesthousebookingsystem.models.Room;
import com.example.guesthousebookingsystem.models.RoomType;
import com.example.guesthousebookingsystem.repositories.BookingRepository;
import com.example.guesthousebookingsystem.repositories.RoomRepository;
import com.example.guesthousebookingsystem.services.CustomerServiceClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureTestRestTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestRestTemplate
class BookingApiIntegrationTest {

    @Autowired
    TestRestTemplate restTemplate;

    @Autowired
    RoomRepository roomRepository;

    @Autowired
    BookingRepository bookingRepository;

    @MockitoBean
    CustomerServiceClient customerServiceClient;

    @Test
    void createBooking_shouldReturn201AndSaveToDatabase() {
        when(customerServiceClient.customerExists(any())).thenReturn(true);

        Room room = roomRepository.save(new Room("201", RoomType.SINGLE));

        BookingDTO newBooking = new BookingDTO(null,
                LocalDate.of(2027, 3, 1),
                LocalDate.of(2027, 3, 5),
                1L, room.getId());

        ResponseEntity<BookingDTO> response =
                restTemplate.postForEntity("/api/bookings", newBooking, BookingDTO.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertTrue(bookingRepository.findById(response.getBody().getId()).isPresent());
    }

    @Test
    void createBooking_shouldReturn409_whenRoomIsAlreadyBooked() {
        when(customerServiceClient.customerExists(any())).thenReturn(true);

        Room room = roomRepository.save(new Room("202", RoomType.DOUBLE));

        BookingDTO firstBooking = new BookingDTO(null,
                LocalDate.of(2027, 4, 1),
                LocalDate.of(2027, 4, 5),
                1L, room.getId());
        ResponseEntity<BookingDTO> firstResponse =
                restTemplate.postForEntity("/api/bookings", firstBooking, BookingDTO.class);
        assertEquals(HttpStatus.CREATED, firstResponse.getStatusCode());

        BookingDTO overlappingBooking = new BookingDTO(null,
                LocalDate.of(2027, 4, 3),
                LocalDate.of(2027, 4, 7),
                2L, room.getId());

        ResponseEntity<String> response =
                restTemplate.postForEntity("/api/bookings", overlappingBooking, String.class);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
    }
}