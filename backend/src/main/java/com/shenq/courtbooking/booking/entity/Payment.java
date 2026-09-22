package com.shenq.courtbooking.payment.entity;

import com.shenq.courtbooking.booking.entity.Booking;

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
import java.util.Locale;

@Entity
@Table(
        name = "payments",
        indexes = {
                @Index(
                        name = "idx_payment_booking",
                        columnList = "booking_id"
                ),
                @Index(
                        name = "idx_payment_status",
                        columnList = "status"
                )
        }
)
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "booking_id",
            nullable = false
    )
    private Booking booking;

    @Column(
            nullable = false,
            length = 30
    )
    private String provider;

    @Column(
            name = "provider_session_id",
            nullable = false,
            unique = true,
            length = 255
    )
    private String providerSessionId;

    @Column(
            name = "provider_payment_id",
            unique = true,
            length = 255
    )
    private String providerPaymentId;

    @Column(
            nullable = false,
            precision = 10,
            scale = 2
    )
    private BigDecimal amount;

    @Column(
            nullable = false,
            length = 3
    )
    private String currency;

    @Enumerated(EnumType.STRING)
    @Column(
            nullable = false,
            length = 30
    )
    private PaymentStatus status;

    @Column(
            name = "created_at",
            nullable = false,
            updatable = false
    )
    private Instant createdAt;

    @Column(name = "paid_at")
    private Instant paidAt;

    @Column(name = "failed_at")
    private Instant failedAt;

    @Column(name = "refunded_at")
    private Instant refundedAt;

    @Version
    private Long version;

    protected Payment() {
        // JPA requires a no-argument constructor.
    }

    public Payment(
            Booking booking,
            String provider,
            String providerSessionId,
            BigDecimal amount,
            String currency,
            Instant createdAt
    ) {
        this.booking = booking;
        this.provider = provider;
        this.providerSessionId =
                providerSessionId;
        this.amount = amount;
        this.currency = currency
                .trim()
                .toUpperCase(Locale.ROOT);
        this.createdAt = createdAt;
        this.status = PaymentStatus.PENDING;
    }

    public void markSucceeded(
            String providerPaymentId,
            Instant paidAt
    ) {
        if (status == PaymentStatus.SUCCEEDED) {
            return;
        }

        if (status != PaymentStatus.PENDING) {
            throw new IllegalStateException("Only a pending payment can succeed");
        }

        this.providerPaymentId =  providerPaymentId;
        this.status = PaymentStatus.SUCCEEDED;
        this.paidAt = paidAt;
    }

    public void markFailed(Instant failedAt) {
        if (status == PaymentStatus.FAILED) {
            return;
        }

        if (status != PaymentStatus.PENDING) {
            throw new IllegalStateException( "Only a pending payment can fail");
        }

        this.status = PaymentStatus.FAILED;
        this.failedAt = failedAt;
    }

    public void markRefunded(Instant refundedAt) {
        if (status == PaymentStatus.REFUNDED) {
            return;
        }

        if (status != PaymentStatus.SUCCEEDED) {
            throw new IllegalStateException( "Only a successful payment can be refunded" );
        }

        this.status = PaymentStatus.REFUNDED;
        this.refundedAt = refundedAt;
    }

    public Long getId() {
        return id;
    }

    public Booking getBooking() {
        return booking;
    }

    public String getProvider() {
        return provider;
    }

    public String getProviderSessionId() {
        return providerSessionId;
    }

    public String getProviderPaymentId() {
        return providerPaymentId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getCurrency() {
        return currency;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getPaidAt() {
        return paidAt;
    }

    public Instant getFailedAt() {
        return failedAt;
    }

    public Instant getRefundedAt() {
        return refundedAt;
    }

    public Long getVersion() {
        return version;
    }
}