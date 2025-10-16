package com.web.websearch.service;

import com.web.websearch.payload.AnthropicResponse;
import com.web.websearch.payload.Content;
import com.web.websearch.payload.UserResponse;
import com.web.websearch.repository.ClaudeRepository;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.anthropic.api.AnthropicApi;
import org.springframework.ai.anthropic.AnthropicChatModel;
import org.springframework.ai.anthropic.AnthropicChatOptions;
import org.springframework.ai.anthropic.api.AnthropicApi.ChatCompletionRequest.ThinkingConfig;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.Objects;


@Service
public class WebSearchService {
    private final static String API_KEY = System.getenv("ANTHROPIC_API_KEY");

    private final AnthropicChatOptions anthropicChatOptions = AnthropicChatOptions
                                                        .builder()
                                                        .model("claude-sonnet-4-5-20250929")
                                                        .maxTokens(500)
                                                        .build();
    private final AnthropicChatModel anthropicChatModel = AnthropicChatModel
                                                    .builder().anthropicApi(AnthropicApi.builder().apiKey(API_KEY).build())
                                                    .defaultOptions(anthropicChatOptions)
                                                    .build();
    private final ChatClient chatClient= ChatClient.builder(anthropicChatModel).build();

    private final ClaudeRepository claudeRepository;

    public WebSearchService(ClaudeRepository claudeRepository) {
        this.claudeRepository = claudeRepository;
    }

    public Mono<UserResponse> search(String query) {
        String systemMessage = """
                You are a workers compensation premium auditor performing a full 360° business and risk assessment of firms.
                
                Your goals:
                1. Accurately describe the firm's operations and industry activities based on web and business information.
                2. Identify any risk factors relevant to workers compensation (e.g., field work, machinery, driving exposure, subcontracting, hazardous materials, etc.).
                3. Flag potential discrepancies or red flags (e.g., business description mismatch, high-risk operations not declared, multiple business lines, unclear or conflicting info).
                4. Search for and summarize relevant online reviews or public reputation insights.
                5. Identify if the business operates in or lists additional states beyond the main address.
                6. Provide your response in a structured, concise, and professional format suitable for audit documentation.
                7. Do NOT include system messages, reasoning steps, or mention web searches.
                
                Output format:
                ---
                ## Description of Operations
                <clear business operations summary>
                
                ## Risk Assessment
                <exposures, risk level, and safety concerns>
                
                ## Discrepancies or Red Flags
                <potential mismatches or inconsistencies>
                
                ## Reviews & Public Insights
                <summary of reputation or customer sentiment>
                
                ## Additional States of Operation
                <list any found states or “None identified”>
                ---
                
                """;

        return claudeRepository.sendMessage("Give me description of operations of firm"+" "+query,
                systemMessage);


    }

    public String getBestRestaurants(String cuisine, String city, String state) {
        ThinkingConfig webSearchOptions = new ThinkingConfig(AnthropicApi.ThinkingType.ENABLED, null);
        AnthropicChatOptions openAiChatOptions = AnthropicChatOptions.builder().thinking(webSearchOptions).build();
        return chatClient.prompt(new Prompt("Provide me best restaurants for " + cuisine ))
                .options(openAiChatOptions).call().content();
    }

}
