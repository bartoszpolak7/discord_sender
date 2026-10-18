package io.github.bartoszpolak7.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public record PythonChatResponse(
        String response,
        @JsonProperty("is_opening") boolean isOpening
) {
}
