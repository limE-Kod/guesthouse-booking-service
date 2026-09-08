package com.example.guesthousebookingsystem.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    @Bean
    public RestClient customerRestClient() {
        return RestClient.builder()
                .baseUrl("http://localhost:8080/api/customers")
                .build();
    }
}