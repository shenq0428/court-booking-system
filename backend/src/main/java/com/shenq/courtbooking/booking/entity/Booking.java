package com.shenq.courtbooking.booking.entity;

import com.shenq.courtbooking.court.entity.Court;
import com.shenq.courtbooking.user.entity.AppUser;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(
        name = "bookings",
        indexes = {
                @Index(
                        name = "idx_booking_court_start",
                        columnList = "court_id,start_at"
                ),
                @Index(
                        name = "idx_booking_user_created",
                        columnList = "user_id,created_at"
                ),
                @Index(
                        name = "idx_booking_status_expiry",
                        columnList = "status,expires_at"
                )
        }
)
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "user_id",
            nullable = false
    )
    private AppUser user;

    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "court_id",
            nullable = false
    )
    private Court court;

    @Column(
            name = "start_at",
            nullable = false
    )
    private Instant startAt;

    @Column(
            name = "end_at",
            nullable = false
    )
    private Instant endAt;

    @Column(
            name = "price_at_booking",
            nullable = false,
            precision = 10,
            scale = 2
    )
    private BigDecimal priceAtBooking;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private BookingStatus status;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    @Column(
            name = "created_at",
            nullable = false,
            updatable = false
    )
    private Instant createdAt;

    @Column(name = "confirmed_at")
    private Instant confirmedAt;

    @Column(name = "cancelled_at")
    private Instant cancelledAt;

    @Version
    private Long version;

    protected Booking() {
        // JPA requires a no-argument constructor.
    }

    public Booking(
            AppUser user,
            Court court,
            Instant startAt,
            Instant endAt,
            BigDecimal priceAtBooking,
            Instant expiresAt,
            Instant createdAt
    ) {
        this.user = user;
        this.court = court;
        this.startAt = startAt;
        this.endAt = endAt;
        this.priceAtBooking = priceAtBooking;
        this.expiresAt = expiresAt;
        this.createdAt = createdAt;
        this.status = BookingStatus.PENDING_PAYMENT;
    }

    public void confirm(Instant confirmedAt) {
        if (status == BookingStatus.CONFIRMED) {
            return;
        }

        if (status != BookingStatus.PENDING_PAYMENT) {
            throw new IllegalStateException("Only a pending booking can be confirmed");
        }

        this.status = BookingStatus.CONFIRMED;
        this.confirmedAt = confirmedAt;
    }

    public void cancel(Instant cancelledAt) {
        if (status == BookingStatus.CANCELLED) {
            return;
        }

        if (
                status != BookingStatus.PENDING_PAYMENT &&
                status != BookingStatus.CONFIRMED
        ) {
            throw new IllegalStateException( "This booking cannot be cancelled" );
        }

        this.status = BookingStatus.CANCELLED;
        this.cancelledAt = cancelledAt;
    }

    public void expire(Instant expiredAt) {
        if (status == BookingStatus.EXPIRED) {
            return;
        }

        if (status != BookingStatus.PENDING_PAYMENT) {
            throw new IllegalStateException("Only a pending booking can expire");
        }

        if (expiresAt.isAfter(expiredAt)) {
            throw new IllegalStateException("The booking has not expired yet");
        }

        this.status = BookingStatus.EXPIRED;
    }

    public boolean isPaymentPending() {
        return status == BookingStatus.PENDING_PAYMENT;
    }

    public boolean isExpiredAt(Instant now) {
        return status == BookingStatus.PENDING_PAYMENT
                && !expiresAt.isAfter(now);
    }

    public Long getId() {
        return id;
    }

    public AppUser getUser() {
        return user;
    }

    public Court getCourt() {
        return court;
    }

    public Instant getStartAt() {
        return startAt;
    }

    public Instant getEndAt() {
        return endAt;
    }

    public BigDecimal getPriceAtBooking() {
        return priceAtBooking;
    }

    public BookingStatus getStatus() {
        return status;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getConfirmedAt() {
        return confirmedAt;
    }

    public Instant getCancelledAt() {
        return cancelledAt;
    }

    public Long getVersion() {
        return version;
    }
}