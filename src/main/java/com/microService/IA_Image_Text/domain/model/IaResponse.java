package com.microService.IA_Image_Text.domain.model;

public class IaResponse {
    private final String cutName;
    private final double confidence;
    private final String rationale;

    public IaResponse(String cutName, double confidence, String rationale) {
        if (cutName == null || cutName.isBlank()) {
            throw new IllegalArgumentException("cutName no puede ser nulo o vacío");
        }
        if (confidence < 0.0 || confidence > 1.0) {
            throw new IllegalArgumentException("confidence debe estar entre 0.0 y 1.0, pero fue: " + confidence);
        }
        if (rationale == null || rationale.isBlank()) {
            throw new IllegalArgumentException("rationale no puede ser nulo o vacío");
        }
        this.cutName = cutName;
        this.confidence = confidence;
        this.rationale = rationale;
    }

    public String getCutName() {
        return cutName;
    }

    public double getConfidence() {
        return confidence;
    }

    public String getRationale() {
        return rationale;
    }
}
