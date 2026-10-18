package io.github.bartoszpolak7.routing;

import io.github.bartoszpolak7.model.DiscordMessage;
import io.github.bartoszpolak7.model.MessageRequest;
import io.github.bartoszpolak7.service.BotService;
import io.github.bartoszpolak7.service.WebhookService;
import jakarta.validation.Valid;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ApiController {

    private final WebhookService webhookService;
    private final BotService botService;

    public ApiController(WebhookService webhookService, BotService botService) {
        this.webhookService = webhookService;
        this.botService = botService;
    }

    @PostMapping(path = "/webhook/send", produces = MediaType.TEXT_PLAIN_VALUE)
    public String sendWebhookMessage(@Valid @RequestBody MessageRequest request) {
        webhookService.sendMessage(request.message());
        return "Message sent to Discord";
    }

    @GetMapping(path = "/webhook/config", produces = MediaType.TEXT_PLAIN_VALUE)
    public String getWebhookConfig() {
        return webhookService.isConfigured()
                ? "Discord webhook jest skonfigurowany"
                : "Discord webhook NIE jest skonfigurowany";
    }

    @GetMapping("/bot/messages")
    public List<DiscordMessage> getBotMessages() {
        return botService.getMessages();
    }
}
