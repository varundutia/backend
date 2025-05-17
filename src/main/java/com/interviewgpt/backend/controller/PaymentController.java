package com.interviewgpt.backend.controller;

import com.interviewgpt.backend.service.StripePaymentService;
import com.interviewgpt.backend.model.User;
import com.interviewgpt.backend.repository.UserRepository;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    @Autowired
    private StripePaymentService paymentService;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/checkout")
    public ResponseEntity<?> createCheckoutSession(Authentication authentication) {
        try {
            String email = authentication.getName();
            Optional<User> optionalUser = userRepository.findByEmail(email);
            if (optionalUser.isEmpty()) {
                return ResponseEntity.status(404).body("User not found");
            }
            User user = optionalUser.get();
            String url = paymentService.createCheckoutSession(user);
            return ResponseEntity.ok().body(new CheckoutResponse(url));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Failed to create Stripe Checkout session");
        }
    }

    // Simple DTO for returning the checkout URL
    record CheckoutResponse(String url) {}
}