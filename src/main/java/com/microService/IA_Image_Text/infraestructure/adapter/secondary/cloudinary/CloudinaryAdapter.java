package com.microService.IA_Image_Text.infraestructure.adapter.secondary.cloudinary;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.microService.IA_Image_Text.application.port.out.ImageStoragePort;
import com.microService.IA_Image_Text.domain.exception.ImageProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CloudinaryAdapter implements ImageStoragePort {
    
    private static final String FOLDER_NAME = "haircut_analysis";
    private final Cloudinary cloudinary;

    @Override
    public String saveImage(byte[] imageBytes, String userId) {
        log.info("Subiendo imagen a Cloudinary para usuario: {}", userId);
        
        try {
            String publicId = userId + "/" + UUID.randomUUID().toString();

            @SuppressWarnings("unchecked")
            Map<String, Object> uploadResult = (Map<String, Object>) cloudinary.uploader().upload(
                    imageBytes,
                    ObjectUtils.asMap(
                            "folder", FOLDER_NAME,
                            "public_id", publicId
                    )
            );
            
            String secureUrl = uploadResult.get("secure_url").toString();
            log.info("Imagen subida exitosamente a Cloudinary: {}", secureUrl);
            return secureUrl;
            
        } catch (IOException e) {
            log.error("Error al subir imagen a Cloudinary: {}", e.getMessage(), e);
            throw new ImageProcessingException("Fallo en el almacenamiento de la imagen en Cloudinary", e);
        } catch (Exception e) {
            log.error("Error inesperado subiendo imagen a Cloudinary: {}", e.getMessage(), e);
            throw new ImageProcessingException("Error inesperado al almacenar la imagen", e);
        }
    }
}

