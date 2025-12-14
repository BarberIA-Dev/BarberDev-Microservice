package com.microService.IA_Image_Text.application.service;

import com.microService.IA_Image_Text.application.port.in.UploadAndAnalyzeUseCase;
import com.microService.IA_Image_Text.application.port.out.IaRecommendationPort;
import com.microService.IA_Image_Text.application.port.out.ImageStoragePort;
import com.microService.IA_Image_Text.domain.exception.ExternalServiceException;
import com.microService.IA_Image_Text.domain.exception.ImageProcessingException;
import com.microService.IA_Image_Text.domain.exception.InvalidImageException;
import com.microService.IA_Image_Text.domain.model.IaResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UploadAndAnalyzeService implements UploadAndAnalyzeUseCase {

    private static final long MAX_IMAGE_SIZE = 10 * 1024 * 1024;
    private static final String[] ALLOWED_CONTENT_TYPES = {"image/jpeg", "image/jpg", "image/png", "image/webp"};

    private final ImageStoragePort imageStoragePort;
    private final IaRecommendationPort iaRecommendationPort;

    @Override
    public IaResponse execute(byte[] imageBytes, String contentType, String userId) {
        validateImage(imageBytes, contentType);

        try {

            String imageUrl = imageStoragePort.saveImage(imageBytes, userId);
            log.info("Imagen almacenada exitosamente para usuario: {}", userId);


            IaResponse response = iaRecommendationPort.getRecommendation(imageUrl);
            log.info("Recomendación generada exitosamente para usuario: {}", userId);
            return response;

        } catch (RuntimeException e) {
            log.error("Error en el servicio de análisis: {}", e.getMessage(), e);
            throw new ExternalServiceException("Fallo en el servicio de análisis de IA: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("Error inesperado procesando imagen: {}", e.getMessage(), e);
            throw new ImageProcessingException("Error al procesar la imagen", e);
        }
    }

    private void validateImage(byte[] imageBytes, String contentType) {
        if (imageBytes == null || imageBytes.length == 0) {
            throw new InvalidImageException("El archivo está vacío o no es válido");
        }

        if (imageBytes.length > MAX_IMAGE_SIZE) {
            throw new InvalidImageException(
                    String.format("El archivo excede el tamaño máximo permitido de %d MB", MAX_IMAGE_SIZE / (1024 * 1024))
            );
        }

        if (contentType == null || !isValidContentType(contentType)) {
            throw new InvalidImageException("Solo se permiten imágenes en formato JPEG, PNG o WEBP");
        }
    }

    private boolean isValidContentType(String contentType) {
        if (contentType == null) {
            return false;
        }
        String normalizedType = contentType.toLowerCase().trim();
        for (String allowedType : ALLOWED_CONTENT_TYPES) {
            if (normalizedType.equals(allowedType) || normalizedType.startsWith(allowedType + "/")) {
                return true;
            }
        }
        return normalizedType.startsWith("image/");
    }
}
