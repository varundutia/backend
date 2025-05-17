package com.interviewgpt.backend.repository;

import com.interviewgpt.backend.model.AIFeedback;
import com.interviewgpt.backend.model.UserAnswer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface AIFeedbackRepository extends JpaRepository<AIFeedback, UUID> {
    Optional<AIFeedback> findByAnswer(UserAnswer answer);
}