package io.github.bartoszpolak7.routing;

import io.github.bartoszpolak7.data.Database;
import io.github.bartoszpolak7.model.DiscordMessage;
import io.github.bartoszpolak7.model.PythonChatResponse;
import io.github.bartoszpolak7.service.AIService;
import io.github.bartoszpolak7.service.BotService;
import io.github.bartoszpolak7.service.WebhookService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ApiController.class)
class ApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private WebhookService webhookService;

    @MockitoBean
    private BotService botService;

    @MockitoBean
    private AIService aiService;

    @MockitoBean
    private Database database;

    @Test
    void sendsWebhookMessage() throws Exception {
        mockMvc.perform(post("/webhook/send")
                        .contentType("application/json")
                        .content("""
                                {"message":"Hello Discord"}
                                """))
                .andExpect(status().isOk())
                .andExpect(content().string("Message sent to Discord"));

        verify(webhookService).sendMessage("Hello Discord");
    }

    @Test
    void reportsWebhookConfigurationWithoutExposingItsUrl() throws Exception {
        given(webhookService.isConfigured()).willReturn(true);

        mockMvc.perform(get("/webhook/config"))
                .andExpect(status().isOk())
                .andExpect(content().string("Discord webhook is configured"));
    }

    @Test
    void returnsBotMessageHistory() throws Exception {
        given(botService.getMessages()).willReturn(List.of(
                new DiscordMessage("Ada", "Hello", "general")
        ));

        mockMvc.perform(get("/bot/messages"))
                .andExpect(status().isOk())
                .andExpect(content().json("""
                        [{"author":"Ada","content":"Hello","channel":"general"}]
                        """));
    }

    @Test
    void forwardsChatRequestAndReturnsPythonResponse() throws Exception {
        given(database.getProducts()).willReturn(List.of());
        given(database.getCategories()).willReturn(List.of());
        given(aiService.chat("Hello", true, List.of(), List.of()))
                .willReturn(new PythonChatResponse("Welcome", true));

        mockMvc.perform(post("/chat")
                        .contentType("application/json")
                        .content("""
                                {"message":"Hello","newConversation":true}
                                """))
                .andExpect(status().isOk())
                .andExpect(content().json("""
                        {"response":"Welcome","is_opening":true}
                        """));

        org.mockito.Mockito.verify(aiService).chat("Hello", true, List.of(), List.of());
    }
}
