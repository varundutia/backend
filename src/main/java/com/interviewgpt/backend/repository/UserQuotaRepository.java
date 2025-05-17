package com.interviewgpt.backend.repository;

import com.interviewgpt.backend.model.User;
import com.interviewgpt.backend.model.UserQuota;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserQuotaRepository extends JpaRepository<UserQuota, UUID> {
    Optional<UserQuota> findByUserId(UUID userId);
    Optional<UserQuota> findByUser(User user);
}