package com.interviewgpt.backend.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import java.util.*;

@Service
public class OpenAIService {

    @Value("${openai.api.key}")
    private String apiKey;

    @Value("${openai.api.url}")
    private String apiUrl;

    private final WebClient webClient = WebClient.builder().build();

    public String generateFeedback(String answerText) {
        Map<String, Object> message = Map.of(
                "role", "user",
                "content", "Please evaluate this interview answer and suggest improvements: " + answerText
        );

        Map<String, Object> body = Map.of(
                "model", "gpt-3.5-turbo",
                "messages", List.of(message),
                "max_tokens", 150,
                "temperature", 0.7
        );

        return webClient.post()
                .uri(apiUrl)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .retrieve()
                .bodyToMono(Map.class)
                .map(resp -> {
                    List<Map<String, Object>> choices = (List<Map<String, Object>>) resp.get("choices");
                    Map<String, Object> msg = (Map<String, Object>) choices.get(0).get("message");
                    return (String) msg.get("content");
                })
                .onErrorReturn("Sorry, unable to evaluate the answer right now.")
                .block();
    }
}