package com.boozer.nexus.learning.models;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class ArchitectureAdaptationResult {
    private String adaptationId;
    private LocalDateTime timestamp = LocalDateTime.now();
    private String adaptationType;
    private boolean success;
    private String description;
    private Map<String, Object> changes = new HashMap<>();
    private double performanceImprovement;
    private String errorMessage;

    public String getAdaptationId() { return adaptationId; }
    public void setAdaptationId(String adaptationId) { this.adaptationId = adaptationId; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    public String getAdaptationType() { return adaptationType; }
    public void setAdaptationType(String adaptationType) { this.adaptationType = adaptationType; }
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Map<String, Object> getChanges() { return changes; }
    public void setChanges(Map<String, Object> changes) { this.changes = changes; }
    public double getPerformanceImprovement() { return performanceImprovement; }
    public void setPerformanceImprovement(double performanceImprovement) { this.performanceImprovement = performanceImprovement; }
    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
}
