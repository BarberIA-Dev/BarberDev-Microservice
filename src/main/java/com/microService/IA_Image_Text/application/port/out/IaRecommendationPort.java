package com.microService.IA_Image_Text.application.port.out;

import com.microService.IA_Image_Text.domain.model.IaResponse;

public interface IaRecommendationPort {
    IaResponse getRecommendation(String imageUrl);
}
