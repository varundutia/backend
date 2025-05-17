package com.interviewgpt.backend.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "ai_feedback")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AIFeedback {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne
    @JoinColumn(name = "answer_id", nullable = false)
    private UserAnswer answer;

    private String feedbackText;

    private String followUpQuestion;

    private int rating;

    private LocalDateTime createdAt = LocalDateTime.now();
}