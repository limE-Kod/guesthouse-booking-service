package com.example.guesthousebookingsystem.services.impl;

import com.example.guesthousebookingsystem.dtos.BookingDTO;
import com.example.guesthousebookingsystem.dtos.RoomDTO;
import com.example.guesthousebookingsystem.models.Booking;
import com.example.guesthousebookingsystem.models.Room;
import com.example.guesthousebookingsystem.repositories.BookingRepository;
import com.example.guesthousebookingsystem.repositories.RoomRepository;
import com.example.guesthousebookingsystem.services.BookingService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final RoomRepository roomRepository;


    public BookingServiceImpl(BookingRepository bookingRepository,
                              RoomRepository roomRepository) {
        this.bookingRepository = bookingRepository;
        this.roomRepository = roomRepository;
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
    public void save(BookingDTO bookingDTO) {
        boolean customerExists;
        try {
            customerExists = customerServiceClient.customerExists(bookingDTO.getCustomerId());
        } catch (CustomerServiceUnavailableException e) {
            throw new RuntimeException("Customer is not verified - Customer Service dosent respond");
        }

        if (!customerExists) {
            throw new RuntimeException("Customer couldnt be found");
        }


        boolean conflict = bookingRepository.existsConflictingBooking(
                bookingDTO.getRoomId(),
                bookingDTO.getCheckIn(),
                bookingDTO.getCheckOut(),
                bookingDTO.getId()
        );
        if (conflict) {
            throw new RuntimeException("Room is not available for the selected dates");
        }


        Room room = roomRepository.findById(bookingDTO.getRoomId())
                .orElseThrow(() -> new RuntimeException("Room not found"));


        Booking booking = new Booking();
        booking.setId(bookingDTO.getId());
        booking.setCheckIn(bookingDTO.getCheckIn());
        booking.setCheckOut(bookingDTO.getCheckOut());
        booking.setCustomerid(bookingDTO.getCustomerId());
        booking.setRoomid(room.getId());

        bookingRepository.save(booking);
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
