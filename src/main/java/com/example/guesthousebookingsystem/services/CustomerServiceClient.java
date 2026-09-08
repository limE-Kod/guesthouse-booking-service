package com.example.guesthousebookingsystem.services;

import com.example.guesthousebookingsystem.dtos.CustomerDTO;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;

import java.util.List;

@Service
public class CustomerServiceClient {

    private final RestClient restClient;

    public CustomerServiceClient(RestClient customerRestClient) {
        this.restClient = customerRestClient;
    }

    public List<CustomerDTO> getAllCustomers() {
        try {
            return restClient.get()
                    .retrieve()
                    .body(new ParameterizedTypeReference<List<CustomerDTO>>() {});
        } catch (ResourceAccessException e) {
            throw new CustomerServiceUnavailableException("Kundtjänsten är inte tillgänglig just nu", e);
        }
    }

    public boolean customerExists(Long customerId) {
        try {
            restClient.get()
                    .uri("/{id}", customerId)
                    .retrieve()
                    .toBodilessEntity();
            return true;
        } catch (HttpClientErrorException.NotFound e) {
            return false;
        } catch (ResourceAccessException e) {
            throw new CustomerServiceUnavailableException("Kundtjänsten är inte tillgänglig just nu", e);
        }
    }
}