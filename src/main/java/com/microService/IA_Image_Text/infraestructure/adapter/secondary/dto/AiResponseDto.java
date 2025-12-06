package com.microService.IA_Image_Text.infraestructure.adapter.secondary.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


public record AiResponseDto(
        String recommendedCut,
        double confidenceScore,
        String analysisDetails
){}
