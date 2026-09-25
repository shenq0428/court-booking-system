package com.shenq.courtbooking.payment.service;

import com.shenq.courtbooking.booking.entity.Booking;
import com.shenq.courtbooking.payment.entity.Payment;
import com.shenq.courtbooking.payment.entity.PaymentProvider;
import com.shenq.courtbooking.payment.repository.PaymentRepository;

import com.stripe.net.Webhook;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.model.StripeObject;
import com.stripe.model.checkout.Session;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
public class PaymentWebhookService {

    private final PaymentRepository paymentRepository;
    private final String webhookSecret;

    public PaymentWebhookService(
            PaymentRepository paymentRepository,
            @Value("${stripe.webhook-secret}")
            String webhookSecret
    ) {
        this.paymentRepository = paymentRepository;
        this.webhookSecret = webhookSecret;
    }

    @Transactional
    public void handleWebhook(
            String payload,
            String signatureHeader
    ) {
        Event event;

        try {
            event = Webhook.constructEvent(
                    payload,
                    signatureHeader,
                    webhookSecret
            );
        } catch (SignatureVerificationException exception) {
            throw new IllegalArgumentException( "Invalid Stripe webhook signature",exception );
        }

        if (!"checkout.session.completed".equals(
                event.getType()
        )) {
            return;
        }

        StripeObject stripeObject = event
                .getDataObjectDeserializer()
                .getObject()
                .orElseThrow(() ->new IllegalArgumentException( "Cannot read Stripe event data") );

        if (!(stripeObject instanceof Session session)) {
            throw new IllegalArgumentException("Stripe event does not contain a Checkout Session" );
        }

        if (!"paid".equals(session.getPaymentStatus())) {
            return;
        }

        Payment payment = paymentRepository
                .findByProviderAndProviderSessionIdForUpdate(
                        PaymentProvider.STRIPE,
                        session.getId()
                )
                .orElseThrow(() ->new IllegalStateException( "Payment not found for Stripe session: "+ session.getId())
                );

        // Stripe 可能重复发送相同事件。
        if (payment.isSucceeded()) {
            return;
        }

        Instant paidAt = Instant.ofEpochSecond(event.getCreated());

        Booking booking = payment.getBooking();

        payment.markSucceeded(
                session.getPaymentIntent(),
                paidAt
        );

        if (!booking.isPaymentPending()|| booking.isExpiredAt(paidAt)
        ) {
            payment.requestRefund(paidAt);
            return;
        }

        booking.confirm(paidAt);
    }
}