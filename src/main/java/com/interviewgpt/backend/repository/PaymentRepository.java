package com.interviewgpt.backend.repository;

import com.interviewgpt.backend.model.Payment;
import com.interviewgpt.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, String> {
    List<Payment> findByUser(User user);
}