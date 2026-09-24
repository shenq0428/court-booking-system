package com.shenq.courtbooking.booking.dto;

import com.shenq.courtbooking.court.entity.SportType;

import java.math.BigDecimal;
import java.util.List;

public record CourtAvailabilityResponse(
        Long courtId,
        int courtNumber,
        SportType sport,
        BigDecimal pricePerHour,
        List<AvailabilitySlotResponse> slots
) {
}