package com.web.websearch.service;


import com.web.websearch.payload.UserResponse;
import com.web.websearch.repository.ClaudeRepository;

import org.springframework.beans.factory.annotation.Value;

import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.io.IOException;
import java.nio.charset.StandardCharsets;


@Service
public class ClaudeWebSearchService {
    private final static String API_KEY = System.getenv("ANTHROPIC_API_KEY");

    @Value("classpath:/templates/system-prompt.st")
    private Resource systemTemplate;

    @Value("classpath:/templates/user-prompt.st")
    private Resource userTemplate;

    private final ClaudeRepository claudeRepository;

    public ClaudeWebSearchService(ClaudeRepository claudeRepository) {
        this.claudeRepository = claudeRepository;
    }

    private Mono<String> readResource(Resource resource) {
        return Mono.fromCallable(() -> {
                    try (java.io.InputStream is = resource.getInputStream()) {
                        return new String(is.readAllBytes(), StandardCharsets.UTF_8);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                })
                .subscribeOn(Schedulers.boundedElastic());
    }

    public Mono<UserResponse> search(String query){
        // Read both system and user prompt templates without blocking the event-loop
        return Mono.zip(
                    readResource(systemTemplate),
                    readResource(userTemplate)
                )
                .flatMap(tuple -> {
                    String systemMessage = tuple.getT1();
                    String userPrompt = tuple.getT2();
                    String userMessage = userPrompt + " " + query;
                    return claudeRepository.sendMessage(userMessage, systemMessage);
                });
    }


}
