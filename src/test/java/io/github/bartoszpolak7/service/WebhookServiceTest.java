package io.github.bartoszpolak7.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClient;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.jsonPath;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withNoContent;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withTooManyRequests;

class WebhookServiceTest {

    private static final String URL = "https://discord.test/api/webhooks/1/secret";
    private MockRestServiceServer server;
    private WebhookService service;

    @BeforeEach
    void setUp() {
        RestClient.Builder builder = RestClient.builder();
        server = MockRestServiceServer.bindTo(builder).build(); // bind BEFORE the service builds the client
        service = new WebhookService(builder, URL);
    }

    @Test
    void postsContentToWebhook() {
        server.expect(requestTo(URL))
                .andExpect(method(HttpMethod.POST))
                .andExpect(jsonPath("$.content").value("Hello"))
                .andRespond(withNoContent());

        service.sendMessage("Hello");
        server.verify();
    }

    @Test
    void throwsOnRateLimit() {
        server.expect(requestTo(URL)).andRespond(withTooManyRequests());
        assertThatThrownBy(() -> service.sendMessage("Hello"))
                .isInstanceOf(HttpClientErrorException.class);
    }

    @Test
    void throwsOnServerError() {
        server.expect(requestTo(URL)).andRespond(withServerError());
        assertThatThrownBy(() -> service.sendMessage("Hello"))
                .isInstanceOf(HttpServerErrorException.class);
    }

    @Test
    void throwsWhenNotConfigured() {
        WebhookService unconfigured = new WebhookService(RestClient.builder(), "");
        assertThatThrownBy(() -> unconfigured.sendMessage("Hello"))
                .isInstanceOf(WebhookNotConfiguredException.class);
    }
}
