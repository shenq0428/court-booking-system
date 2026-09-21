package com.shenq.courtbooking.court.service;

import com.shenq.courtbooking.court.dto.CourtCreateRequest;
import com.shenq.courtbooking.court.dto.CourtResponse;
import com.shenq.courtbooking.court.entity.Court;
import com.shenq.courtbooking.court.repository.CourtRepository;
import com.shenq.courtbooking.venue.entity.Venue;
import com.shenq.courtbooking.venue.repository.VenueRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CourtService {

    private final CourtRepository courtRepository;
    private final VenueRepository venueRepository;

    public CourtService(
            CourtRepository courtRepository,
            VenueRepository venueRepository
    ) {
        this.courtRepository = courtRepository;
        this.venueRepository = venueRepository;
    }

    @Transactional
    public CourtResponse createCourt(
            CourtCreateRequest request
    ) {
        Venue venue = venueRepository
                .findById(request.venueId())
                .orElseThrow(() ->
                        new RuntimeException( "Venue not found with id: " + request.venueId() )
                );

        Court court = new Court(
                venue,
                request.courtNumber(),
                request.sport(),
                request.pricePerHour()
        );

        Court savedCourt = courtRepository.save(court);

        return convertToResponse(savedCourt);
    }

    private CourtResponse convertToResponse(Court court) {
        return new CourtResponse(
                court.getId(),
                court.getVenue().getId(),
                court.getCourtNumber(),
                court.getSport(),
                court.getPricePerHour(),
                court.isActive()
        );
    }
}