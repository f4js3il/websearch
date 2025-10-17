package com.web.websearch.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.ai.openai.api.OpenAiApi.ChatCompletionRequest.WebSearchOptions;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.List;
import java.util.Map;

@Service
public class OpenAIWebSearchService {
    private final static String API_KEY = System.getenv("OPENAI_API_KEY");

    @Value("classpath:/templates/system-prompt.st")
    private Resource systemTemplate;

    @Value("classpath:/templates/user-prompt.st")
    private Resource userTemplate;

    private final OpenAiChatOptions universalOpenAiChatOptions = OpenAiChatOptions
            .builder()
            .model("gpt-4o-mini-search-preview")
            .build();
    private final OpenAiChatModel openAiChatModel = OpenAiChatModel
            .builder()
            .openAiApi(OpenAiApi.builder().apiKey(API_KEY).build())
            .defaultOptions(universalOpenAiChatOptions)
            .build();
    private final ChatClient chatClient= ChatClient.builder(openAiChatModel).build();

    public Mono<String> openAISearch(String query) {
        WebSearchOptions webSearchOptions = new WebSearchOptions(OpenAiApi.ChatCompletionRequest.WebSearchOptions.SearchContextSize.LOW, null);
        OpenAiChatOptions openAiChatOptions = OpenAiChatOptions.builder().webSearchOptions(webSearchOptions).build();
        PromptTemplate userPromptTemplate = new PromptTemplate(userTemplate);
        String userMessageString = userPromptTemplate.getTemplate() + " " + query;
        Message userMessage = UserMessage.builder().text(userMessageString).build();
        PromptTemplate systemPromptTemplate = new PromptTemplate(systemTemplate);
        Message systemMessage = systemPromptTemplate.createMessage(Map.of());
        return Mono.fromCallable(() ->
                chatClient.prompt(new Prompt(List.of(userMessage, systemMessage)))
                        .options(openAiChatOptions)
                        .call()
                        .content()
        ).subscribeOn(Schedulers.boundedElastic());
    }
}
