package com.microService.IA_Image_Text.infraestructure.adapter.secondary.openai.mapper;

import com.microService.IA_Image_Text.domain.model.IaResponse;
import com.microService.IA_Image_Text.infraestructure.adapter.secondary.dto.AiResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OpenAIResponseMapper {

    @Mapping(source = "recommendedCut", target = "cutName")
    @Mapping(source = "confidenceScore", target = "confidence")
    @Mapping(source = "analysisDetails", target = "rationale")
    IaResponse toDomain(AiResponseDto dto);
}

