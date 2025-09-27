package com.boozer.nexus.consciousness.models;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MetacognitionResult {
    private String reflectionType;
    private double metacognitionLevel;
    private Map<String,Object> insights = new HashMap<>();
    private List<String> strategies;
    private double confidenceAssessment;
    private LocalDateTime timestamp;

    public String getReflectionType() { return reflectionType; }
    public void setReflectionType(String reflectionType) { this.reflectionType = reflectionType; }
    public double getMetacognitionLevel() { return metacognitionLevel; }
    public void setMetacognitionLevel(double metacognitionLevel) { this.metacognitionLevel = metacognitionLevel; }
    public Map<String, Object> getInsights() { return insights; }
    public void setInsights(Map<String, Object> insights) { this.insights = insights; }
    public List<String> getStrategies() { return strategies; }
    public void setStrategies(List<String> strategies) { this.strategies = strategies; }
    public double getConfidenceAssessment() { return confidenceAssessment; }
    public void setConfidenceAssessment(double confidenceAssessment) { this.confidenceAssessment = confidenceAssessment; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}
