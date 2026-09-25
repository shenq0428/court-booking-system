package com.shenq.courtbooking.payment.dto;

public record PaymentCheckoutResponse(
        Long paymentId,
        String checkoutUrl
) {
}