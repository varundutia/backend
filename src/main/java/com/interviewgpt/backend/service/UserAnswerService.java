package com.interviewgpt.backend.service;

import com.interviewgpt.backend.model.*;
import com.interviewgpt.backend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.data.redis.core.RedisTemplate;
import java.time.Duration;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.DeserializationFeature;

@Service
public class UserAnswerService {

    @Autowired
    private UserAnswerRepository answerRepo;

    @Autowired
    private AIFeedbackRepository feedbackRepo;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

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
        String key = "feedback::" + answer.getAnswerText().hashCode();
        Object cachedRaw = redisTemplate.opsForValue().get(key);
        if (cachedRaw != null) {
            try {
                ObjectMapper mapper = new ObjectMapper();
                mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                mapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());
                AIFeedback cached = mapper.convertValue(cachedRaw, AIFeedback.class);
                System.out.println("✅ Returning cached AI feedback for: " + key);
                return cached;
            } catch (IllegalArgumentException e) {
                System.err.println("⚠️ Redis cache conversion failed for key: " + key + " - " + e.getMessage());
            }
        }

        String response = openAIService.generateFeedback(answer.getAnswerText());
        AIFeedback fresh = AIFeedback.builder()
            .answer(answer)
            .feedbackText(response)
            .followUpQuestion("What would you do differently next time?")
            .rating(8)
            .build();
        feedbackRepo.save(fresh);
        redisTemplate.opsForValue().set(key, fresh, Duration.ofMinutes(30));
        return fresh;

        }
}