package com.boozer.nexus.dto;

import java.time.LocalDateTime;
import java.util.Map;

public class ModelTrainingResult {
    private String jobId;
    private LocalDateTime estimatedCompletion;
    private Map<String, Object> currentMetrics;
    private String status;
    private String message;
    
    // Constructors
    public ModelTrainingResult() {}
    
    public ModelTrainingResult(String jobId, LocalDateTime estimatedCompletion, Map<String, Object> currentMetrics) {
        this.jobId = jobId;
        this.estimatedCompletion = estimatedCompletion;
        this.currentMetrics = currentMetrics;
        this.status = "SUCCESS";
    }
    
    // Getters and Setters
    public String getJobId() {
        return jobId;
    }
    
    public void setJobId(String jobId) {
        this.jobId = jobId;
    }
    
    public LocalDateTime getEstimatedCompletion() {
        return estimatedCompletion;
    }
    
    public void setEstimatedCompletion(LocalDateTime estimatedCompletion) {
        this.estimatedCompletion = estimatedCompletion;
    }
    
    public Map<String, Object> getCurrentMetrics() {
        return currentMetrics;
    }
    
    public void setCurrentMetrics(Map<String, Object> currentMetrics) {
        this.currentMetrics = currentMetrics;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
}
