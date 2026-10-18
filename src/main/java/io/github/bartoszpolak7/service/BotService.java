package io.github.bartoszpolak7.service;

import io.github.bartoszpolak7.data.Database;
import io.github.bartoszpolak7.model.DiscordMessage;
import io.github.bartoszpolak7.model.PythonChatResponse;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
public class BotService extends ListenerAdapter {

    private static final Logger log = LoggerFactory.getLogger(BotService.class);

    private final Database database;
    private final AIService aiService;
    private final String botToken;
    private final CopyOnWriteArrayList<DiscordMessage> messages = new CopyOnWriteArrayList<>();
    private JDA jda;

    public BotService(
            Database database,
            AIService aiService,
            @Value("${discord.bot-token:}") String botToken
    ) {
        this.database = database;
        this.aiService = aiService;
        this.botToken = botToken;
    }

    @PostConstruct
    public void start() {
        if (botToken.isBlank()) {
            throw new IllegalStateException("DISCORD_BOT_TOKEN is not configured");
        }
        jda = JDABuilder.createDefault(botToken)
                .enableIntents(GatewayIntent.MESSAGE_CONTENT, GatewayIntent.GUILD_MESSAGES)
                .addEventListeners(this)
                .build();
    }

    @PreDestroy
    public void stop() {
        if (jda != null) {
            jda.shutdown();
        }
    }

    public List<DiscordMessage> getMessages() {
        return List.copyOf(messages);
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        String content = event.getMessage().getContentDisplay();
        String categories = database.getCategories().stream()
                .map(category -> category.name())
                .reduce((first, second) -> first + "\n" + second)
                .orElse("");

        if (content.startsWith("!kategorie")) {
            event.getChannel().sendMessage(categories).queue();
        } else if (content.startsWith("!kategoria")) {
            String[] args = content.split(" ");
            if (args.length < 2) {
                event.getChannel().sendMessage("Użytkowanie: !kategoria <nazwa>").queue();
                return;
            }
            String categoryName = args[1];
            var category = database.getCategories().stream()
                    .filter(item -> item.name().equals(categoryName))
                    .findFirst()
                    .orElse(null);
            if (category == null) {
                event.getChannel().sendMessage("Nie znaleziono kategorii: " + categoryName).queue();
                return;
            }
            String products = database.getProducts().stream()
                    .filter(product -> product.categoryId() == category.id())
                    .map(product -> product.name() + " - " + product.price())
                    .reduce((first, second) -> first + "\n" + second)
                    .orElse("");
            event.getChannel().sendMessage(categoryName + ":\n" + products).queue();
        } else if (content.startsWith("!czat")) {
            String message = content.substring("!czat".length()).trim();
            if (message.isEmpty()) {
                event.getChannel().sendMessage("Użytkowanie: !czat <wiadomość>").queue();
                return;
            }
            List<String> products = database.getProducts().stream()
                    .map(product -> product.name() + " - " + product.price())
                    .toList();
            List<String> categoryNames = database.getCategories().stream()
                    .map(category -> category.name())
                    .toList();

            event.getChannel().sendTyping().queue();
            CompletableFuture.runAsync(() -> {
                try {
                    PythonChatResponse response = aiService.chat(message, false, products, categoryNames);
                    event.getChannel().sendMessage(response.response()).queue();
                } catch (RuntimeException exception) {
                    log.error("Failed to process Discord chat command", exception);
                    event.getChannel().sendMessage("Błąd: " + exception.getMessage()).queue();
                }
            });
        } else if (content.startsWith("!")) {
            event.getChannel().sendMessage("Nie znaleziono komendy.").queue();
            return;
        }

        messages.add(new DiscordMessage(
                event.getAuthor().getName(),
                content,
                event.getChannel().getName()
        ));
    }
}
