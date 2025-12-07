package com.microService.IA_Image_Text.infraestructure.adapter.secondary;

import com.microService.IA_Image_Text.application.port.out.IaRecommendationPort;
import com.microService.IA_Image_Text.domain.model.IaResponse;
import com.microService.IA_Image_Text.infraestructure.adapter.secondary.dto.AiResponseDto;
import com.microService.IA_Image_Text.infraestructure.adapter.secondary.mapper.IaResponseMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class OpenAIAdapter implements IaRecommendationPort {

    private final WebClient webClient;
    private final IaResponseMapper mapper;

    @Value("${ia.api.key}")
    private String apiKey;

    @Override
    public IaResponse getRecommendation(String imageUrl) {

        Map<String, String> textContent = Map.of(
                "type", "text",
                "text", "Analiza la imagen del rostro. Recomienda un corte de pelo basado en la forma de la cara y la textura. El resultado debe ser un JSON estricto con los campos: cutName (string), confidenceScore (float), y analysisDetails (string)."
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
                                    System.err.println("OpenAI API Falló: Código " + e.getStatusCode() + " - " + e.getResponseBodyAsString());
                                    return reactor.core.publisher.Mono.error(e);
                                });
                    })
                    .bodyToMono(AiResponseDto.class)
                    .block();

            if (responseDto != null) {
                return mapper.toDomain(responseDto);
            }

        } catch (WebClientResponseException e) {
            throw new RuntimeException("Fallo en el servicio de análisis de IA. Error HTTP: " + e.getStatusCode(), e);

        } catch (Exception e) {
            System.err.println("Error de Conexión/Timeout con OpenAI: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Fallo en el servicio de análisis de IA", e);
        }

        return new IaResponse("No Definido", 0.0, "Análisis fallido o sin respuesta de la IA.");
    }
}