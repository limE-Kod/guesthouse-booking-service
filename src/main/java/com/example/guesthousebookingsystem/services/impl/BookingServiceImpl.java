package com.example.guesthousebookingsystem.services.impl;

import com.example.guesthousebookingsystem.dtos.BookingDTO;
import com.example.guesthousebookingsystem.dtos.RoomDTO;
import com.example.guesthousebookingsystem.models.Booking;
import com.example.guesthousebookingsystem.models.Room;
import com.example.guesthousebookingsystem.repositories.BookingRepository;
import com.example.guesthousebookingsystem.repositories.RoomRepository;
import com.example.guesthousebookingsystem.services.*;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final RoomRepository roomRepository;
    private final CustomerServiceClient customerServiceClient;


    public BookingServiceImpl(BookingRepository bookingRepository,
                              RoomRepository roomRepository, CustomerServiceClient customerServiceClient) {
        this.bookingRepository = bookingRepository;
        this.roomRepository = roomRepository;
        this.customerServiceClient = customerServiceClient;
    }

    @Override
    public List<BookingDTO> getAllBookings() {
        return bookingRepository.findAll()
                .stream()
                .map(b -> new BookingDTO(b.getId(), b.getCheckIn(), b.getCheckOut(),
                        b.getCustomerid(), b.getRoomid()))
                .toList();
    }

    @Override
    public BookingDTO getById(Long id) {
        Booking booking = bookingRepository.findById(id).orElseThrow();
        return new BookingDTO(booking.getId(), booking.getCheckIn(), booking.getCheckOut(),
                booking.getCustomerid(), booking.getRoomid());
    }

    @Override
    public boolean hasActiveBookings(Long customerId) {
        return bookingRepository.existsByCustomerId(customerId);
    }

    @Override
    public BookingDTO save(BookingDTO bookingDTO) {
        if (!customerServiceClient.customerExists(bookingDTO.getCustomerId())) {
            throw new CustomerNotFoundException(
                    "Customer " + bookingDTO.getCustomerId() + " does not exist");
        }

        boolean conflict = bookingRepository.existsConflictingBooking(
                bookingDTO.getRoomId(),
                bookingDTO.getCheckIn(),
                bookingDTO.getCheckOut(),
                bookingDTO.getId()
        );
        if (conflict) {
            throw new BookingConflictException("Room is not available for the selected dates");
        }

        Room room = roomRepository.findById(bookingDTO.getRoomId()).orElseThrow();

        Booking booking = new Booking();
        booking.setId(bookingDTO.getId());
        booking.setCheckIn(bookingDTO.getCheckIn());
        booking.setCheckOut(bookingDTO.getCheckOut());
        booking.setCustomerid(bookingDTO.getCustomerId());
        booking.setRoomid(room.getId());

        Booking saved = bookingRepository.save(booking);
        return new BookingDTO(saved.getId(), saved.getCheckIn(), saved.getCheckOut(),
                saved.getCustomerid(), saved.getRoomid());
    }

    @Override
    public void delete(Long id) {
        bookingRepository.deleteById(id);
    }

    @Override
    public List<RoomDTO> getAvailableRooms(LocalDate checkIn, LocalDate checkOut, int numberOfPeople) {
        List<Long> bookedRoomIds = bookingRepository.findBookedRoomIds(checkIn, checkOut);
        return roomRepository.findAll()
                .stream()
                .filter(r -> !bookedRoomIds.contains(r.getId()))
                .filter(r -> r.getMaxCapacity() >= numberOfPeople)
                .map(r -> new RoomDTO(r.getName(), r.getId(), r.getRoomType(), r.getExtraBeds(), r.getMaxCapacity()))
                .toList();
    }
}
