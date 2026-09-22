package com.shenq.courtbooking.booking.repository;

import com.shenq.courtbooking.booking.entity.Booking;
import com.shenq.courtbooking.booking.entity.BookingStatus;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface BookingRepository
        extends JpaRepository<Booking, Long> {

    Optional<Booking> findByIdAndUser_Id(
            Long bookingId,
            Long userId
    );

    Page<Booking> findAllByUser_IdOrderByStartAtDesc(
            Long userId,
            Pageable pageable
    );

    List<Booking>
    findAllByStatusAndExpiresAtLessThanEqual(
            BookingStatus status,
            Instant expiresAt
    );

    @Query("""
            SELECT COUNT(booking)
            FROM Booking booking
            WHERE booking.court.id = :courtId
              AND booking.startAt < :requestedEnd
              AND booking.endAt > :requestedStart
              AND (
                    booking.status = :confirmedStatus
                    OR (
                        booking.status = :pendingStatus
                        AND booking.expiresAt > :now
                    )
              )
            """)
    long countBlockingBookings(
            @Param("courtId")
            Long courtId,

            @Param("requestedStart")
            Instant requestedStart,

            @Param("requestedEnd")
            Instant requestedEnd,

            @Param("now")
            Instant now,

            @Param("confirmedStatus")
            BookingStatus confirmedStatus,

            @Param("pendingStatus")
            BookingStatus pendingStatus
    );
}