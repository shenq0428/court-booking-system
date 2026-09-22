package com.shenq.courtbooking.venue.service;

import com.shenq.courtbooking.venue.dto.VenueCreateRequest;
import com.shenq.courtbooking.venue.dto.VenueResponse;
import com.shenq.courtbooking.venue.dto.VenueSummaryResponse;
import com.shenq.courtbooking.venue.entity.Venue;
import com.shenq.courtbooking.venue.repository.VenueRepository;

import com.shenq.courtbooking.court.entity.Court;
import com.shenq.courtbooking.court.entity.SportType;
import com.shenq.courtbooking.court.repository.CourtRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

@Service
public class VenueService {

    private final VenueRepository venueRepository;
    private final CourtRepository courtRepository;

    public VenueService(VenueRepository venueRepository, CourtRepository courtRepository) {
        this.venueRepository = venueRepository;
        this.courtRepository = courtRepository;
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

        venue.setImageUrl(request.getImageUrl());

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
                venue.getImageUrl(),
                venue.isActive());
    }

    @Transactional(readOnly = true)
    public VenueResponse getVenueById(Long id) {

        Venue venue = venueRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Venue not found with id: " + id));
        return convertToResponse(venue);
    }

    // nearby location method
    @Transactional(readOnly = true)
    public List<VenueSummaryResponse> getNearbyVenues(
            Long venueId,
            int limit) {
        Venue sourceVenue = venueRepository
                .findById(venueId)
                .orElseThrow(() -> new RuntimeException("Venue not found with id: " + venueId));

        int safeLimit = Math.max(
                1,
                Math.min(limit, 6));

        List<Venue> nearbyVenues;
        if (sourceVenue.getLatitude() != null
                && sourceVenue.getLongitude() != null) {
            nearbyVenues = venueRepository
                    .findNearbyByCoordinates(
                            sourceVenue.getId(),
                            sourceVenue.getLatitude(),
                            sourceVenue.getLongitude(),
                            safeLimit);
        } else {
            Pageable pageable = PageRequest.of(0, safeLimit);
            nearbyVenues = venueRepository
                    .findByActiveTrueAndIdNotAndCityIgnoreCaseOrderByNameAsc(
                            sourceVenue.getId(),
                            sourceVenue.getCity(),
                            pageable);
        }

        List<Long> nearbyVenueIds = new ArrayList<>();

        for (Venue venue : nearbyVenues) {
            nearbyVenueIds.add(venue.getId());
        }

        List<Court> courts;

        if (nearbyVenueIds.isEmpty()) {
            courts = List.of();
        } else {
            courts = courtRepository
                    .findAllByVenue_IdInAndActiveTrue(nearbyVenueIds);
        }

        Map<Long, List<Court>> courtsByVenueId = new HashMap<>();

        for (Court court : courts) {
            Long courtVenueId = court.getVenue().getId();

            if (!courtsByVenueId.containsKey(
                    courtVenueId)) {
                courtsByVenueId.put(
                        courtVenueId,
                        new ArrayList<>());
            }

            courtsByVenueId
                    .get(courtVenueId)
                    .add(court);
        }

        List<VenueSummaryResponse> responses = new ArrayList<>();

        for (Venue venue : nearbyVenues) {
            List<Court> venueCourts = courtsByVenueId.getOrDefault(
                    venue.getId(),
                    List.of());

            responses.add(
                    convertToSummaryResponse(
                            venue,
                            venueCourts));
        }

        return responses;
    }

    // 查询
    @Transactional(readOnly = true)
    public Page<VenueSummaryResponse> getVenueSummaries(int page, int size) {
        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by("name").ascending());
        Page<Venue> venuePage = venueRepository.findAllByActiveTrue(pageable);

        List<Long> venueIds = new ArrayList<>();

        for (Venue venue : venuePage.getContent()) {
            venueIds.add(venue.getId());
        }

        List<Court> courts;
        if (venueIds.isEmpty()) {
            courts = List.of();
        } else {
            courts = courtRepository.findAllByVenue_IdInAndActiveTrue(venueIds);
        }

        Map<Long, List<Court>> courtsByVenueId = new HashMap<>();
        for (Court court : courts) {
            Long venueId = court.getVenue().getId();

            if (!courtsByVenueId.containsKey(venueId)) {
                courtsByVenueId.put(venueId, new ArrayList<>());
            }
            courtsByVenueId.get(venueId).add(court);
        }

        return venuePage.map(venue -> {
            List<Court> venueCourts = courtsByVenueId.getOrDefault(venue.getId(), List.of());

            return convertToSummaryResponse(venue, venueCourts);
        });
    }

    // 转换方法
    private VenueSummaryResponse convertToSummaryResponse(Venue venue, List<Court> courts) {
        List<SportType> sports = new ArrayList<>();
        BigDecimal startingPrice = null;

        for (Court court : courts) {
            if (!sports.contains(court.getSport())) {
                sports.add(court.getSport());
            }

            if (startingPrice == null || court.getPricePerHour().compareTo(startingPrice) < 0) {
                startingPrice = court.getPricePerHour();
            }
        }

        return new VenueSummaryResponse(
                venue.getId(),
                venue.getName(),
                buildAddress(venue),
                sports,
                startingPrice,
                venue.getImageUrl());
    }

    // 加入地址组合方法
    private String buildAddress(Venue venue) {
        List<String> addressParts = new ArrayList<>();
        addAddressPart(addressParts, venue.getAddressLine1());
        addAddressPart(addressParts, venue.getAddressLine2());
        addAddressPart(addressParts, venue.getCity());
        addAddressPart(addressParts, venue.getState());

        return String.join(", ", addressParts);
    }

    private void addAddressPart(List<String> addressParts, String value) {
        if (value != null && !value.isBlank()) {
            addressParts.add(value);
        }
    }
}