package com.interviewgpt.backend.controller;

import com.interviewgpt.backend.model.InterviewSession;
import com.interviewgpt.backend.model.User;
import com.interviewgpt.backend.model.UserQuota;
import com.interviewgpt.backend.repository.UserRepository;
import com.interviewgpt.backend.repository.UserQuotaRepository;
import com.interviewgpt.backend.service.InterviewSessionService;
import java.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/sessions")
public class InterviewSessionController {

    @Autowired
    private InterviewSessionService sessionService;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private UserQuotaRepository userQuotaRepo;

    @PostMapping("/start")
    public InterviewSession startSession(@RequestBody Map<String, String> body, org.springframework.security.core.Authentication authentication) {
        String email = authentication.getName();
        User user = userRepo.findByEmail(email).orElseThrow();
        UserQuota quota = userQuotaRepo.findByUser(user).orElseThrow(() -> new RuntimeException("Quota not found"));
        if (quota.getRemainingSessions() <= 0) {
            throw new RuntimeException("No remaining interview sessions");
        }
        quota.setRemainingSessions(quota.getRemainingSessions() - 1);
        quota.setUpdatedAt(LocalDateTime.now());
        userQuotaRepo.save(quota);
        return sessionService.createSession(user, body.get("title"));
    }

    @GetMapping("/my-sessions")
    public List<InterviewSession> listSessions(org.springframework.security.core.Authentication authentication) {
        String email = authentication.getName();
        User user = userRepo.findByEmail(email).orElseThrow();
        return sessionService.getUserSessions(user);
    }
}