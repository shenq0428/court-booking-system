package com.shenq.courtbooking.booking.repository;

import com.shenq.courtbooking.booking.entity.Booking;
import com.shenq.courtbooking.booking.entity.BookingStatus;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.Lock;

import jakarta.persistence.LockModeType;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface BookingRepository
                extends JpaRepository<Booking, Long> {

        Optional<Booking> 
        findByIdAndUser_Id(
                        Long bookingId,
                        Long userId
                      );

        List<Booking> 
        findAllByUser_IdOrderByStartAtDesc(
                        Long userId
                      );

        List<Booking> 
        findAllByCourt_IdAndStatusAndExpiresAtLessThanEqual(
                        Long courtId,
                        BookingStatus status,
                        Instant now
                      );

        List<Booking> 
        findAllByStatusAndExpiresAtLessThanEqual(
                        BookingStatus status,
                        Instant now
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
                        @Param("courtId") Long courtId,

                        @Param("requestedStart") Instant requestedStart,

                        @Param("requestedEnd") Instant requestedEnd,

                        @Param("confirmedStatus") BookingStatus confirmedStatus,

                        @Param("pendingStatus") BookingStatus pendingStatus,

                        @Param("now") Instant now);

        @Query("""
                        SELECT booking
                        FROM Booking booking
                        WHERE booking.court.venue.id = :venueId
                          AND booking.startAt < :dayEnd
                          AND booking.endAt > :dayStart
                          AND (
                                booking.status = :confirmedStatus
                                OR (
                                    booking.status = :pendingStatus
                                    AND booking.expiresAt > :now
                                )
                          )
                        """)
        List<Booking> 
        findBlockingBookingsForVenue(
                        @Param("venueId") Long venueId,
                        @Param("dayStart") Instant dayStart,
                        @Param("dayEnd") Instant dayEnd,
                        @Param("confirmedStatus") BookingStatus confirmedStatus,
                        @Param("pendingStatus") BookingStatus pendingStatus,
                        @Param("now") Instant now);

@Lock(LockModeType.PESSIMISTIC_WRITE)
@Query("""
        SELECT booking
        FROM Booking booking
        WHERE booking.id = :bookingId
          AND booking.user.id = :userId
        """)
Optional<Booking> findOwnedBookingForUpdate(
        @Param("bookingId") Long bookingId,
        @Param("userId") Long userId
);

}