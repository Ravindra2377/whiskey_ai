package com.boozer.nexus.bci.models;

public class PatternClassification {
    private String patternType;
    private double confidence;

    public String getPatternType() { return patternType; }
    public void setPatternType(String patternType) { this.patternType = patternType; }
    public double getConfidence() { return confidence; }
    public void setConfidence(double confidence) { this.confidence = confidence; }
}
