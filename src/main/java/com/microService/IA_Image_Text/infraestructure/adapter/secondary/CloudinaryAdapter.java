package com.microService.IA_Image_Text.infraestructure.adapter.secondary;


import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.microService.IA_Image_Text.application.port.in.ImageStorageUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CloudinaryAdapter implements ImageStorageUseCase {
    private final String FOLDER_NAME = "haircut_analysis";
    private final Cloudinary cloudinary;


    // funcion que gestiona la imagen y la manda a cloudinary
    @Override
    public String execute(byte[] imageBytes, String userId) {
        try {
            // Genera un id unico para el archivo, incluyendo el id del usuario
            String publicId = userId + "/" + UUID.randomUUID().toString();

            // Sube imagen a cloudinary
            Map uploadResult= cloudinary.uploader().upload(
                    imageBytes,
                    ObjectUtils.asMap(
                            "folder", FOLDER_NAME,
                            "public_id", publicId
                    )
            );
            // Aqui obtenemos la URL segura del archivo (Imagen)
            return uploadResult.get("secure_url").toString();
        }catch (IOException error){
            System.out.println("Error al subir a cloudinary: " + error.getMessage());
            throw new RuntimeException("Fallo en el almacenamiento de la imagen.", error);
        }
    }
}
