package com.shenq.courtbooking.booking.controller;

import com.shenq.courtbooking.booking.dto.BookingCreateRequest;
import com.shenq.courtbooking.booking.dto.BookingResponse;
import com.shenq.courtbooking.booking.service.BookingService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/bookings")
public class BookingController{
    private final BookingService bookingService;

    public BookingController(
        BookingService bookingService
    ){
        this.bookingService = bookingService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookingResponse createBooking(
        @AuthenticationPrincipal Jwt jwt,
        @Valid @RequestBody
        BookingCreateRequest request
    ){
        Long userId = Long.valueOf(jwt.getSubject());

        return bookingService.createBooking(
            userId,
            request
        );
    }
}