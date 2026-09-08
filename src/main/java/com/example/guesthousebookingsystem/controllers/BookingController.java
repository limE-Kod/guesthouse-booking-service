package com.example.guesthousebookingsystem.controllers;

import com.example.guesthousebookingsystem.dtos.BookingDTO;
import com.example.guesthousebookingsystem.dtos.RoomDTO;
import com.example.guesthousebookingsystem.services.BookingService;
import com.example.guesthousebookingsystem.services.RoomService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.example.guesthousebookingsystem.dtos.CustomerDto;
import com.example.guesthousebookingsystem.services.CustomerServiceClient;
import com.example.guesthousebookingsystem.services.CustomerServiceUnavailableException;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/bookings")
public class BookingController {

    private final BookingService bookingService;
    private final RoomService roomService;
    private final CustomerServiceClient customerServiceClient;

    public BookingController(BookingService bookingService, RoomService roomService,
                             CustomerServiceClient customerServiceClient) {
        this.bookingService = bookingService;
        this.roomService = roomService;
        this.customerServiceClient = customerServiceClient;

    }


    @GetMapping
    public String listBookings(Model model) {
        model.addAttribute("bookings", bookingService.getAllBookings());
        return "bookings/list";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("booking", new BookingDTO());
        model.addAttribute("customers", getCustomersOrEmpty(model));
        return "bookings/form";
    }

    @GetMapping("/available-rooms")
    public String showAvailableRooms(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkIn,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkOut,
            @RequestParam int numberOfPeople,
            Model model) {
        List<RoomDTO> availableRooms = bookingService.getAvailableRooms(checkIn, checkOut, numberOfPeople);
        model.addAttribute("booking", new BookingDTO());
        model.addAttribute("availableRooms", availableRooms);
        model.addAttribute("checkIn", checkIn);
        model.addAttribute("checkOut", checkOut);
        model.addAttribute("customers", getCustomersOrEmpty(model));
        return "bookings/form";
    }

    @PostMapping("/save")
    public String saveBooking(@ModelAttribute BookingDTO bookingDTO, RedirectAttributes redirectAttributes) {
        try {
            bookingService.save(bookingDTO);
            redirectAttributes.addFlashAttribute("successMessage", "Bokningen sparades!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/bookings";
    }


    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        model.addAttribute("booking", bookingService.getById(id));
        model.addAttribute("rooms", roomService.getAllRooms());
        model.addAttribute("customers", getCustomersOrEmpty(model));
        return "bookings/form";
    }

    @PostMapping("/delete/{id}")
    public String deleteBooking(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            bookingService.delete(id);
            redirectAttributes.addFlashAttribute("successMessage", "Bokningen avbokades!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Kunde inte avboka!");
        }
        return "redirect:/bookings";
    }
    private List<CustomerDto> getCustomersOrEmpty(Model model) {
        try {
            return customerServiceClient.getAllCustomers();
        } catch (CustomerServiceUnavailableException e) {
            model.addAttribute("customerServiceWarning", "Kundtjänsten är inte tillgänglig just nu, kundlistan kunde inte hämtas.");
            return List.of();
        }
    }

}