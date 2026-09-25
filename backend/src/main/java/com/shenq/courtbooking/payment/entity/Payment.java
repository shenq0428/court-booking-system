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
import jakarta.persistence.UniqueConstraint;
import jakarta.persistence.Version;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Locale;
import java.util.Objects;


@Entity
@Table(
    name="payments",
    indexes={
        @Index(
            name="idx_payment_booking_created",
            columnList = "booking_id,created_at"
        ),
        @Index(
            name="idx_payment_status",
            columnList = "status"
        )
    },
    uniqueConstraints={
        @UniqueConstraint(
            name= "uk_payment_provider_session",
            columnNames ={
                "provider",
                "provider_session_id"
            }
        ),
        @UniqueConstraint(
            name = "uk_payment_provider_transaction",
            columnNames={
                "provider",
                "provider_payment_id"
            }
        )
    }
)

public class Payment{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY,optional = false)
    @JoinColumn( name = "booking_id",nullable = false)
    private Booking booking;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length =30)
    private PaymentProvider provider;

    @Column(name = "provider_session_id",length = 255)
    private String providerSessionId;

    @Column( name = "provider_payment_id",length = 255)
    private String providerPaymentId;

    @Column(nullable = false,precision = 10,scale = 2)
    private BigDecimal amount;

    @Column(nullable = false, length = 3)
    private String currency;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private PaymentStatus status;

    @Column(name = "failure_reason",columnDefinition = "TEXT")
    private String failureReason;

    @Column( name = "created_at",nullable = false,updatable = false)
    private Instant createdAt;

    @Column(name = "paid_at")
    private Instant paidAt;

    @Column(name = "failed_at")
    private Instant failedAt;

    @Column(name = "cancelled_at")
    private Instant cancelledAt;

    @Column(name = "refund_requested_at")
    private Instant refundRequestedAt;

    @Column(name = "refunded_at")
    private Instant refundedAt;

    @Version
    private Long version;

    protected Payment() {
    }

    public Payment(
            Booking booking,
            PaymentProvider provider,
            BigDecimal amount,
            String currency,
            Instant createdAt
    ) {
        this.booking = Objects.requireNonNull(
                booking,
                "Booking is required"
        );

        this.provider = Objects.requireNonNull(
                provider,
                "Payment provider is required"
        );

        this.amount = Objects.requireNonNull(
                amount,
                "Payment amount is required"
        );

        if (amount.signum() <= 0) {
            throw new IllegalArgumentException(
                    "Payment amount must be greater than zero"
            );
        }

        if (currency == null || currency.length() != 3) {
            throw new IllegalArgumentException(
                    "Currency must contain three letters"
            );
        }

        this.currency = currency.toUpperCase(Locale.ROOT);

        this.createdAt = Objects.requireNonNull(
                createdAt,
                "Created time is required"
        );

        this.status = PaymentStatus.PENDING;
    }

    public void attachProviderSession(
            String providerSessionId
    ) {
        if (
                providerSessionId == null
                || providerSessionId.isBlank()
        ) {
            throw new IllegalArgumentException(
                    "Provider session ID is required"
            );
        }

        if (
                this.providerSessionId != null
                && !this.providerSessionId.equals(
                        providerSessionId
                )
        ) {
            throw new IllegalStateException(
                    "Payment already has a different provider session"
            );
        }

        this.providerSessionId = providerSessionId;
    }

    public void markSucceeded(
            String providerPaymentId,
            Instant paidAt
    ) {
        if (
                status == PaymentStatus.SUCCEEDED
                || status == PaymentStatus.REFUND_PENDING
                || status == PaymentStatus.REFUNDED
        ) {
            return;
        }

        if (status != PaymentStatus.PENDING) {
            throw new IllegalStateException(
                    "Only a pending payment can succeed"
            );
        }

        this.providerPaymentId = providerPaymentId;
        this.paidAt = paidAt;
        this.failureReason = null;
        this.status = PaymentStatus.SUCCEEDED;
    }

    public void markFailed(
            String failureReason,
            Instant failedAt
    ) {
        if (status == PaymentStatus.FAILED) {
            return;
        }

        if (status != PaymentStatus.PENDING) {
            throw new IllegalStateException(
                    "Only a pending payment can fail"
            );
        }

        this.failureReason = failureReason;
        this.failedAt = failedAt;
        this.status = PaymentStatus.FAILED;
    }

    public void cancel(Instant cancelledAt) {
        if (status == PaymentStatus.CANCELLED) {
            return;
        }

        if (status != PaymentStatus.PENDING) {
            throw new IllegalStateException(
                    "Only a pending payment can be cancelled"
            );
        }

        this.cancelledAt = cancelledAt;
        this.status = PaymentStatus.CANCELLED;
    }

    public void requestRefund(Instant requestedAt) {
        if (
                status == PaymentStatus.REFUND_PENDING
                || status == PaymentStatus.REFUNDED
        ) {
            return;
        }

        if (status != PaymentStatus.SUCCEEDED) {
            throw new IllegalStateException(
                    "Only a successful payment can be refunded"
            );
        }

        this.refundRequestedAt = requestedAt;
        this.status = PaymentStatus.REFUND_PENDING;
    }

    public void markRefunded(Instant refundedAt) {
        if (status == PaymentStatus.REFUNDED) {
            return;
        }

        if (status != PaymentStatus.REFUND_PENDING) {
            throw new IllegalStateException(
                    "Refund has not been requested"
            );
        }

        this.refundedAt = refundedAt;
        this.status = PaymentStatus.REFUNDED;
    }

    public boolean isPending() {
        return status == PaymentStatus.PENDING;
    }

    public boolean isSucceeded() {
        return status == PaymentStatus.SUCCEEDED;
    }

    public Long getId() {
        return id;
    }

    public Booking getBooking() {
        return booking;
    }

    public PaymentProvider getProvider() {
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

    public String getFailureReason() {
        return failureReason;
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

    public Instant getCancelledAt() {
        return cancelledAt;
    }

    public Instant getRefundRequestedAt() {
        return refundRequestedAt;
    }

    public Instant getRefundedAt() {
        return refundedAt;
    }

    public Long getVersion() {
        return version;
    }

}