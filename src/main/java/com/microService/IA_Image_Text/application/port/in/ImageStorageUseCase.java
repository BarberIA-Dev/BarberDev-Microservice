package com.microService.IA_Image_Text.application.port.in;

public interface ImageStorageUseCase {
    // Con este metodo recibimos la imagen
    String execute(byte[] imageBytes, String userId);
}
