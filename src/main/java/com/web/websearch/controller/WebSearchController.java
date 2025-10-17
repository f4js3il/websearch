package com.web.websearch.controller;

import com.web.websearch.payload.UserResponse;
import com.web.websearch.service.ClaudeWebSearchService;
import com.web.websearch.service.OpenAIWebSearchService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
public class WebSearchController {
    private final ClaudeWebSearchService claudeWebSearchService;
    private final OpenAIWebSearchService openAIWebSearchService;

    public WebSearchController(ClaudeWebSearchService claudeWebSearchService, OpenAIWebSearchService openAIWebSearchService) {
        this.claudeWebSearchService = claudeWebSearchService;
        this.openAIWebSearchService = openAIWebSearchService;
    }

    @GetMapping("/claude/search")
    public Mono<UserResponse> claudeSearch(@RequestParam String query) {
        return claudeWebSearchService.search(query);
    }

    @GetMapping("/openai/search")
    public Mono<String> openAISearch(@RequestParam String query) {
        return openAIWebSearchService.openAISearch(query);
    }

}
