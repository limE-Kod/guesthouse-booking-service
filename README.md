# Guesthouse Booking Service

Part of the entire guesthouse booking system
- **booking-service** handles rooms, bookings and frontend.
- **customer-service** all customer data

## How the services talk
Through REST, neither service accesses the others database.
- When a booking is created, booking-service asks customer-service if the customer exists
- When a customer is deleted, customer-service asks booking-service if that customer has active bookings
- If the other service is down the request fails with a clear message instead of crashing

A booking only stores customer id, not the customer.

## Starting the system
Both repos need to be exist next to each other, then from the **booking-service**-repo 
``` docker compose up --build ``` 

This starts four containers, both services and a database each.
- booking-service: localhost:8081
- customer-service: localhost:8080

## REST API
- GET /api/bookings/customer/{id}/has-active = does this customer have bookings
- POST /api/bookings = create a booking

Body for POST:

``` 
{
"checkIn": "2026-09-13,
"checkOut": "2027-09-16",
"customerId": 1,
"roomId": 1 
} 
```

409 = room already booked those dates
404 = customer or room does not exist

# Frontend
Thymeleaf from monolith project
- http://localhost:8081/bookings
- http://localhost:8081/rooms

# Tests
./mvnw test 
