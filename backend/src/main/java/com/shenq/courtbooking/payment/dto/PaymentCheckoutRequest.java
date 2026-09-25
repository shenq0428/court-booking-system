package com.shenq.courtbooking.payment.dto;

import jakarta.validation.constraints.NotNull;

public record PaymentCheckoutRequest(

        @NotNull
        Long bookingId

) {
}