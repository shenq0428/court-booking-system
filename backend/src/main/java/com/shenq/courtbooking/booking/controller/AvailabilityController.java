package com.shenq.courtbooking.booking.controller;

import com.shenq.courtbooking.booking.dto.VenueAvailabilityResponse;
import com.shenq.courtbooking.booking.service.AvailabilityService;
import com.shenq.courtbooking.court.entity.SportType;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/venues")
public class AvailabilityController {

    private final AvailabilityService availabilityService;

    public AvailabilityController(
            AvailabilityService availabilityService
    ) {
        this.availabilityService = availabilityService;
    }

    @GetMapping("/{venueId}/availability")
    public VenueAvailabilityResponse getAvailability(
            @PathVariable Long venueId,

            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate date,

            @RequestParam(required = false)
            SportType sport
    ) {
        return availabilityService.getAvailability(
                venueId,
                date,
                sport
        );
    }
}