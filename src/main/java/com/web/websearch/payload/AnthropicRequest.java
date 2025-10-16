package com.web.websearch.payload;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record AnthropicRequest(String model,
                               @JsonProperty("max_tokens") Integer maxTokens,
                               List<Message> messages,
                               List<Tool> tools,
                               String system) {
}
