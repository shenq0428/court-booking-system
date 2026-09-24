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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {
    private final BookingService bookingService;

    public BookingController(
            BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookingResponse createBooking(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody BookingCreateRequest request) {
        Long userId = Long.valueOf(jwt.getSubject());

        return bookingService.createBooking(
                userId,
                request);
    }

    @GetMapping("/me")
    public List<BookingResponse> getMyBookings(
            @AuthenticationPrincipal Jwt jwt) {
        Long userId = Long.valueOf(jwt.getSubject());

        return bookingService.getMyBookings(userId);
    }

    @PostMapping("/{bookingId}/cancel")
    public BookingResponse cancelPendingBooking(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long bookingId) {
        Long userId = Long.valueOf(jwt.getSubject());

        return bookingService.cancelPendingBooking(
                userId,
                bookingId);
    }
}