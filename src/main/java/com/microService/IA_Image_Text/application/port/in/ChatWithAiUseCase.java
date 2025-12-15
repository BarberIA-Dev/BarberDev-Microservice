package com.microService.IA_Image_Text.application.port.in;

public interface ChatWithAiUseCase {
    String chat(String userMessage, String recommendationContext);
}
