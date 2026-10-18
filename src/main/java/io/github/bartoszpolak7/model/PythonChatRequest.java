package io.github.bartoszpolak7.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record PythonChatRequest(
        String message,
        @JsonProperty("new_conversation") boolean newConversation,
        List<String> products,
        List<String> categories
) {
}
