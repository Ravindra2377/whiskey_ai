package com.boozer.nexus.bci.models;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ThoughtPattern {
    private String patternType;
    private double confidence;
    private double intensity;
    private long duration;
    private FrequencySignature frequencySignature;
    private List<String> associatedConcepts;
    private LocalDateTime timestamp;

    public String getPatternType() { return patternType; }
    public void setPatternType(String patternType) { this.patternType = patternType; }
    public double getConfidence() { return confidence; }
    public void setConfidence(double confidence) { this.confidence = confidence; }
    public double getIntensity() { return intensity; }
    public void setIntensity(double intensity) { this.intensity = intensity; }
    public long getDuration() { return duration; }
    public void setDuration(long duration) { this.duration = duration; }
    public FrequencySignature getFrequencySignature() { return frequencySignature; }
    public void setFrequencySignature(FrequencySignature frequencySignature) { this.frequencySignature = frequencySignature; }
    public List<String> getAssociatedConcepts() { return associatedConcepts != null ? new ArrayList<>(associatedConcepts) : null; }
    public void setAssociatedConcepts(List<String> associatedConcepts) { this.associatedConcepts = associatedConcepts != null ? new ArrayList<>(associatedConcepts) : null; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}
