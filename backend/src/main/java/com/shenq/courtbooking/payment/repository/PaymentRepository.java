package com.shenq.courtbooking.payment.repository;

import com.shenq.courtbooking.payment.entity.Payment;
import com.shenq.courtbooking.payment.entity.PaymentProvider;
import com.shenq.courtbooking.payment.entity.PaymentStatus;

import jakarta.persistence.LockModeType;

import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository
                extends JpaRepository<Payment, Long> {

        List<Payment>// 查询某个 Booking 的所有付款尝试
                        findAllByBooking_IdOrderByCreatedAtDesc(
                                        Long bookingId);

        Optional<Payment> findFirstByBooking_IdAndStatusOrderByCreatedAtDesc(
                        Long bookingId,
                        PaymentStatus status);

        Optional<Payment>// Stripe Webhook 根据 Checkout Session 找 Payment
                        findByProviderAndProviderSessionId(
                                        PaymentProvider provider,
                                        String providerSessionId);

        Optional<Payment>// 根据 Stripe Payment ID 找 Payment
                        findByProviderAndProviderPaymentId(
                                        PaymentProvider provider,
                                        String providerPaymentId);

        // 检查 Booking 是否已经成功付款
        boolean existsByBooking_IdAndStatus(
                        Long bookingId,
                        PaymentStatus status);

        @Lock(LockModeType.PESSIMISTIC_WRITE)
        @Query("""
                        SELECT payment
                        FROM Payment payment
                        WHERE payment.provider = :provider
                          AND payment.providerSessionId = :providerSessionId
                        """)
        Optional<Payment> findByProviderAndProviderSessionIdForUpdate(
                        @Param("provider") PaymentProvider provider,
                        @Param("providerSessionId") String providerSessionId);

        Optional<Payment> findByProviderSessionIdAndBooking_User_Id(
                        String providerSessionId,
                        Long userId);
}