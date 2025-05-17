package com.interviewgpt.backend.repository;

import com.interviewgpt.backend.model.UserAnswer;
import com.interviewgpt.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface UserAnswerRepository extends JpaRepository<UserAnswer, UUID> {
    List<UserAnswer> findByUser(User user);
}