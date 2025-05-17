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

    
    @Autowired private OpenAIService openAIService;
    public AIFeedback generateLiveFeedback(UserAnswer answer) {
        String response = openAIService.generateFeedback(answer.getAnswerText());
        return feedbackRepo.save(AIFeedback.builder()
            .answer(answer)
            .feedbackText(response)
            .followUpQuestion("What would you do differently next time?")
            .rating(8) // optional static rating
            .build());

        }
}