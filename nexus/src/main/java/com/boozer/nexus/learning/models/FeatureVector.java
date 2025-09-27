package com.boozer.nexus.learning.models;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class FeatureVector {
    private String experienceId;
    private LocalDateTime timestamp;
    private Map<String, Double> features = new HashMap<>();
    private int dimensions;
    private String featureType = "default";

    public String getExperienceId() { return experienceId; }
    public void setExperienceId(String experienceId) { this.experienceId = experienceId; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    public Map<String, Double> getFeatures() { return features; }
    public void setFeatures(Map<String, Double> features) { this.features = features; }
    public int getDimensions() { return dimensions; }
    public void setDimensions(int dimensions) { this.dimensions = dimensions; }
    public String getFeatureType() { return featureType; }
    public void setFeatureType(String featureType) { this.featureType = featureType; }
}
