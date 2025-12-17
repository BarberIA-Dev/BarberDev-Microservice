package com.microService.IA_Image_Text.infraestructure.adapter.in.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.microService.IA_Image_Text.application.port.in.ChatWithAiUseCase;
import com.microService.IA_Image_Text.infraestructure.adapter.in.web.dto.ChatRequestDto;
import com.microService.IA_Image_Text.infraestructure.adapter.in.web.dto.ChatResponseDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ChatControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ChatWithAiUseCase chatWithAiUseCase;

    @InjectMocks
    private ChatController chatController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(chatController).build();
    }

    @Test
    void ask_ShouldReturnChatResponse() throws Exception {
       
        String userMessage = "Pregunta";
        String context = "Contexto";
        String expectedReply = "Respuesta";

        ChatRequestDto requestDto = new ChatRequestDto(userMessage, context);

        when(chatWithAiUseCase.chat(userMessage, context)).thenReturn(expectedReply);

        mockMvc.perform(post("/api/chat/ask")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reply").value(expectedReply));
    }
}
