package com.example.guesthousebookingsystem.repositories;

import com.example.guesthousebookingsystem.models.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {


    @Query("SELECT CASE WHEN COUNT(b) > 0 THEN true ELSE false END FROM Booking b WHERE b.customerid = :customerId")
    boolean existsByCustomerId(@Param("customerId") Long customerId);

    @Query("SELECT CASE WHEN COUNT(b) > 0 then TRUE else FALSE end from Booking b " +
            "WHERE b.roomid = :roomId " +
            "AND b.checkIn < :checkOut " +
            "AND b.checkOut > :checkIn " +
            "AND (:bookingId IS NULL OR b.id != :bookingId)")
    boolean existsConflictingBooking(@Param("roomId") Long roomId,
                                     @Param("checkIn") LocalDate checkIn,
                                     @Param("checkOut") LocalDate checkOut,
                                     @Param("bookingId") Long bookingId);


    @Query("SELECT b.roomid FROM Booking b WHERE b.checkIn < :checkOut AND b.checkOut > :checkIn")
    List<Long> findBookedRoomIds(@Param("checkIn") LocalDate checkIn,
                                 @Param("checkOut") LocalDate checkOut);
}
