package com.shenq.courtbooking.payment.repository;

import com.shenq.courtbooking.payment.entity.Payment;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository
        extends JpaRepository<Payment, Long> {

    Optional<Payment> findByProviderSessionId(
            String providerSessionId
    );

    Optional<Payment> findByProviderPaymentId(
            String providerPaymentId
    );

    List<Payment>
    findAllByBooking_IdOrderByCreatedAtDesc(
            Long bookingId
    );

    boolean existsByProviderSessionId(
            String providerSessionId
    );
}