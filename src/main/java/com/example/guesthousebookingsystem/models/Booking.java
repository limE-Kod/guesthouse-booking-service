package com.example.guesthousebookingsystem.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor(access = AccessLevel.PUBLIC)
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@Entity
public class Booking {

    @Id
    @GeneratedValue
    private Long id;

    @NotNull(message="Checkin date required")
    private LocalDate checkIn;
    @NotNull(message="Checkout date required")
    private LocalDate checkOut;

    @NotNull
    private Long customerid;

    @NotNull
    private Long roomid;

}

//GitHub-accesstest