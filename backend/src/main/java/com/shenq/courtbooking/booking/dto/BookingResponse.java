package com.shenq.courtbooking.booking.dto;

import com.shenq.courtbooking.booking.entity.BookingStatus;
import com.shenq.courtbooking.court.entity.SportType;

import java.math.BigDecimal;
import java.time.Instant;

public record BookingResponse(
        Long id,
        
        Long userId,

        Long courtId,

        Long venueId,

        String venueName,

        int courtNumber,

        SportType sport,

        Instant startAt,

        Instant endAt,

        BigDecimal priceAtBooking,

        BookingStatus status,

        Instant expiresAt,

        Instant createdAt

) {
}