package com.microService.IA_Image_Text.application.service;

import com.microService.IA_Image_Text.application.port.out.IaConversationPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ChatWithAiServiceTest {

    @Mock
    private IaConversationPort iaConversationPort;

    @InjectMocks
    private ChatWithAiService chatWithAiService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void chat_ShouldReturnResponseFromPort() {
        
        String userMessage = "Hola";
        String context = "Contexto";
        String expectedResponse = "Respuesta de IA";

        when(iaConversationPort.chatWithContext(userMessage, context)).thenReturn(expectedResponse);


        String actualResponse = chatWithAiService.chat(userMessage, context);

  
        assertEquals(expectedResponse, actualResponse);
        verify(iaConversationPort).chatWithContext(userMessage, context);
    }
}
