package com.web.websearch.controller;

import com.web.websearch.payload.AnthropicResponse;
import com.web.websearch.payload.UserResponse;
import com.web.websearch.service.WebSearchService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
public class WebSearchController {
    private final WebSearchService webSearchService;

    public WebSearchController(WebSearchService webSearchService) {
        this.webSearchService = webSearchService;
    }

    @GetMapping("/search")
    public ResponseEntity<Mono<UserResponse>> search(@RequestParam String query) {
        return ResponseEntity.ok(webSearchService.search(query));
    }

}
