package com.interviewgpt.backend.service;

import com.interviewgpt.backend.model.InterviewQuestion;
import com.interviewgpt.backend.model.InterviewSession;
import com.interviewgpt.backend.repository.InterviewQuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InterviewQuestionService {

    @Autowired
    private InterviewQuestionRepository questionRepo;

    public InterviewQuestion addQuestion(InterviewSession session, String text, String type, int order) {
        InterviewQuestion question = InterviewQuestion.builder()
                .session(session)
                .questionText(text)
                .questionType(type)
                .sequenceNo(order)
                .build();
        return questionRepo.save(question);
    }

    public List<InterviewQuestion> getQuestionsForSession(InterviewSession session) {
        return questionRepo.findBySession(session);
    }
}