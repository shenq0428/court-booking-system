package com.shenq.courtbooking.payment.controller;

import com.shenq.courtbooking.payment.service.PaymentWebhookService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/payments")
public class PaymentWebhookController {

    private final PaymentWebhookService paymentWebhookService;

    public PaymentWebhookController(
            PaymentWebhookService paymentWebhookService
    ) {
        this.paymentWebhookService =
                paymentWebhookService;
    }

    @PostMapping("/webhook")
    public ResponseEntity<Void> handleWebhook(
            @RequestBody String payload,
            @RequestHeader("Stripe-Signature")
            String signatureHeader
    ) {
        paymentWebhookService.handleWebhook(
                payload,
                signatureHeader
        );

        return ResponseEntity.ok().build();
    }
}