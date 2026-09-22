package com.shenq.courtbooking.venue.controller;

import com.shenq.courtbooking.venue.dto.VenueCreateRequest;
import com.shenq.courtbooking.venue.dto.VenueResponse;
import com.shenq.courtbooking.venue.service.VenueService;
import com.shenq.courtbooking.venue.dto.VenueSummaryResponse;
import com.shenq.courtbooking.court.entity.SportType;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestParam;

import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.List;

@RestController
@RequestMapping("/api/venues")
public class VenueController {

    private final VenueService venueService;

    public VenueController(VenueService venueService) {
        this.venueService = venueService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public VenueResponse createVenue(
            @Valid @RequestBody VenueCreateRequest request) {
        return venueService.createVenue(request);
    }

    @GetMapping
    public Page<VenueSummaryResponse> getVenues(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int size) {
        return venueService.getVenueSummaries(page, size);
    }

    @GetMapping("/{id}")
    public VenueResponse getVenueById(@PathVariable Long id) {
        return venueService.getVenueById(id);
    }

    @GetMapping("/{venueId}/nearby")
    public List<VenueSummaryResponse> getNearbyVenues(
            @PathVariable Long venueId,
            @RequestParam(defaultValue = "3") int limit) {
        return venueService.getNearbyVenues(
                venueId,
                limit);
    }

    @GetMapping("/search")
    public Page<VenueSummaryResponse> searchVenues(
            @RequestParam(required = false) 
            String location,
            @RequestParam(required = false) 
            SportType sport,
            @RequestParam(defaultValue = "0") 
            int page,
            @RequestParam(defaultValue = "9") 
            int size
        ) {
        return venueService.searchVenues(
                location,
                sport,
                page,
                size);
    }

}