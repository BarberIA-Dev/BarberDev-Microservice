package com.microService.IA_Image_Text.infraestructure.adapter.config;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class HttpClientConfig {

    @Value("${ia.recommendation.url}")
    private String apiUrl;

    @Bean
    public WebClient webClient(){
        return WebClient.builder()
                .baseUrl(apiUrl)
                .build();
    }
}
