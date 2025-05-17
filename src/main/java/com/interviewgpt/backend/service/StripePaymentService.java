package com.interviewgpt.backend.service;

import com.stripe.Stripe;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import com.interviewgpt.backend.model.Payment;
import com.interviewgpt.backend.model.User;
import com.interviewgpt.backend.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class StripePaymentService {

    @Value("${stripe.secret.key}")
    private String stripeSecretKey;

    @Autowired
    private PaymentRepository paymentRepository;

    public String createCheckoutSession(User user) throws Exception {
        Stripe.apiKey = stripeSecretKey;

        SessionCreateParams params = SessionCreateParams.builder()
                .setCustomerEmail(user.getEmail())
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl("http://localhost:3000/success")
                .setCancelUrl("http://localhost:3000/cancel")
                .addAllLineItem(List.of(
                        SessionCreateParams.LineItem.builder()
                                .setQuantity(1L)
                                .setPriceData(
                                        SessionCreateParams.LineItem.PriceData.builder()
                                                .setCurrency("usd")
                                                .setUnitAmount(1000L)
                                                .setProductData(
                                                        SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                                .setName("Mock Interview Session")
                                                                .build()
                                                ).build()
                                )
                                .build()
                ))
                .build();

        Session session = Session.create(params);

        // Save payment record
        Payment payment = new Payment();
        payment.setUser(user);
        payment.setAmount(BigDecimal.valueOf(10.00));
        payment.setPaymentGateway("stripe");
        payment.setPaymentStatus("PENDING");
        payment.setTransactionReference(session.getId());
        payment.setSessionCount(3);
        payment.setExpiryDate(java.time.LocalDateTime.now().plusMonths(1));
        paymentRepository.save(payment);

        return session.getUrl();
    }
}