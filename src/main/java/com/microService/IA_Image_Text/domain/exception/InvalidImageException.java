package com.microService.IA_Image_Text.domain.exception;

public class InvalidImageException extends RuntimeException {
    public InvalidImageException(String message) {
        super(message);
    }
}

