package com.shenq.courtbooking.venue.service;

import com.shenq.courtbooking.venue.dto.VenueCreateRequest;
import com.shenq.courtbooking.venue.dto.VenueResponse;
import com.shenq.courtbooking.venue.entity.Venue;
import com.shenq.courtbooking.venue.repository.VenueRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class VenueService {

    private final VenueRepository venueRepository;

    public VenueService(VenueRepository venueRepository) {
        this.venueRepository = venueRepository;
    }

    @Transactional
    public VenueResponse createVenue(VenueCreateRequest request) {

        Venue venue = new Venue(
                request.getName(),
                request.getDescription(),
                request.getAddressLine1(),
                request.getAddressLine2(),
                request.getCity(),
                request.getState(),
                request.getPostalCode(),
                request.getLatitude(),
                request.getLongitude(),
                request.getPhoneNumber());

        Venue savedVenue = venueRepository.save(venue);

        return convertToResponse(savedVenue);
    }

    @Transactional(readOnly = true)
    public List<VenueResponse> getAllVenues() {

        List<Venue> venues = venueRepository.findAll();
        List<VenueResponse> responses = new ArrayList<>();

        for (Venue venue : venues) {
            VenueResponse response = convertToResponse(venue);
            responses.add(response);
        }

        return responses;
    }

    private VenueResponse convertToResponse(Venue venue) {

        return new VenueResponse(
                venue.getId(),
                venue.getName(),
                venue.getDescription(),
                venue.getAddressLine1(),
                venue.getAddressLine2(),
                venue.getCity(),
                venue.getState(),
                venue.getPostalCode(),
                venue.getLatitude(),
                venue.getLongitude(),
                venue.getPhoneNumber(),
                venue.isActive());
    }

    @Transactional(readOnly = true)
    public VenueResponse getVenueById(Long id) {

        Venue venue = venueRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Venue not found with id: " + id));
        return convertToResponse(venue);
    }
}