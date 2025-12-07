package com.microService.IA_Image_Text.infraestructure.adapter.in.web.controller;

import com.microService.IA_Image_Text.application.port.in.UploadAndAnalyzeUseCase;
import com.microService.IA_Image_Text.domain.model.IaResponse;
import com.microService.IA_Image_Text.infraestructure.adapter.in.web.dto.HaircutResponseDto;
import com.microService.IA_Image_Text.infraestructure.adapter.in.web.mapper.HaircutResponseMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/haircut")
@RequiredArgsConstructor
public class ImageUploadController {

    private final UploadAndAnalyzeUseCase uploadAndAnalyzeUseCase;
    private final HaircutResponseMapper responseMapper;

    @PostMapping("analyze")
    public ResponseEntity<HaircutResponseDto> analyzeImage (
            @RequestParam("file")MultipartFile file,
            @RequestParam(value = "userId", defaultValue = "anonymous") String userId
            ){
        if (file.isEmpty() || file.getSize() == 0) {
            return ResponseEntity.badRequest().body(
                    new HaircutResponseDto("Error de archivo", "0%", "El archivo no es válido")
            );
        }
        try {
            IaResponse domainResponse = uploadAndAnalyzeUseCase.execute(file.getBytes(), userId);

            HaircutResponseDto responseDto = responseMapper.toDto(domainResponse);

            return ResponseEntity.ok(responseDto);

        }catch (IOException e){
            return ResponseEntity.internalServerError().body(new HaircutResponseDto("Error de Servidor", "0%", "Fallo al procesar el archivo en el servidor"));
        }catch (RuntimeException e){
            System.err.println("Fallo de Adaptador Externo: " + e.getMessage());
            return ResponseEntity.internalServerError().body(
                    new HaircutResponseDto("Fallo de Servicio Externo", "0%", e.getMessage()));
        }

    }
}
