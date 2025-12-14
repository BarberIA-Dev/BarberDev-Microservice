package com.microService.IA_Image_Text.infraestructure.adapter.in.web.controller;

import com.microService.IA_Image_Text.application.port.in.UploadAndAnalyzeUseCase;
import com.microService.IA_Image_Text.domain.model.IaResponse;
import com.microService.IA_Image_Text.infraestructure.adapter.in.web.dto.HaircutResponseDto;
import com.microService.IA_Image_Text.infraestructure.adapter.in.web.mapper.HaircutResponseMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Slf4j
@RestController
@RequestMapping("/api/haircut")
@RequiredArgsConstructor
public class ImageUploadController {

    private final UploadAndAnalyzeUseCase uploadAndAnalyzeUseCase;
    private final HaircutResponseMapper responseMapper;

    @PostMapping("/analyze")
    public ResponseEntity<HaircutResponseDto> analyzeImage(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "userId", defaultValue = "anonymous") String userId
    ) {
        log.info("Solicitud de análisis recibida para usuario: {}", userId);

        try {
            byte[] imageBytes = file.getBytes();
            String contentType = file.getContentType();

            IaResponse domainResponse = uploadAndAnalyzeUseCase.execute(imageBytes, contentType, userId);
            HaircutResponseDto responseDto = responseMapper.toDto(domainResponse);

            log.info("Análisis completado exitosamente para usuario: {}", userId);
            return ResponseEntity.ok(responseDto);

        } catch (IOException e) {
            log.error("Error leyendo archivo: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body(
                    new HaircutResponseDto("Error de Servidor", "0%", "Fallo al leer el archivo")
            );
        }
    }
}
