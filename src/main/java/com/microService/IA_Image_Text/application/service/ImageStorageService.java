package com.microService.IA_Image_Text.application.service;

import com.microService.IA_Image_Text.application.port.in.ImageStorageUseCase;
import com.microService.IA_Image_Text.application.port.out.ImageStorafePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ImageStorageService implements ImageStorageUseCase {

    public final ImageStorafePort imageStorafePort;

    @Override
    public String execute(byte[] imageBytes, String userId) {
        return imageStorafePort.saveImage(imageBytes, userId);
    }
}
