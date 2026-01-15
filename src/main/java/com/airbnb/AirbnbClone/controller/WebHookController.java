package com.airbnb.AirbnbClone.controller;

import com.airbnb.AirbnbClone.service.BookingService;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.net.Webhook;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/webhook")
@RequiredArgsConstructor
public class WebHookController {

    private final BookingService bookingService;

    @Value("${stripe-webHook-key}")
    private String webhookSecret;

    @PostMapping("/payment")
    public ResponseEntity<Void> capturePayment(@RequestBody String payload , @RequestHeader("Stripe-Signature") String signHeader){

        try {
            Event event = Webhook.constructEvent(payload , signHeader , webhookSecret);
            bookingService.capturePayment(event);
            return ResponseEntity.noContent().build();
        } catch (SignatureVerificationException e) {
            throw new RuntimeException(e);
        }

    }
}
