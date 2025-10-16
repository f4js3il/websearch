package com.web.websearch.repository;

import com.web.websearch.payload.*;
import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Service
public class ClaudeRepository {

    private final WebClient webClient;

    public ClaudeRepository(WebClient.Builder webClientBuilder) {
        String apiKey = System.getenv("ANTHROPIC_API_KEY");

        HttpClient httpClient = HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 30_000) // connect timeout
                // Time allowed between sending the request and receiving the first response byte
                .responseTimeout(Duration.ofSeconds(120))
                // Ensure no 30s idle read/write timeout by Netty when streaming large responses
                .doOnConnected(conn -> conn
                        .addHandlerLast(new ReadTimeoutHandler(120, TimeUnit.SECONDS))
                        .addHandlerLast(new WriteTimeoutHandler(120, TimeUnit.SECONDS))
                );

        this.webClient = webClientBuilder
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .baseUrl("https://api.anthropic.com")
                .defaultHeader("x-api-key", apiKey)
                .defaultHeader("anthropic-version", "2023-06-01")
                .defaultHeader("content-type", "application/json")
                .build();
    }

    public Mono<UserResponse> sendMessage(String userMessage, String systemMessage) {
        AnthropicRequest request = new AnthropicRequest(
                "claude-sonnet-4-5",
                1024,
                List.of(new Message("user", userMessage)),
                List.of(new Tool("web_search_20250305", "web_search", 5)),
                systemMessage
        );

        return webClient.post()
                .uri("/v1/messages")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(AnthropicResponse.class)
                .map(res -> new UserResponse(extractTextResponse(res)))
                .onErrorResume(WebClientResponseException.class, ex -> {
                    String errorMsg = "Anthropic API error: " + ex.getStatusCode() + " - " + ex.getResponseBodyAsString();
                    return Mono.just(new UserResponse("Error: " + errorMsg));
                })
                // Handle timeouts, network issues, or unexpected exceptions
                .onErrorResume(Exception.class, ex -> {
                    String errorMsg = "Unexpected error calling Anthropic: " + ex.getMessage();
                    return Mono.just(new UserResponse("Error: " + errorMsg));
                });
    }

    public String extractTextResponse(AnthropicResponse response) {
        return response.content().stream()
                .filter(content -> "text".equals(content.type()))
                .map(Content::text)
                .filter(Objects::nonNull)
                .collect(java.util.stream.Collectors.joining());
    }
}
