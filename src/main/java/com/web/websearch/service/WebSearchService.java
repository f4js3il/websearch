package com.web.websearch.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.ai.openai.api.OpenAiApi.ChatCompletionRequest.WebSearchOptions;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;


@Service
public class WebSearchService {
    private final static String API_KEY = System.getenv("OPENAI_API_KEY");

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


    public String search(String query) {
       WebSearchOptions webSearchOptions = new WebSearchOptions(WebSearchOptions.SearchContextSize.LOW, null);
        OpenAiChatOptions openAiChatOptions = OpenAiChatOptions.builder().webSearchOptions(webSearchOptions).build();
        PromptTemplate userPromptTemplate = new PromptTemplate("Give me description of operations of firm {firm}");
        Message userMessage = userPromptTemplate.createMessage(Map.of("firm", query));
        PromptTemplate systemPromptTemplate = new PromptTemplate("You are a workers compensation premium audit doing audits of business firms");
        Message systemMessage = systemPromptTemplate.createMessage(Map.of());
        return chatClient.prompt(new Prompt(List.of(userMessage,systemMessage)))
                .options(openAiChatOptions).call().content();
    }

    public String getBestRestaurants(String cuisine, String city, String state) {
        WebSearchOptions webSearchOptions = new WebSearchOptions(WebSearchOptions.SearchContextSize.LOW, new WebSearchOptions.UserLocation("approximate", new WebSearchOptions.UserLocation.Approximate(city,"US",state,"America/Chicago")));
        OpenAiChatOptions openAiChatOptions = OpenAiChatOptions.builder().webSearchOptions(webSearchOptions).build();
        return chatClient.prompt(new Prompt("Provide me best restaurants for " + cuisine ))
                .options(openAiChatOptions).call().content();
    }
}
