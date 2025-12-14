package com.microService.IA_Image_Text.application.port.out;

public interface ImageStoragePort {
    String saveImage(byte[] imageBytes, String userId);
}

