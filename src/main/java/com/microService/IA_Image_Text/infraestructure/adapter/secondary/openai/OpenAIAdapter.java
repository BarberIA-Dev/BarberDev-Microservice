package com.microService.IA_Image_Text.infraestructure.adapter.secondary.openai;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.microService.IA_Image_Text.application.port.out.IaRecommendationPort;
import com.microService.IA_Image_Text.domain.exception.ExternalServiceException;
import com.microService.IA_Image_Text.domain.model.IaResponse;
import com.microService.IA_Image_Text.infraestructure.adapter.secondary.dto.AiResponseDto;
import com.microService.IA_Image_Text.infraestructure.adapter.secondary.dto.OpenIAResponseDto;
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
    private final ObjectMapper objectMapper = new ObjectMapper();
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

        try {
            Map<String, Object> requestBody = Map.of(
                    "model", "gpt-4.1",
                    "response_format", Map.of("type", "json_object"),
                    "input", List.of(
                            Map.of(
                                    "role", "user",
                                    "content", List.of(
                                            Map.of("type", "input_text",
                                                    "text",
                                                    "Analiza el rostro y recomienda un corte de pelo. " +
                                                            "Devuelve SOLO JSON estricto con los campos: " +
                                                            "recommendedCut, confidenceScore (0-1), analysisDetails."),
                                            Map.of(
                                                    "type", "input_image",
                                                    "image_url", imageUrl
                                            )

                                    )
                            )
                    )
            );

            OpenIAResponseDto openIAResponseDto = webClient.post()
                    .uri("/responses")
                    .header("Authorization", "Bearer " + apiKey)
                    .header("Content-Type", "application/json")
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(OpenIAResponseDto.class)
                    .block();

            if (openIAResponseDto == null ||
                    openIAResponseDto.getOutput() == null ||
                    openIAResponseDto.getOutput().isEmpty() ||
                    openIAResponseDto.getOutput().get(0).getContent() == null ||
                    openIAResponseDto.getOutput().get(0).getContent().isEmpty()) {

                throw new ExternalServiceException("Respuesta vacía o incompleta de OpenAI");
            }

                    String rawJson = openIAResponseDto.getOutput()
                            .get(0)
                            .getContent()
                            .get(0)
                            .getText();

                    AiResponseDto aiDto;
            try {
                aiDto = objectMapper.readValue(rawJson, AiResponseDto.class);
            } catch (Exception e) {
                log.error("OpenAI devolvió JSON inválido: {}", rawJson, e);
                throw new ExternalServiceException(
                        "OpenAI devolvió un JSON inválido: " + rawJson, e
                );
            }

            log.info("Respuesta recibida de OpenAI exitosamente");
            return mapper.toDomain(aiDto);

        } catch (WebClientResponseException e) {
            throw new ExternalServiceException(
                    "Error HTTP de OpenAI: " + e.getStatusCode(), e
            );
        } catch (Exception e) {
            throw new ExternalServiceException(
                    "Fallo de conexión o procesamiento con OpenAI", e
            );
        }
    }
}