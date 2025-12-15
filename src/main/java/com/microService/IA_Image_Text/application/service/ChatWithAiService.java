package com.microService.IA_Image_Text.application.service;

import com.microService.IA_Image_Text.application.port.in.ChatWithAiUseCase;
import com.microService.IA_Image_Text.application.port.out.IaConversationPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatWithAiService implements ChatWithAiUseCase {

    private final IaConversationPort iaConversationPort;

    @Override
    public String chat(String userMessage, String recommendationContext) {
        log.info("Processing chat request with context");
        return iaConversationPort.chatWithContext(userMessage, recommendationContext);
    }
}
