package com.example.guesthousebookingsystem;

import com.example.guesthousebookingsystem.models.Customer;
import com.example.guesthousebookingsystem.repositories.BookingRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CustomerServiceImplTest {

    @Mock
    CustomerRepository customerRepository;

    @Mock
    BookingRepository bookingRepository;

    @InjectMocks
    CustomerServiceImpl customerService;

    @Test
    void getAllCustomers_shouldReturnListOfCustomerDTOs() {
        Customer customer1 = new Customer("Anna");
        customer1.setId(1L);
        Customer customer2 = new Customer("Erik");
        customer2.setId(2L);

        when(customerRepository.findAll()).thenReturn(List.of(customer1, customer2));

        List<CustomerDTO> result = customerService.getAllCustomers();

        assertEquals(2, result.size());
        assertEquals("Anna", result.get(0).getName());
        assertEquals("Erik", result.get(1).getName());
    }

    @Test
    void getById_shouldReturnCorrectCustomerDTO() {
        Customer customer = new Customer("Anna");
        customer.setId(1L);

        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));

        CustomerDTO result = customerService.getById(1L);

        assertEquals("Anna", result.getName());
        assertEquals(1L, result.getId());
    }

    @Test
    void save_shouldSaveCustomer() {
        CustomerDTO customerDTO = new CustomerDTO("Anna", null);

        customerService.save(customerDTO);

        verify(customerRepository).save(new Customer("Anna"));
    }

    @Test
    void delete_shouldDeleteCustomer_whenNoBookingsExist() {
        when(bookingRepository.existsByCustomerId(1L)).thenReturn(false);

        customerService.delete(1L);

        verify(customerRepository).deleteById(1L);
    }

    @Test
    void delete_shouldThrowException_whenCustomerHasBookings() {
        when(bookingRepository.existsByCustomerId(1L)).thenReturn(true);

        assertThrows(RuntimeException.class, () -> customerService.delete(1L));

        verify(customerRepository, never()).deleteById(1L);
    }
}