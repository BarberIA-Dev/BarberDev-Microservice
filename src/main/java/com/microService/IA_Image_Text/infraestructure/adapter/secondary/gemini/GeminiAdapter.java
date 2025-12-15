package com.microService.IA_Image_Text.infraestructure.adapter.secondary.gemini;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.microService.IA_Image_Text.application.port.out.IaConversationPort;
import com.microService.IA_Image_Text.application.port.out.IaRecommendationPort;
import com.microService.IA_Image_Text.domain.exception.ExternalServiceException;
import com.microService.IA_Image_Text.domain.model.IaResponse;
import com.microService.IA_Image_Text.infraestructure.adapter.image.ImageDownloader;
import com.microService.IA_Image_Text.infraestructure.adapter.secondary.dto.AiResponseDto;
import com.microService.IA_Image_Text.infraestructure.adapter.secondary.dto.GeminiResponseDto;
import com.microService.IA_Image_Text.infraestructure.adapter.secondary.gemini.mapper.GeminiResponseMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.Base64;
import java.util.List;
import java.util.Map;

@Slf4j
@Service("geminiAdapter")
public class GeminiAdapter implements IaRecommendationPort, IaConversationPort {

        private final WebClient webClient;
        private final ImageDownloader imageDownloader;
        private final GeminiResponseMapper mapper;
        private final ObjectMapper objectMapper = new ObjectMapper();
        private final String apiKey;

        private static final String GEMINI_MODEL_PATH = "/v1beta/models/gemini-2.5-flash:generateContent";

        public GeminiAdapter(
                        @Qualifier("geminiWebClient") WebClient webClient,
                        ImageDownloader imageDownloader,
                        GeminiResponseMapper mapper,
                        @Value("${gemini.api.key}") String apiKey) {
                this.webClient = webClient;
                this.imageDownloader = imageDownloader;
                this.mapper = mapper;
                this.apiKey = apiKey;
        }

        @Override
        public IaResponse getRecommendation(String imageUrl) {
                log.info("Solicitando recomendación a Gemini para imagen: {}", imageUrl);

                try {

                        byte[] imageBytes = imageDownloader.download(imageUrl);

                        if (imageBytes == null || imageBytes.length == 0) {
                                log.error("ImageDownloader falló o devolvió bytes vacíos.");
                                throw new ExternalServiceException(
                                                "Error: No se pudo descargar la imagen de la URL: " + imageUrl);
                        }

                        String base64 = Base64.getEncoder().encodeToString(imageBytes);

                        log.info("Base64 generado (Tamaño: {} bytes). Inicio: {}...", imageBytes.length,
                                        base64.substring(0, Math.min(base64.length(), 50)));

                        Map<String, Object> request = Map.of(
                                        "contents", List.of(
                                                        Map.of(
                                                                        "parts", List.of(
                                                                                        Map.of(
                                                                                                        "text",
                                                                                                        "Analiza el rostro y recomienda un corte de pelo. "
                                                                                                                        +
                                                                                                                        "Devuelve SOLO JSON estricto con los campos: recommendedCut (string), confidenceScore (float entre 0 y 1), analysisDetails (string)."),
                                                                                        Map.of(
                                                                                                        "inlineData",
                                                                                                        Map.of(
                                                                                                                        "mimeType",
                                                                                                                        "image/jpeg",
                                                                                                                        "data",
                                                                                                                        base64))))),
                                        "generationConfig", Map.of(
                                                        "responseMimeType", "application/json"));

                        GeminiResponseDto geminiResponse = webClient.post()
                                        .uri(uri -> uri
                                                        .path(GEMINI_MODEL_PATH)
                                                        .queryParam("key", apiKey)
                                                        .build())
                                        .bodyValue(request)
                                        .retrieve()
                                        .onStatus(status -> status.isError(), response -> response
                                                        .bodyToMono(String.class)
                                                        .defaultIfEmpty("No hay cuerpo de error de Gemini.")
                                                        .flatMap(errorBody -> {
                                                                WebClientResponseException e = new WebClientResponseException(
                                                                                response.statusCode().value(),
                                                                                "Error de la API de Gemini",
                                                                                response.headers().asHttpHeaders(),
                                                                                errorBody.getBytes(),
                                                                                null);
                                                                log.error("Error {} de Gemini. Detalle del cuerpo: {}",
                                                                                response.statusCode().value(),
                                                                                errorBody);
                                                                return reactor.core.publisher.Mono
                                                                                .error(new ExternalServiceException(
                                                                                                "Error HTTP de Gemini: "
                                                                                                                + response.statusCode()
                                                                                                                                .value()
                                                                                                                + " - Detalle: "
                                                                                                                + errorBody,
                                                                                                e));
                                                        }))
                                        .bodyToMono(GeminiResponseDto.class)
                                        .block();

                        if (geminiResponse == null ||
                                        geminiResponse.getCandidates() == null ||
                                        geminiResponse.getCandidates().isEmpty()) {
                                throw new ExternalServiceException("Respuesta vacía o incompleta de Gemini");
                        }

                        String rawJson = geminiResponse.getCandidates()
                                        .get(0)
                                        .getContent()
                                        .getParts()
                                        .get(0)
                                        .getText();

                        AiResponseDto aiDto;
                        try {
                                aiDto = objectMapper.readValue(rawJson, AiResponseDto.class);
                        } catch (Exception e) {
                                log.error("Gemini devolvió JSON inválido: {}", rawJson, e);
                                throw new ExternalServiceException(
                                                "Gemini devolvió un formato JSON inesperado o inválido: " + rawJson, e);
                        }

                        log.info("Respuesta recibida de Gemini exitosamente");
                        return mapper.toDomain(aiDto);

                } catch (ExternalServiceException e) {
                        log.error("Error externo controlado en GeminiAdapter: {}", e.getMessage());
                        throw e;
                } catch (Exception e) {
                        log.error("Error de conexión general con Gemini: {}", e.getMessage(), e);
                        throw new ExternalServiceException("Fallo de conexion o procesamiento con el servicio de IA",
                                        e);
                }
        }

