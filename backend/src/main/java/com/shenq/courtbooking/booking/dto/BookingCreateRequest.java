package com.shenq.courtbooking.booking.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;

public record BookingCreateRequest(

        @NotNull
        Long courtId,

        @NotNull
        @Future
        Instant startAt,

        @NotNull
        @Future
        Instant endAt

) {
}