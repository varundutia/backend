package com.interviewgpt.backend.controller;

import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import com.stripe.model.Event;
import com.stripe.model.EventDataObjectDeserializer;
import com.interviewgpt.backend.repository.UserRepository;
import com.interviewgpt.backend.model.User;
import com.interviewgpt.backend.model.UserQuota;
import com.interviewgpt.backend.repository.UserQuotaRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Map;

@RestController
@RequestMapping("/api/payments")
public class PaymentWebhookController {

    private final UserRepository userRepository;
    private final UserQuotaRepository userQuotaRepository;

    @Value("${stripe.webhook.secret}")
    private String endpointSecret;

    public PaymentWebhookController(UserRepository userRepository, UserQuotaRepository userQuotaRepository) {
        this.userRepository = userRepository;
        this.userQuotaRepository = userQuotaRepository;
    }

    @PostMapping("/webhook")
    @Transactional
    public ResponseEntity<String> handleWebhook(@RequestBody String payload,
                                                @RequestHeader("Stripe-Signature") String sigHeader) {
        try {
            Event event = Webhook.constructEvent(payload, sigHeader, endpointSecret);

            if ("checkout.session.completed".equals(event.getType())) {
                System.out.println("🔔 Stripe event received: checkout.session.completed");
                EventDataObjectDeserializer deserializer = event.getDataObjectDeserializer();
                if (deserializer.getObject().isPresent()) {
                    Session session = (Session) deserializer.getObject().get();
                    String email = session.getCustomerEmail();
                    System.out.println("📧 Stripe session email: " + email);
                    if (email == null) {
                        System.out.println("❌ Email is null in Stripe session");
                        return ResponseEntity.ok("Email missing in session");
                    }
                    Optional<User> userOpt = userRepository.findByEmail(email);
                    userOpt.ifPresent(user -> {
                        System.out.println("🧪 User ID: " + user.getId());
                        System.out.println("🔍 Looking for existing UserQuota record...");
                        userQuotaRepository.findByUserId(user.getId()).ifPresentOrElse(
                            quota -> {
                                quota.setRemainingSessions(quota.getRemainingSessions() + 1);
                                quota.setUpdatedAt(LocalDateTime.now());
                                userQuotaRepository.save(quota);
                                System.out.println("✅ Quota updated via user_quota table for: " + user.getEmail());
                            },
                            () -> {
                                System.out.println("🆕 Creating new UserQuota for: " + user.getEmail());
                                UserQuota newQuota = new UserQuota();
                                newQuota.setUser(user);
                                newQuota.setRemainingSessions(1);
                                newQuota.setUpdatedAt(LocalDateTime.now());
                                userQuotaRepository.save(newQuota);
                                System.out.println("➕ Created new quota record for: " + user.getEmail());
                            }
                        );
                    });
                } else {
                    System.out.println("❌ Stripe deserialization failed. Could not convert object to Session.");
                    Object rawObject = event.getData().getObject();
                    if (rawObject instanceof Session) {
                        Session session = (Session) rawObject;
                        String email = session.getCustomerEmail();
                        System.out.println("📧 Fallback fetch: Stripe session email = " + email);
                        if (email == null) {
                            System.out.println("❌ Email is null in Stripe session");
                            return ResponseEntity.ok("Email missing in session");
                        }
                        Optional<User> userOpt = userRepository.findByEmail(email);
                        userOpt.ifPresent(user -> {
                            System.out.println("🧪 User ID: " + user.getId());
                            System.out.println("🔍 Looking for existing UserQuota record...");
                            userQuotaRepository.findByUserId(user.getId()).ifPresentOrElse(
                                quota -> {
                                    quota.setRemainingSessions(quota.getRemainingSessions() + 1);
                                    quota.setUpdatedAt(LocalDateTime.now());
                                    userQuotaRepository.save(quota);
                                    System.out.println("✅ Quota updated via user_quota table for: " + user.getEmail());
                                },
                                () -> {
                                    System.out.println("🆕 Creating new UserQuota for: " + user.getEmail());
                                    UserQuota newQuota = new UserQuota();
                                    newQuota.setUser(user);
                                    newQuota.setRemainingSessions(1);
                                    newQuota.setUpdatedAt(LocalDateTime.now());
                                    userQuotaRepository.save(newQuota);
                                    System.out.println("➕ Created new quota record for: " + user.getEmail());
                                }
                            );
                        });
                    } else {
                        System.out.println("❌ Fallback cast failed: object is not a Session");
                    }
                }
            }
            return ResponseEntity.ok("Webhook received");
        } catch (SignatureVerificationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Webhook signature invalid");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Webhook processing failed");
        }
    }
}