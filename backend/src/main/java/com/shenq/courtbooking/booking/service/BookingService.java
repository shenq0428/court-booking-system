package com.shenq.courtbooking.booking.service;

import com.shenq.courtbooking.booking.dto.BookingCreateRequest;
import com.shenq.courtbooking.booking.dto.BookingResponse;
import com.shenq.courtbooking.booking.entity.Booking;
import com.shenq.courtbooking.booking.entity.BookingStatus;
import com.shenq.courtbooking.booking.repository.BookingRepository;
import com.shenq.courtbooking.common.exception.BookingNotFoundException;
import com.shenq.courtbooking.common.exception.BookingConflictException;
import com.shenq.courtbooking.common.exception.CourtNotFoundException;

import com.shenq.courtbooking.court.entity.Court;
import com.shenq.courtbooking.court.repository.CourtRepository;

import com.shenq.courtbooking.user.entity.AppUser;
import com.shenq.courtbooking.user.repository.AppUserRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.ArrayList;

@Service
public class BookingService {

    private static final Duration PAYMENT_HOLD_DURATION = Duration.ofMinutes(10);

    private static final Duration BOOKING_DURATION = Duration.ofHours(1);

    private final BookingRepository bookingRepository;
    private final CourtRepository courtRepository;
    private final AppUserRepository appUserRepository;

    public BookingService(
            BookingRepository bookingRepository,
            CourtRepository courtRepository,
            AppUserRepository appUserRepository) {
        this.bookingRepository = bookingRepository;
        this.courtRepository = courtRepository;
        this.appUserRepository = appUserRepository;
    }

    @Transactional
    public BookingResponse createBooking(
            Long userId,
            BookingCreateRequest request) {
        Instant now = Instant.now();

        validateBookingTime(request, now);

        AppUser user = appUserRepository
                .findById(userId)
                .orElseThrow(() -> new IllegalStateException("Authenticated user not found"));

        if (!user.isActive()) {
            throw new IllegalStateException("User account is inactive");
        }

        Court court = courtRepository
                .findByIdForUpdate(request.courtId())
                .orElseThrow(() -> new CourtNotFoundException("Court not found with id: " + request.courtId()));

        if (!court.isActive()
                || !court.getVenue().isActive()) {
            throw new BookingConflictException("This court is not available");
        }

        expireOldPendingBookings(
                court.getId(),
                now);

        long conflictCount = bookingRepository.countBlockingBookings(
                court.getId(),
                request.startAt(),
                request.endAt(),
                BookingStatus.CONFIRMED,
                BookingStatus.PENDING_PAYMENT,
                now);

        if (conflictCount > 0) {
            throw new BookingConflictException("This time slot is no longer available");
        }

        Instant expiresAt = now.plus(PAYMENT_HOLD_DURATION);

        Booking booking = new Booking(
                user,
                court,
                request.startAt(),
                request.endAt(),
                court.getPricePerHour(),
                now,
                expiresAt);

        Booking savedBooking = bookingRepository.save(booking);

        return convertToResponse(savedBooking);
    }

    private void validateBookingTime(
            BookingCreateRequest request,
            Instant now) {
        if (!request.startAt().isAfter(now)) {
            throw new IllegalArgumentException("Booking start time must be in the future");
        }

        if (!request.endAt().isAfter(request.startAt())) {
            throw new IllegalArgumentException("Booking end time must be after start time");
        }

        Duration requestedDuration = Duration.between(
                request.startAt(),
                request.endAt());

        if (!requestedDuration.equals(BOOKING_DURATION)) {
            throw new IllegalArgumentException("Booking duration must be exactly one hour");
        }
    }

    private void expireOldPendingBookings(
            Long courtId,
            Instant now) {
        List<Booking> expiredBookings = bookingRepository
                .findAllByCourt_IdAndStatusAndExpiresAtLessThanEqual(
                        courtId,
                        BookingStatus.PENDING_PAYMENT,
                        now);

        for (Booking booking : expiredBookings) {
            booking.expire(now);
        }

        if (!expiredBookings.isEmpty()) {
            bookingRepository.flush();
        }
    }

    private BookingResponse convertToResponse(
            Booking booking) {
        Court court = booking.getCourt();

        return new BookingResponse(
                booking.getId(),
                booking.getUser().getId(),
                court.getId(),
                court.getVenue().getId(),
                court.getVenue().getName(),
                court.getCourtNumber(),
                court.getSport(),
                booking.getStartAt(),
                booking.getEndAt(),
                booking.getPriceAtBooking(),
                booking.getStatus(),
                booking.getExpiresAt(),
                booking.getCreatedAt());
    }

    @Transactional(readOnly = true)
    public List<BookingResponse> getMyBookings(Long userId) {
        List<Booking> bookings = bookingRepository
                .findAllByUser_IdOrderByStartAtDesc(userId);

        List<BookingResponse> response = new ArrayList<>();

        for (Booking booking : bookings) {
            response.add(convertToResponse(booking));
        }
        return response;
    }

    @Transactional
    public int expireOverduePendingBookings() {
        Instant now = Instant.now();

        List<Booking> expiredBookings = bookingRepository
                .findAllByStatusAndExpiresAtLessThanEqual(
                        BookingStatus.PENDING_PAYMENT,
                        now);

        for (Booking booking : expiredBookings) {
            booking.expire(now);
        }

        return expiredBookings.size();
    }

    @Transactional
    public BookingResponse cancelPendingBooking(
            Long userId,
            Long bookingId) {
        Booking booking = bookingRepository
                .findByIdAndUser_Id(bookingId, userId)
                .orElseThrow(() -> new BookingNotFoundException("Booking not found with id:" + bookingId));

        Instant now = Instant.now();

        // 重复取消时直接返回现有结果
        if (booking.getStatus() == BookingStatus.CANCELLED) {
            return convertToResponse(booking);
        }

        // Scheduler 可能还没来得及更新状态，
        // 所以这里也检查真实的 expiresAt。
        if (booking.isExpiredAt(now)) {
            throw new BookingConflictException(
                    "This booking has already expired");
        }

        if (booking.getStatus() != BookingStatus.PENDING_PAYMENT) {
            throw new BookingConflictException(
                    "Only a pending booking can be cancelled");
        }

        if (!now.isBefore(booking.getStartAt())) {
            throw new BookingConflictException(
                    "A booking cannot be cancelled after it has started");
        }

        booking.cancelPending(now);

        return convertToResponse(booking);

    }

}