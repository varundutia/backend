package com.interviewgpt.backend.repository;

import com.interviewgpt.backend.model.InterviewSession;
import com.interviewgpt.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface InterviewSessionRepository extends JpaRepository<InterviewSession, UUID> {
    List<InterviewSession> findByUser(User user);
}