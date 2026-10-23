package io.github.bartoszpolak7.service;

public class WebhookNotConfiguredException extends RuntimeException {
    public WebhookNotConfiguredException(String message) {
        super(message);
    }
}
