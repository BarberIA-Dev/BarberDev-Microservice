package com.microService.IA_Image_Text.application.port.out;

public interface IaConversationPort {
    String chatWithContext(String message, String context);
}
