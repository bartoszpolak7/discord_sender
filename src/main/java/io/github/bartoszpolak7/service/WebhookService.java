package io.github.bartoszpolak7.service;

import io.github.bartoszpolak7.model.WebhookMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class WebhookService {

    private final RestClient client;
    private final String webhookUrl;

    public WebhookService(
            RestClient.Builder restClientBuilder,
            @Value("${discord.webhook-url:}") String webhookUrl) {
        this.client = restClientBuilder.build();
        this.webhookUrl = webhookUrl;
    }

    public void sendMessage(String content) {
        if (webhookUrl.isBlank()) {
            throw new WebhookNotConfiguredException("discord.webhook-url is not configured");
        }
        client.post()
                .uri(webhookUrl)
                .body(new WebhookMessage(content))
                .retrieve()
                .toBodilessEntity();
    }

    public boolean isConfigured() {
        return !webhookUrl.isBlank();
    }
}
