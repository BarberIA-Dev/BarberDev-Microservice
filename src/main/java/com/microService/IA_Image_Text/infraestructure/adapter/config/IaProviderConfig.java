package com.microService.IA_Image_Text.infraestructure.adapter.config;

import com.microService.IA_Image_Text.application.port.out.IaRecommendationPort;
import com.microService.IA_Image_Text.infraestructure.adapter.secondary.gemini.GeminiAdapter;
import com.microService.IA_Image_Text.infraestructure.adapter.secondary.openai.OpenAIAdapter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Slf4j
@Configuration
public class IaProviderConfig {

    @Bean
    @Primary
    public IaRecommendationPort iaRecommendationPort(
            @Value("${ia.provider:gemini}") String provider,
            @Qualifier("openAIAdapter") OpenAIAdapter openAIAdapter,
            @Qualifier("geminiAdapter") GeminiAdapter geminiAdapter
    ) {
        log.info("Configurando proveedor de IA: {}", provider);
        return switch (provider.toLowerCase()) {
            case "openai" -> {
                log.info("Usando OpenAI como proveedor de IA");
                yield openAIAdapter;
            }
            case "gemini" -> {
                log.info("Usando Gemini como proveedor de IA");
                yield geminiAdapter;
            }
            default -> {
                log.warn("Proveedor '{}' no reconocido, usando Gemini por defecto", provider);
                yield geminiAdapter;
            }
        };
    }
}

