package com.shenq.courtbooking.court.dto;

import com.shenq.courtbooking.court.entity.SportType;

import java.math.BigDecimal;

public record CourtResponse(
        Long id,
        Long venueId,
        Integer courtNumber,
        SportType sport,
        BigDecimal pricePerHour,
        boolean active
) {
}