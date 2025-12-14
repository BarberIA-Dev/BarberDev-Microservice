package com.microService.IA_Image_Text.infraestructure.adapter.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class HttpClientConfig {

    @Bean("openAIWebClient")
    public WebClient openAIWebClient(
            @Value("${openai.api.url}") String apiUrl) {
        return WebClient.builder()
                .baseUrl(apiUrl)
                .build();
    }

    @Bean("geminiWebClient")
    public WebClient geminiWebClient(
            @Value("${gemini.api.url}") String apiUrl) {
        return WebClient.builder()
                .baseUrl(apiUrl)
                .build();
    }

    @Bean("rawWebClient")
    public WebClient rawWebClient() {
        return WebClient.builder().build();
    }
}
