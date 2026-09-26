package com.shenq.courtbooking.payment.service;

import com.shenq.courtbooking.booking.entity.Booking;
import com.shenq.courtbooking.booking.repository.BookingRepository;
import com.shenq.courtbooking.common.exception.BookingConflictException;
import com.shenq.courtbooking.payment.dto.PaymentCheckoutRequest;
import com.shenq.courtbooking.payment.dto.PaymentCheckoutResponse;
import com.shenq.courtbooking.payment.dto.PaymentStatusResponse;
import com.shenq.courtbooking.payment.entity.Payment;
import com.shenq.courtbooking.payment.entity.PaymentProvider;
import com.shenq.courtbooking.payment.entity.PaymentStatus;
import com.shenq.courtbooking.payment.repository.PaymentRepository;

import com.stripe.StripeClient;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import com.stripe.param.checkout.SessionRetrieveParams;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

@Service
public class PaymentService {

    private static final String CURRENCY = "MYR";

    private static final Duration CHECKOUT_DURATION = Duration.ofMinutes(31);

    private final PaymentRepository paymentRepository;
    private final BookingRepository bookingRepository;
    private final StripeClient stripeClient;
    private final String frontendUrl;

    private static final Logger LOGGER =  LoggerFactory.getLogger( PaymentService.class );

    public PaymentService(
            PaymentRepository paymentRepository,
            BookingRepository bookingRepository,
            StripeClient stripeClient,
            @Value("${app.frontend-url}") String frontendUrl) {
        this.paymentRepository = paymentRepository;
        this.bookingRepository = bookingRepository;
        this.stripeClient = stripeClient;
        this.frontendUrl = frontendUrl;
    }

    @Transactional
    public PaymentCheckoutResponse createCheckout(
            Long userId,
            PaymentCheckoutRequest request) {
        Instant now = Instant.now();

        Booking booking = bookingRepository
                .findOwnedBookingForUpdate(
                        request.bookingId(),
                        userId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Booking not found"));

        validateBookingForPayment(
                booking,
                now);

        Optional<Payment> existingPendingPayment = paymentRepository
                .findFirstByBooking_IdAndStatusOrderByCreatedAtDesc(
                        booking.getId(),
                        PaymentStatus.PENDING);

        if (existingPendingPayment.isPresent()) {
            Payment existingPayment = existingPendingPayment.get();

            PaymentCheckoutResponse existingCheckout = retrieveExistingCheckout(
                    existingPayment);

            if (existingCheckout != null) {
                return existingCheckout;
            }

            existingPayment.cancel(now);
        }

        Instant checkoutExpiresAt = now.plus(CHECKOUT_DURATION);

        booking.extendPaymentExpiry(
                checkoutExpiresAt);

        Payment payment = new Payment(
                booking,
                PaymentProvider.STRIPE,
                booking.getPriceAtBooking(),
                CURRENCY,
                now);

        Payment savedPayment = paymentRepository.saveAndFlush(
                payment);

        SessionCreateParams params = buildCheckoutParams(
                savedPayment,
                checkoutExpiresAt);

        try {
            Session session = stripeClient
                    .v1()
                    .checkout()
                    .sessions()
                    .create(params);

            savedPayment.attachProviderSession(
                    session.getId());

            paymentRepository.save(savedPayment);

            return new PaymentCheckoutResponse(
                    savedPayment.getId(),
                    session.getUrl());
        } catch (StripeException exception) {
            LOGGER.error("Stripe Checkout creation failed for bookingId={} and paymentId={}",
                booking.getId(),savedPayment.getId(),exception
            );
            throw new IllegalStateException("Unable to create Stripe Checkout Session", exception);
        }
    }

    private void validateBookingForPayment(
            Booking booking,
            Instant now) {
        if (!booking.isPaymentPending()) {
            throw new BookingConflictException("This booking is not waiting for payment");
        }

        if (booking.isExpiredAt(now)) {
            throw new BookingConflictException("This booking has expired");
        }

        boolean alreadyPaid = paymentRepository
                .existsByBooking_IdAndStatus(
                        booking.getId(),
                        PaymentStatus.SUCCEEDED);

        if (alreadyPaid) {
            throw new BookingConflictException("This booking has already been paid");
        }
    }

    private PaymentCheckoutResponse retrieveExistingCheckout(
            Payment payment) {
        if (payment.getProviderSessionId() == null || payment.getProviderSessionId().isBlank()) {
            return null;
        }

        try {
            SessionRetrieveParams params = SessionRetrieveParams
                    .builder()
                    .build();

            Session session = stripeClient
                    .v1()
                    .checkout()
                    .sessions()
                    .retrieve(payment.getProviderSessionId(), params);

            if ("open".equals(session.getStatus())
                    && session.getUrl() != null) {
                return new PaymentCheckoutResponse(
                        payment.getId(),
                        session.getUrl());
            }

            return null;
        } catch (StripeException exception) {
            LOGGER.error("Stripe checkout connection failed for paymentId={}",payment.getId(),exception);
            
            throw new IllegalStateException( "Unable to retrieve Stripe Checkout Session", exception);
        }
    }

    private SessionCreateParams buildCheckoutParams(
            Payment payment,
            Instant checkoutExpiresAt) {
        Booking booking = payment.getBooking();

        long amountInSen = convertRinggitToSen(payment.getAmount());

        String productName = booking.getCourt()
                .getVenue()
                .getName()
                + " - Court "
                + booking.getCourt()
                        .getCourtNumber();

        return SessionCreateParams
                .builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSubmitType(SessionCreateParams.SubmitType.BOOK)
                .setSuccessUrl(
                        frontendUrl
                                + "/payment/success"
                                + "?session_id={CHECKOUT_SESSION_ID}")
                .setCancelUrl(
                        frontendUrl
                                + "/my-bookings"
                                + "?payment=cancelled")
                .setClientReferenceId(booking.getId().toString())
                .setCustomerEmail(booking.getUser().getEmail())
                .setExpiresAt(checkoutExpiresAt.getEpochSecond())
                .putMetadata(
                        "paymentId",
                        payment.getId().toString())
                .putMetadata(
                        "bookingId",
                        booking.getId().toString())
                .putMetadata(
                        "userId",
                        booking.getUser()
                                .getId()
                                .toString())
                .addLineItem(
                        SessionCreateParams.LineItem
                                .builder()
                                .setQuantity(1L)
                                .setPriceData(
                                        SessionCreateParams.LineItem.PriceData
                                                .builder()
                                                .setCurrency("myr")
                                                .setUnitAmount(
                                                        amountInSen)
                                                .setProductData(
                                                        SessionCreateParams.LineItem.PriceData.ProductData
                                                                .builder()
                                                                .setName(
                                                                        productName)
                                                                .build())
                                                .build())
                                .build())
                .build();
    }

    private long convertRinggitToSen(
            BigDecimal amount
        ) {
        return amount .movePointRight(2).longValueExact();
    }

    @Transactional(readOnly = true)
        public PaymentStatusResponse getCheckoutStatus(
                Long userId,
                String sessionId
                ) {
    if (sessionId == null || sessionId.isBlank()) {
        throw new IllegalArgumentException("Checkout Session ID is required");
    }

    Payment payment = paymentRepository
            .findByProviderSessionIdAndBooking_User_Id( sessionId, userId)
            .orElseThrow(() -> new IllegalArgumentException( "Payment session not found")
            );

    Booking booking = payment.getBooking();

    return new PaymentStatusResponse(
            payment.getId(),
            booking.getId(),
            payment.getStatus(),
            booking.getStatus()
    );
}

}