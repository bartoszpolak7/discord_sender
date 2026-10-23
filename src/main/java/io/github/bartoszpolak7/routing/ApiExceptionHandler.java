package io.github.bartoszpolak7.routing;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;

import io.github.bartoszpolak7.service.WebhookNotConfiguredException;

@RestControllerAdvice // Spring looks for exception handlers first in the class
                      // then in classes with this annotation
public class ApiExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(ApiExceptionHandler.class);

    @ExceptionHandler(WebhookNotConfiguredException.class)
    public ResponseEntity<String> handleNotConfigured(WebhookNotConfiguredException ex) {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body("Discord webhook is not configured");
    }

    @ExceptionHandler(ResourceAccessException.class)
    public ResponseEntity<String> handleUnreachable(ResourceAccessException ex) {
        log.warn("Discord unreachable: {}", ex.getClass().getSimpleName());
        return ResponseEntity.status(HttpStatus.GATEWAY_TIMEOUT)
                .body("Discord is unreachable");
    }

    @ExceptionHandler(RestClientException.class)
    public ResponseEntity<String> handleDiscordFailure(RestClientException ex) {
        log.warn("Discord call failed: {}", ex.getClass().getSimpleName());
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                .body("Failed to send message to Discord");
    }
}