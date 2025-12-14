package com.microService.IA_Image_Text.infraestructure.adapter.in.web.exception;

import com.microService.IA_Image_Text.domain.exception.ExternalServiceException;
import com.microService.IA_Image_Text.domain.exception.ImageProcessingException;
import com.microService.IA_Image_Text.domain.exception.InvalidImageException;
import org.springframework.http.ProblemDetail;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(InvalidImageException.class)
    public ProblemDetail handleInvalidImage(InvalidImageException e) {
        log.warn("Imagen inválida: {}", e.getMessage());
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, e.getMessage());
        problemDetail.setTitle("Formato de imagen inválido");
        return problemDetail;
    }

    @ExceptionHandler(ImageProcessingException.class)
    public ProblemDetail handleImageProcessing(ImageProcessingException e) {
        log.error("Error procesando imagen: {}", e.getMessage(), e);
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, "Fallo al procesar el archivo en el servidor");
        problemDetail.setTitle("Error al procesar la imagen");
        return problemDetail;
    }

    @ExceptionHandler(ExternalServiceException.class)
    public ProblemDetail handleExternalService(ExternalServiceException e) {
        log.error("Fallo de servicio externo: {}", e.getMessage(), e);
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.SERVICE_UNAVAILABLE, e.getMessage());
        problemDetail.setTitle("Fallo en servicio externo");
        return problemDetail;
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ProblemDetail handleIllegalArgument(IllegalArgumentException e) {
        log.warn("Argumento inválido: {}", e.getMessage());
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, e.getMessage());
        problemDetail.setTitle("Error de validación");
        return problemDetail;
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleGenericException(Exception e) {
        log.error("Error inesperado: {}", e.getMessage(), e);
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, "Ha ocurrido un error inesperado");
        problemDetail.setTitle("Error interno del servidor");
        return problemDetail;
    }
}

