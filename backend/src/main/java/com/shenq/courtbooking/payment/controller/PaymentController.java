package com.shenq.courtbooking.payment.controller;

import com.shenq.courtbooking.payment.dto.PaymentCheckoutRequest;
import com.shenq.courtbooking.payment.dto.PaymentCheckoutResponse;
import com.shenq.courtbooking.payment.service.PaymentService;

import jakarta.validation.Valid;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.shenq.courtbooking.payment.dto.PaymentStatusResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {
    private final PaymentService paymentService;

    public PaymentController(
        PaymentService paymentService
    ){
        this.paymentService = paymentService;
    }

    @PostMapping("/checkout")
    public PaymentCheckoutResponse createCheckout(
        @AuthenticationPrincipal Jwt jwt,
        @Valid
        @RequestBody
        PaymentCheckoutRequest request
    ){
        Long userId = Long.valueOf(jwt.getSubject());

        return paymentService.createCheckout(
            userId,
            request
        );
    }

    @GetMapping("/checkout-status")
    public PaymentStatusResponse getCheckoutStatus(
        @AuthenticationPrincipal Jwt jwt,
        @RequestParam String sessionId
    ){
        Long userId = Long.valueOf(jwt.getSubject());

        return paymentService.getCheckoutStatus(
            userId,
            sessionId
        );
    }
}
