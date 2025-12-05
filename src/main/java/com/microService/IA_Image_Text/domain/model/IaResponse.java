package com.microService.IA_Image_Text.domain.model;

public class IaResponse {
    public String cutName;
    double confidence;
    String rationale;

    public IaResponse(String cutName, double confidence, String rationale) {
        this.cutName = cutName;
        this.confidence = confidence;
        this.rationale = rationale;
    }

    public String getCutName() {
        return cutName;
    }

    public void setCutName(String cutName) {
        this.cutName = cutName;
    }

    public double getConfidence() {
        return confidence;
    }

    public void setConfidence(double confidence) {
        this.confidence = confidence;
    }

    public String getRationale() {
        return rationale;
    }

    public void setRationale(String rationale) {
        this.rationale = rationale;
    }
}
