package io.github.bartoszpolak7.routing;

import io.github.bartoszpolak7.model.DiscordMessage;
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
                .andExpect(content().string("Discord webhook jest skonfigurowany"));
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

}
