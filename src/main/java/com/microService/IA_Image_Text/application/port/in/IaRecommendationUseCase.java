package com.microService.IA_Image_Text.application.port.in;

import com.microService.IA_Image_Text.domain.model.IaResponse;

public interface IaRecommendationUseCase {
    IaResponse getRecommendation(String imageUrl);
}
