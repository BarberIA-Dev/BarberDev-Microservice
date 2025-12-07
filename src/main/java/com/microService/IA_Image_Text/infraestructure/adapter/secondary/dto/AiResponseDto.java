package com.microService.IA_Image_Text.infraestructure.adapter.secondary.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AiResponseDto {

    private String recommendedCut;
    private double confidenceScore;
    private String analysisDetails;
}
