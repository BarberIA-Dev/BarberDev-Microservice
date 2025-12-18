package com.microService.IA_Image_Text.infraestructure.adapter.secondary.dto;

import lombok.Data;

import java.util.List;

@Data
public class OpenIAResponseDto {
    private List<Output> output;

    @Data
    public static class Output {
        private List<Content> content;
    }

    @Data
    public static class Content {
        private String type;
        private String text;
    }
}
