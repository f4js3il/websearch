package com.web.websearch.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.anthropic.api.AnthropicApi;
import org.springframework.ai.anthropic.AnthropicChatModel;
import org.springframework.ai.anthropic.AnthropicChatOptions;
import org.springframework.ai.anthropic.api.AnthropicApi.ChatCompletionRequest.ThinkingConfig;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;


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


    public String search(String query) {
        PromptTemplate userPromptTemplate = new PromptTemplate("Give me description of operations of firm {firm}");
        Message userMessage = userPromptTemplate.createMessage(Map.of("firm", query));
        PromptTemplate systemPromptTemplate = new PromptTemplate("You are a workers compensation premium audit doing audits of business firms");
        Message systemMessage = systemPromptTemplate.createMessage(Map.of());
        return chatClient.prompt(new Prompt(List.of(userMessage,systemMessage)))
                .call().content();
    }

    public String getBestRestaurants(String cuisine, String city, String state) {
        ThinkingConfig webSearchOptions = new ThinkingConfig(AnthropicApi.ThinkingType.ENABLED, null);
        AnthropicChatOptions openAiChatOptions = AnthropicChatOptions.builder().thinking(webSearchOptions).build();
        return chatClient.prompt(new Prompt("Provide me best restaurants for " + cuisine ))
                .options(openAiChatOptions).call().content();
    }
}
