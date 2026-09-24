package com.shenq.courtbooking.booking.dto;

import java.time.Instant;

public record AvailabilitySlotResponse(
    Instant startAt,
    Instant endAt,
    boolean available
){
    
}