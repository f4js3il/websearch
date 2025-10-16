package com.web.websearch.payload;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Tool(String type,
                   String name,
                   @JsonProperty("max_uses") Integer maxUses) {
}
