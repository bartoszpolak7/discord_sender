package io.github.bartoszpolak7.routing;

import io.github.bartoszpolak7.model.DiscordMessage;
import io.github.bartoszpolak7.service.BotService;
import io.github.bartoszpolak7.service.WebhookNotConfiguredException;
import io.github.bartoszpolak7.service.WebhookService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(ApiController.class)
class ApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private WebhookService webhookService;

    @MockitoBean
    private BotService botService;

    private static final String BODY = """
            {"message":"Hello Discord"}
            """;

    private ResultActions postMessage(String body) throws Exception {
        return mockMvc.perform(post("/webhook/send")
                .contentType("application/json")
                .content(body));
    }

    @Test
    void returns204OnSuccess() throws Exception {
        postMessage(BODY).andExpect(status().isNoContent());
        verify(webhookService).sendMessage("Hello Discord");
    }

    @Test
    void returns400WhenBlank() throws Exception {
        postMessage("""
                {"message":""}
                """).andExpect(status().isBadRequest());
        verifyNoInteractions(webhookService);
    }

    @Test
    void returns400WhenMalformedJson() throws Exception {
        postMessage("{not json").andExpect(status().isBadRequest());
    }

    @Test
    void returns503WhenNotConfigured() throws Exception {
        doThrow(new WebhookNotConfiguredException("x"))
                .when(webhookService).sendMessage(any());
        postMessage(BODY).andExpect(status().isServiceUnavailable());
    }

    @Test
    void returns504WhenUnreachable() throws Exception {
        doThrow(new ResourceAccessException("timeout"))
                .when(webhookService).sendMessage(any());
        postMessage(BODY).andExpect(status().isGatewayTimeout());
    }

    @Test
    void returns502WhenDiscordFails() throws Exception {
        doThrow(new RestClientException("boom"))
                .when(webhookService).sendMessage(any());
        postMessage(BODY).andExpect(status().isBadGateway());
    }

    @Test
    void reportsWebhookIsConfigured() throws Exception {
        given(webhookService.isConfigured()).willReturn(true);

        mockMvc.perform(get("/webhook/config"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.configured").value(false));
    }

    @Test
    void reportsWhenWebhookIsNotConfigured() throws Exception {
        given(webhookService.isConfigured()).willReturn(false);

        mockMvc.perform(get("/webhook/config"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.configured").value(false));
    }

    @Test
    void returnsBotMessageHistory() throws Exception {
        given(botService.getMessages()).willReturn(List.of(
                new DiscordMessage("Ada", "Hello", "general")));

        mockMvc.perform(get("/bot/messages"))
                .andExpect(status().isOk())
                .andExpect(content().json("""
                        [{"author":"Ada","content":"Hello","channel":"general"}]
                        """));
    }

}
