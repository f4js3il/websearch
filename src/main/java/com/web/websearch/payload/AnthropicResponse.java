package com.web.websearch.payload;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record AnthropicResponse(String id,
                                String type,
                                String role,
                                List<Content> content,
                                String model,
                                @JsonProperty("stop_reason") String stopReason,
                                @JsonProperty("stop_sequence") String stopSequence,
                                Usage usage) {
}
