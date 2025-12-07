package com.microService.IA_Image_Text.infraestructure.adapter.in.web.mapper;

import com.microService.IA_Image_Text.domain.model.IaResponse;
import com.microService.IA_Image_Text.infraestructure.adapter.in.web.dto.HaircutResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface HaircutResponseMapper {
    @Mapping(source = "cutName", target = "recommendedStyle")
    @Mapping(source = "rationale", target = "analysisReport")
    @Mapping(expression = "java(\"\" + (int)(iaResponse.getConfidence() * 100) + \"%\")",
            target = "confidenceLevel")
    HaircutResponseDto toDto(IaResponse iaResponse);
}
