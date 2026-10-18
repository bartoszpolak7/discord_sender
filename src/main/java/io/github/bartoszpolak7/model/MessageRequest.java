package io.github.bartoszpolak7.model;

import jakarta.validation.constraints.NotBlank;

public record MessageRequest(@NotBlank String message) {
}
