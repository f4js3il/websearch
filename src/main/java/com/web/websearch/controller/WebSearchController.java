package com.web.websearch.controller;

import com.web.websearch.service.WebSearchService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WebSearchController {
    private final WebSearchService webSearchService;

    public WebSearchController(WebSearchService webSearchService) {
        this.webSearchService = webSearchService;
    }

    @GetMapping("/search")
    public ResponseEntity<String> search(@RequestParam String query) {
        return ResponseEntity.ok(webSearchService.search(query));
    }

    @GetMapping("/best/restaurants")
    public ResponseEntity<String> bestRestaurants(@RequestParam String cuisine, @RequestParam String  city, @RequestParam  String state ) {
        return ResponseEntity.ok(webSearchService.getBestRestaurants(cuisine,city,state));
    }
}
