package com.interviewgpt.backend.service;

import com.interviewgpt.backend.model.*;
import com.interviewgpt.backend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserAnswerService {

    @Autowired
    private UserAnswerRepository answerRepo;

    @Autowired
    private AIFeedbackRepository feedbackRepo;

    public UserAnswer saveAnswer(User user, InterviewQuestion question, String answerText) {
        UserAnswer answer = UserAnswer.builder()
                .user(user)
                .question(question)
                .answerText(answerText)
                .build();
        return answerRepo.save(answer);
    }

    public AIFeedback generateMockFeedback(UserAnswer answer) {
        AIFeedback feedback = AIFeedback.builder()
                .answer(answer)
                .feedbackText("Good structure, but could add more depth.")
                .followUpQuestion("Can you elaborate on your impact in that role?")
                .rating(7)
                .build();
        return feedbackRepo.save(feedback);
    }
}