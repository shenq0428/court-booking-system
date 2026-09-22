package com.shenq.courtbooking.court.controller;

import com.shenq.courtbooking.court.dto.CourtCreateRequest;
import com.shenq.courtbooking.court.dto.CourtResponse;
import com.shenq.courtbooking.court.service.CourtService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class CourtController {

    private final CourtService courtService;

    public CourtController(CourtService courtService) {
        this.courtService = courtService;
    }

    @PostMapping("/courts")
    @ResponseStatus(HttpStatus.CREATED)
    public CourtResponse createCourt(
            @Valid @RequestBody CourtCreateRequest request
    ) {
        return courtService.createCourt(request);
    }

    @GetMapping("/venues/{venueId}/courts")
    public List<CourtResponse>getActiveCourtsByVenueId(
        @PathVariable Long venueId
    ){
        return courtService.getActiveCourtsByVenueId(venueId);
    }
}