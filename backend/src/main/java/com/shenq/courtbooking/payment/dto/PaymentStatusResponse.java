package com.shenq.courtbooking.payment.dto;

import com.shenq.courtbooking.booking.entity.BookingStatus;
import com.shenq.courtbooking.payment.entity.PaymentStatus;

public record PaymentStatusResponse (
    Long paymentId,
    Long bookingId,
    PaymentStatus paymentStatus,
    BookingStatus bookingStatus
){
}
