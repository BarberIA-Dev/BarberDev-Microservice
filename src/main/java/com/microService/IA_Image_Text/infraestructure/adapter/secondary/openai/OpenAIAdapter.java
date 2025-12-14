package com.microService.IA_Image_Text.infraestructure.adapter.secondary.openai;

import com.microService.IA_Image_Text.application.port.out.IaRecommendationPort;
import com.microService.IA_Image_Text.domain.exception.ExternalServiceException;
import com.microService.IA_Image_Text.domain.model.IaResponse;
import com.microService.IA_Image_Text.infraestructure.adapter.secondary.dto.AiResponseDto;
import com.microService.IA_Image_Text.infraestructure.adapter.secondary.openai.mapper.OpenAIResponseMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.List;
import java.util.Map;

@Slf4j
@Service("openAIAdapter")
public class OpenAIAdapter implements IaRecommendationPort {

    private final WebClient webClient;
    private final OpenAIResponseMapper mapper;
    private final String apiKey;

    public OpenAIAdapter(
            @Qualifier("openAIWebClient") WebClient webClient,
            OpenAIResponseMapper mapper,
            @Value("${openai.api.key}") String apiKey
    ) {
        this.webClient = webClient;
        this.mapper = mapper;
        this.apiKey = apiKey;
    }

    @Override
    public IaResponse getRecommendation(String imageUrl) {
        log.info("Solicitando recomendación a OpenAI para imagen: {}", imageUrl);

        Map<String, String> textContent = Map.of(
                "type", "text",
                "text", "Analiza la imagen del rostro. Recomienda un corte de pelo basado en la forma de la cara y la textura. El resultado debe ser un JSON estricto con los campos: recommendedCut (string), confidenceScore (float entre 0 y 1), y analysisDetails (string)."
        );

        Map<String, Object> imageUrlMap = Map.of("url", imageUrl);
        Map<String, Object> imageContent = Map.of(
                "type", "image_url",
                "image_url", imageUrlMap
        );

        Map<String, Object> userMessage = Map.of(
                "role", "user",
                "content", List.of(textContent, imageContent)
        );

        Map<String, Object> requestBody = Map.of(
                "model", "gpt-4o",
                "messages", List.of(userMessage)
        );

        try {
            AiResponseDto responseDto = webClient.post()
                    .uri("/chat/completions")
                    .header("Authorization", "Bearer " + apiKey)
                    .bodyValue(requestBody)
                    .retrieve()
                    .onStatus(status -> status.isError(), response -> {
                        return response.createException()
                                .flatMap(e -> {
                                    log.error("OpenAI API Falló: Código {} - {}", e.getStatusCode(), e.getResponseBodyAsString());
                                    return reactor.core.publisher.Mono.error(e);
                                });
                    })
                    .bodyToMono(AiResponseDto.class)
                    .block();

            if (responseDto != null) {
                log.info("Respuesta recibida de OpenAI exitosamente");
                return mapper.toDomain(responseDto);
            }

        } catch (WebClientResponseException e) {
            log.error("Error HTTP de OpenAI: {}", e.getStatusCode(), e);
            throw new ExternalServiceException(
                    "Fallo en el servicio de análisis de IA. Error HTTP: " + e.getStatusCode(), e
            );
        } catch (Exception e) {
            log.error("Error de conexión/timeout con OpenAI: {}", e.getMessage(), e);
            throw new ExternalServiceException("Fallo en el servicio de análisis de IA", e);
        }

        throw new ExternalServiceException("Análisis fallido o sin respuesta de la IA");
    }
}

