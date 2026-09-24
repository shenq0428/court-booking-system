package com.shenq.courtbooking.booking.dto;

import java.time.LocalDate;
import java.util.List;

public record VenueAvailabilityResponse(
        Long venueId,
        LocalDate date,
        String timeZone,
        List<CourtAvailabilityResponse> courts
) {
}