package com.interviewgpt.backend.repository;

import com.interviewgpt.backend.model.InterviewQuestion;
import com.interviewgpt.backend.model.InterviewSession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface InterviewQuestionRepository extends JpaRepository<InterviewQuestion, UUID> {
    List<InterviewQuestion> findBySession(InterviewSession session);
}