package com.example.guesthousebookingsystem;
import com.example.guesthousebookingsystem.services.CustomerServiceClient;
import com.example.guesthousebookingsystem.dtos.BookingDTO;
import com.example.guesthousebookingsystem.dtos.RoomDTO;
import com.example.guesthousebookingsystem.models.Booking;
import com.example.guesthousebookingsystem.models.Room;
import com.example.guesthousebookingsystem.models.RoomType;
import com.example.guesthousebookingsystem.repositories.BookingRepository;
import com.example.guesthousebookingsystem.repositories.RoomRepository;
import com.example.guesthousebookingsystem.services.impl.BookingServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BookingServiceImplTest {

    @Mock
    BookingRepository bookingRepository;

    @Mock
    RoomRepository roomRepository;

    @Mock
    CustomerServiceClient customerServiceClient;

    @InjectMocks
    BookingServiceImpl bookingService;

    @Test
    void getAllBookings_shouldReturnListOfBookingDTOs() {


        Room room = new Room("101");
        room.setId(1L);

        Booking booking = new Booking();
        booking.setId(1L);
        booking.setCheckIn(LocalDate.of(2026, 6, 1));
        booking.setCheckOut(LocalDate.of(2026, 6, 5));
        booking.setCustomerid(1L);
        booking.setRoomid(room.getId());

        when(bookingRepository.findAll()).thenReturn(List.of(booking));

        List<BookingDTO> result = bookingService.getAllBookings();

        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getCustomerId());
        assertEquals(1L, result.get(0).getRoomId());
    }

    @Test
    void getById_shouldReturnCorrectBookingDTO() {

        Room room = new Room("101");
        room.setId(1L);

        Booking booking = new Booking();
        booking.setId(1L);
        booking.setCheckIn(LocalDate.of(2026, 6, 1));
        booking.setCheckOut(LocalDate.of(2026, 6, 5));
        booking.setCustomerid(1L);
        booking.setRoomid(room.getId());

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        BookingDTO result = bookingService.getById(1L);

        assertEquals(1L, result.getId());
        assertEquals(1L, result.getCustomerId());
        assertEquals(1L, result.getRoomId());
    }

    @Test
    void save_shouldThrowException_whenConflictExists() {
        BookingDTO bookingDTO = new BookingDTO(null,
                LocalDate.of(2026, 6, 1),
                LocalDate.of(2026, 6, 5),
                1L, 1L);
        when(customerServiceClient.customerExists(any())).thenReturn(true);
        when(bookingRepository.existsConflictingBooking(1L,
                LocalDate.of(2026, 6, 1),
                LocalDate.of(2026, 6, 5),
                null)).thenReturn(true);

        assertThrows(RuntimeException.class, () -> bookingService.save(bookingDTO));
    }

    @Test
    void save_shouldSaveBooking_whenNoConflict() {
        BookingDTO bookingDTO = new BookingDTO(null,
                LocalDate.of(2026, 6, 1),
                LocalDate.of(2026, 6, 5),
                1L, 1L);
        when(customerServiceClient.customerExists(any())).thenReturn(true);


        Room room = new Room("101");
        room.setId(1L);

        when(bookingRepository.existsConflictingBooking(any(), any(), any(), any())).thenReturn(false);

        when(roomRepository.findById(1L)).thenReturn(Optional.of(room));

        bookingService.save(bookingDTO);

        verify(bookingRepository).save(any(Booking.class));
    }

    @Test
    void delete_shouldDeleteBooking() {
        bookingService.delete(1L);

        verify(bookingRepository).deleteById(1L);
    }

    @Test
    void getAvailableRooms_shouldReturnOnlyAvailableRooms() {
        Room room1 = new Room("101");
        room1.setId(1L);
        room1.setRoomType(RoomType.SINGLE);
        room1.setExtraBeds(0);

        Room room2 = new Room("102");
        room2.setId(2L);
        room2.setRoomType(RoomType.DOUBLE);
        room2.setExtraBeds(2);

        when(roomRepository.findAll()).thenReturn(List.of(room1, room2));
        when(bookingRepository.findBookedRoomIds(any(), any())).thenReturn(List.of(1L));

        List<RoomDTO> result = bookingService.getAvailableRooms(
                LocalDate.of(2026, 6, 1),
                LocalDate.of(2026, 6, 5),
                2);

        assertEquals(1, result.size());
        assertEquals("102", result.get(0).getName());
    }
}