package com.microService.IA_Image_Text.application.service;

import com.microService.IA_Image_Text.application.port.in.UploadAndAnalyzeUseCase;
import com.microService.IA_Image_Text.application.port.out.IaRecommendationPort;
import com.microService.IA_Image_Text.application.port.out.ImageStorafePort;
import com.microService.IA_Image_Text.domain.model.IaResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UploadAndAnalyzeService implements UploadAndAnalyzeUseCase {

    private final ImageStorafePort imageStorafePort;
    private final IaRecommendationPort iaRecommendationPort;

    @Override
    public IaResponse execute(byte[] imageBytes, String userId) {
        // Almacenar la imagen llamando al port
        String imageUrl = imageStorafePort.saveImage(imageBytes, userId);

        // Usar la url para obtener una respuesta o la recomendacion de la >Ia
        return iaRecommendationPort.getRecommendation(imageUrl);
    }
}
