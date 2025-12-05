package com.microService.IA_Image_Text.application.port.out;

public interface ImageStorafePort{
    String saveImage(byte[] imageBytes, String userId);
}
