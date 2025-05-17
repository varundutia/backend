package com.interviewgpt.backend.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "payment_gateway")
    private String paymentGateway;

    @Column(name = "payment_status")
    private String paymentStatus;

    @Column(name = "transaction_reference")
    private String transactionReference;

    @Column(name = "amount")
    private BigDecimal amount;

    @Column(name = "session_count")
    private int sessionCount;

    @Column(name = "expiry_date")
    private LocalDateTime expiryDate;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
}