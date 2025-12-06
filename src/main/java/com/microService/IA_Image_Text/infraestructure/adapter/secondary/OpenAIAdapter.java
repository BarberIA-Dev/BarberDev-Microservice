package com.microService.IA_Image_Text.infraestructure.adapter.secondary;

import com.microService.IA_Image_Text.application.port.out.IaRecommendationPort;
import com.microService.IA_Image_Text.domain.model.IaResponse;
import com.microService.IA_Image_Text.infraestructure.adapter.secondary.dto.AiRequestDto;
import com.microService.IA_Image_Text.infraestructure.adapter.secondary.dto.AiResponseDto;
import com.microService.IA_Image_Text.infraestructure.adapter.secondary.mapper.IaResponseMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@RequiredArgsConstructor
public class OpenAIAdapter implements IaRecommendationPort {

    private final WebClient webClient;
    private final IaResponseMapper mapper;

    @Value("${ia.api.key}")
    private String apiKey;

    @Override
    public IaResponse getRecommendation(String imageUrl) {
        AiRequestDto requestDto = new AiRequestDto(imageUrl);
        try {
            AiResponseDto responseDto = webClient.post()
                    .uri("/analyze-image")
                    .header("Authorization", "Bearer " + apiKey)
                    .bodyValue(requestDto)
                    .retrieve()
                    .bodyToMono(AiResponseDto.class)
                    .block();
            if (responseDto != null){
                return mapper.toDomain(responseDto);
            }
        }catch (Exception error){
            throw new RuntimeException("Fallo en el servicio de análisis de IA", error);
        }
        return new IaResponse("No Definido", 0.0, "Análisis fallido");
    }
}
