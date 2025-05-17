package com.interviewgpt.backend.service;

import com.interviewgpt.backend.model.InterviewSession;
import com.interviewgpt.backend.model.User;
import com.interviewgpt.backend.repository.InterviewSessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InterviewSessionService {

    @Autowired
    private InterviewSessionRepository sessionRepo;

    public InterviewSession createSession(User user, String title) {
        InterviewSession session = InterviewSession.builder()
                .user(user)
                .sessionTitle(title)
                .status("in_progress")
                .build();
        return sessionRepo.save(session);
    }

    public List<InterviewSession> getUserSessions(User user) {
        return sessionRepo.findByUser(user);
    }
}