package com.shenq.courtbooking.booking.service;

import com.shenq.courtbooking.booking.dto.AvailabilitySlotResponse;
import com.shenq.courtbooking.booking.dto.CourtAvailabilityResponse;
import com.shenq.courtbooking.booking.dto.VenueAvailabilityResponse;
import com.shenq.courtbooking.booking.entity.Booking;
import com.shenq.courtbooking.booking.entity.BookingStatus;
import com.shenq.courtbooking.booking.repository.BookingRepository;
import com.shenq.courtbooking.court.entity.Court;
import com.shenq.courtbooking.court.entity.SportType;
import com.shenq.courtbooking.court.repository.CourtRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AvailabilityService {

    private static final ZoneId MALAYSIA_ZONE = ZoneId.of("Asia/Kuala_Lumpur");
    private static final LocalTime OPENING_TIME = LocalTime.of(8, 0);
    private static final LocalTime CLOSING_TIME = LocalTime.of(22, 0);
    private static final Duration SLOT_DURATION = Duration.ofHours(1);
    private final CourtRepository courtRepository;
    private final BookingRepository bookingRepository;

    public AvailabilityService(
            CourtRepository courtRepository,
            BookingRepository bookingRepository
    ) {
        this.courtRepository = courtRepository;
        this.bookingRepository = bookingRepository;
    }

    @Transactional(readOnly = true)
    public VenueAvailabilityResponse getAvailability(
            Long venueId,
            LocalDate date,
            SportType sport
    ) {
        LocalDate today = LocalDate.now(MALAYSIA_ZONE);

        if (date.isBefore(today)) {
            throw new IllegalArgumentException( "Availability date cannot be in the past");
        }

        Instant now = Instant.now();

        Instant dayStart = date
                .atTime(OPENING_TIME)
                .atZone(MALAYSIA_ZONE)
                .toInstant();

        Instant dayEnd = date
                .atTime(CLOSING_TIME)
                .atZone(MALAYSIA_ZONE)
                .toInstant();

        List<Court> allCourts =
                courtRepository
                        .findAllByVenue_IdAndActiveTrueOrderByCourtNumberAsc(
                                venueId
                        );

        List<Booking> blockingBookings =
                bookingRepository
                        .findBlockingBookingsForVenue(
                                venueId,
                                dayStart,
                                dayEnd,
                                BookingStatus.CONFIRMED,
                                BookingStatus.PENDING_PAYMENT,
                                now
                        );

        Map<Long, List<Booking>> bookingsByCourtId =
                new HashMap<>();

        for (Booking booking : blockingBookings) {
            Long courtId = booking.getCourt().getId();

            bookingsByCourtId
                    .computeIfAbsent(
                            courtId,
                            ignored -> new ArrayList<>()
                    )
                    .add(booking);
        }

        List<CourtAvailabilityResponse> courtResponses =
                new ArrayList<>();

        for (Court court : allCourts) {
            if (sport != null && court.getSport() != sport) {
                continue;
            }

            List<Booking> courtBookings =
                    bookingsByCourtId.getOrDefault(
                            court.getId(),
                            List.of()
                    );

            List<AvailabilitySlotResponse> slots =
                    new ArrayList<>();

            for (
                    Instant slotStart = dayStart;
                    slotStart.isBefore(dayEnd);
                    slotStart = slotStart.plus(SLOT_DURATION)
            ) {
                Instant slotEnd =
                        slotStart.plus(SLOT_DURATION);

                boolean available = slotStart.isAfter(now);

                for (Booking booking : courtBookings) {
                    boolean overlaps =
                            booking.getStartAt()
                                    .isBefore(slotEnd)
                            &&
                            booking.getEndAt()
                                    .isAfter(slotStart);

                    if (overlaps) {
                        available = false;
                        break;
                    }
                }

                slots.add(
                        new AvailabilitySlotResponse(
                                slotStart,
                                slotEnd,
                                available
                        )
                );
            }

            courtResponses.add(
                    new CourtAvailabilityResponse(
                            court.getId(),
                            court.getCourtNumber(),
                            court.getSport(),
                            court.getPricePerHour(),
                            slots
                    )
            );
        }

        return new VenueAvailabilityResponse(
                venueId,
                date,
                MALAYSIA_ZONE.getId(),
                courtResponses
        );
    }
}