package com.interviewgpt.backend.controller;

import com.interviewgpt.backend.model.InterviewSession;
import com.interviewgpt.backend.model.User;
import com.interviewgpt.backend.repository.UserRepository;
import com.interviewgpt.backend.service.InterviewSessionService;
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

    @PostMapping("/start")
    public InterviewSession startSession(@RequestBody Map<String, String> body) {
        User user = userRepo.findByEmail(body.get("email")).orElseThrow();
        return sessionService.createSession(user, body.get("title"));
    }

    @GetMapping("/user/{email}")
    public List<InterviewSession> listSessions(@PathVariable String email) {
        User user = userRepo.findByEmail(email).orElseThrow();
        return sessionService.getUserSessions(user);
    }
}