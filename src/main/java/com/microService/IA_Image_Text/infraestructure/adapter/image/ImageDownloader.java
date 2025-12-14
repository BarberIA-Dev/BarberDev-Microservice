package com.microService.IA_Image_Text.infraestructure.adapter.image;


public interface ImageDownloader {
    byte[] download(String imageUrl);
}
