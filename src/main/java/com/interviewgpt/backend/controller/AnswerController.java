package com.interviewgpt.backend.controller;

import com.interviewgpt.backend.model.*;
import com.interviewgpt.backend.repository.*;
import com.interviewgpt.backend.service.UserAnswerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/answers")
public class AnswerController {

    @Autowired private UserRepository userRepo;
    @Autowired private InterviewQuestionRepository questionRepo;
    @Autowired private UserAnswerService answerService;

    @PostMapping("/submit")
    public AIFeedback submitAnswer(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        UUID questionId = UUID.fromString(request.get("questionId"));
        String answerText = request.get("answerText");

        User user = userRepo.findByEmail(email).orElseThrow();
        InterviewQuestion question = questionRepo.findById(questionId).orElseThrow();

        UserAnswer answer = answerService.saveAnswer(user, question, answerText);
        return answerService.generateMockFeedback(answer);
    }
}