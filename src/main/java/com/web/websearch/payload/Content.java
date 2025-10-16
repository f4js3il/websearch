package com.web.websearch.payload;

import java.util.Map;

public record Content(String type,
                      String text,
                      String id,
                      String name,
                      Map<String, Object> input) {
}
