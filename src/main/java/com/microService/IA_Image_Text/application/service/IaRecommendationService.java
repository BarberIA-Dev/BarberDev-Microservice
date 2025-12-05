package com.microService.IA_Image_Text.application.service;

import com.microService.IA_Image_Text.application.port.in.IaRecommendationUseCase;
import com.microService.IA_Image_Text.application.port.out.IaRecommendationPort;
import com.microService.IA_Image_Text.domain.model.IaResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class IaRecommendationService implements IaRecommendationUseCase {

    private final IaRecommendationPort iaRecommendationPort;

    @Override
    public IaResponse getRecommendation(String imageUrl) {
        return null;
    }
}
