package io.github.bartoszpolak7.service;

import io.github.bartoszpolak7.model.PythonChatRequest;
import io.github.bartoszpolak7.model.PythonChatResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;

@Service
public class AIService {

    private final RestClient client;

    public AIService(
            RestClient.Builder restClientBuilder,
            @Value("${app.python-service-url:http://localhost:8000}") String pythonServiceUrl
    ) {
        var requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(10_000);
        requestFactory.setReadTimeout(120_000);
        this.client = restClientBuilder
                .baseUrl(pythonServiceUrl)
                .requestFactory(requestFactory)
                .build();
    }

    public PythonChatResponse chat(
            String message,
            boolean newConversation,
            List<String> products,
            List<String> categories
    ) {
        PythonChatResponse response = client.post()
                .uri("/")
                .body(new PythonChatRequest(message, newConversation, products, categories))
                .retrieve()
                .body(PythonChatResponse.class);
        if (response == null) {
            throw new IllegalStateException("Python chat service returned an empty response");
        }
        return response;
    }
}
