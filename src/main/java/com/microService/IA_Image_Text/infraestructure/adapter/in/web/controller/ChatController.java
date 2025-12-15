package com.microService.IA_Image_Text.infraestructure.adapter.in.web.controller;

import com.microService.IA_Image_Text.application.port.in.ChatWithAiUseCase;
import com.microService.IA_Image_Text.infraestructure.adapter.in.web.dto.ChatRequestDto;
import com.microService.IA_Image_Text.infraestructure.adapter.in.web.dto.ChatResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatWithAiUseCase chatWithAiUseCase;

    @PostMapping("/ask")
    public ResponseEntity<ChatResponseDto> ask(@RequestBody ChatRequestDto request) {
        log.info("Chat request received");
        String reply = chatWithAiUseCase.chat(request.getUserMessage(), request.getRecommendationContext());
        return ResponseEntity.ok(new ChatResponseDto(reply));
    }
}
