package io.github.bartoszpolak7.routing;

import io.github.bartoszpolak7.data.Database;
import io.github.bartoszpolak7.model.ChatRequest;
import io.github.bartoszpolak7.model.DiscordMessage;
import io.github.bartoszpolak7.model.MessageRequest;
import io.github.bartoszpolak7.model.PythonChatResponse;
import io.github.bartoszpolak7.service.AIService;
import io.github.bartoszpolak7.service.BotService;
import io.github.bartoszpolak7.service.WebhookService;
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
    private final AIService aiService;
    private final Database database;

    public ApiController(
            WebhookService webhookService,
            BotService botService,
            AIService aiService,
            Database database
    ) {
        this.webhookService = webhookService;
        this.botService = botService;
        this.aiService = aiService;
        this.database = database;
    }

    @PostMapping(path = "/webhook/send", produces = MediaType.TEXT_PLAIN_VALUE)
    public String sendWebhookMessage(@RequestBody MessageRequest request) {
        webhookService.sendMessage(request.message());
        return "Message sent to Discord";
    }

    @GetMapping(path = "/webhook/config", produces = MediaType.TEXT_PLAIN_VALUE)
    public String getWebhookConfig() {
        return webhookService.isConfigured()
                ? "Discord webhook is configured"
                : "Discord webhook is not configured";
    }

    @GetMapping("/bot/messages")
    public List<DiscordMessage> getBotMessages() {
        return botService.getMessages();
    }

    @PostMapping("/chat")
    public PythonChatResponse chat(@RequestBody ChatRequest request) {
        List<String> products = database.getProducts().stream()
                .map(product -> product.name() + " - " + product.price())
                .toList();
        List<String> categories = database.getCategories().stream()
                .map(category -> category.name())
                .toList();
        return aiService.chat(request.message(), request.newConversation(), products, categories);
    }
}
