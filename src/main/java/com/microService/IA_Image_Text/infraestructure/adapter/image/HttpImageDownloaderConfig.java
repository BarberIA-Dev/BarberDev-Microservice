package com.microService.IA_Image_Text.infraestructure.adapter.image;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class HttpImageDownloaderConfig implements ImageDownloader {
    
    private final WebClient webClient;

    public HttpImageDownloaderConfig(@Qualifier("rawWebClient") WebClient webClient) {
        this.webClient = webClient;
    }

    @Override
    public byte[] download(String imageUrl) {
        return webClient.get()
                .uri(imageUrl)
                .retrieve()
                .bodyToMono(byte[].class)
                .block();
    }
}