        @Override
        public String chatWithContext(String message, String context) {
                log.info("Solicitando chat a Gemini. Mensaje: {}, Contexto: {}", message, context);

                try {
                        Map<String, Object> request = Map.of(
                                        "contents", List.of(
                                                        Map.of(
                                                                        "parts", List.of(
                                                                                        Map.of(
                                                                                                        "text",
                                                                                                        "Contexto de recomendación previa: "
                                                                                                                        + context
                                                                                                                        + "\n\n"
                                                                                                                        +
                                                                                                                        "Pregunta del usuario: "
                                                                                                                        + message
                                                                                                                        + "\n\n"
                                                                                                                        +
                                                                                                                        "Responde como un experto barbero y estilista. Sé breve y directo.")))),
                                        "generationConfig", Map.of(
                                                        "responseMimeType", "text/plain"));

                        GeminiResponseDto geminiResponse = webClient.post()
                                        .uri(uri -> uri
                                                        .path(GEMINI_MODEL_PATH)
                                                        .queryParam("key", apiKey)
                                                        .build())
                                        .bodyValue(request)
                                        .retrieve()
                                        .onStatus(status -> status.isError(), response -> response
                                                        .bodyToMono(String.class)
                                                        .defaultIfEmpty("No hay cuerpo de error de Gemini.")
                                                        .flatMap(errorBody -> {
                                                                WebClientResponseException e = new WebClientResponseException(
                                                                                response.statusCode().value(),
                                                                                "Error de la API de Gemini",
                                                                                response.headers().asHttpHeaders(),
                                                                                errorBody.getBytes(),
                                                                                null);
                                                                log.error("Error {} de Gemini en chat. Detalle: {}",
                                                                                response.statusCode().value(),
                                                                                errorBody);
                                                                return reactor.core.publisher.Mono
                                                                                .error(new ExternalServiceException(
                                                                                                "Error HTTP de Gemini: "
                                                                                                                + response.statusCode()
                                                                                                                                .value(),
                                                                                                e));
                                                        }))
                                        .bodyToMono(GeminiResponseDto.class)
                                        .block();

                        if (geminiResponse == null ||
                                        geminiResponse.getCandidates() == null ||
                                        geminiResponse.getCandidates().isEmpty()) {
                                throw new ExternalServiceException("Respuesta vacía o incompleta de Gemini en chat");
                        }

                        return geminiResponse.getCandidates()
                                        .get(0)
                                        .getContent()
                                        .getParts()
                                        .get(0)
                                        .getText();

                } catch (ExternalServiceException e) {
                        log.error("Error externo controlado en chat: {}", e.getMessage());
                        throw e;
                } catch (Exception e) {
                        log.error("Error general en chat con Gemini: {}", e.getMessage(), e);
                        throw new ExternalServiceException("Fallo en el servicio de chat de IA", e);
                }
        }
}