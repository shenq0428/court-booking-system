package com.shenq.courtbooking.payment.dto;

import com.shenq.courtbooking.payment.entity.PaymentProvider;
import com.shenq.courtbooking.payment.entity.PaymentStatus;

import java.math.BigDecimal;
import java.time.Instant;

public record PaymentResponse(
        Long id,
        Long bookingId,
        PaymentProvider provider,
        BigDecimal amount,
        String currency,
        PaymentStatus status,
        String failureReason,
        Instant createdAt,
        Instant paidAt,
        Instant failedAt,
        Instant cancelledAt,
        Instant refundRequestedAt,
        Instant refundedAt
) {
}