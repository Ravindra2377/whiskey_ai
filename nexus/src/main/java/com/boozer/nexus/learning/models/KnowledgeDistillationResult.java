package com.boozer.nexus.learning.models;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class KnowledgeDistillationResult {
    private String distillationId;
    private LocalDateTime timestamp = LocalDateTime.now();
    private boolean success;
    private String description;
    private Map<String, Object> distilledKnowledge = new HashMap<>();
    private double compressionRatio;
    private double accuracyRetention;
    private String errorMessage;

    public String getDistillationId() { return distillationId; }
    public void setDistillationId(String distillationId) { this.distillationId = distillationId; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Map<String, Object> getDistilledKnowledge() { return distilledKnowledge; }
    public void setDistilledKnowledge(Map<String, Object> distilledKnowledge) { this.distilledKnowledge = distilledKnowledge; }
    public double getCompressionRatio() { return compressionRatio; }
    public void setCompressionRatio(double compressionRatio) { this.compressionRatio = compressionRatio; }
    public double getAccuracyRetention() { return accuracyRetention; }
    public void setAccuracyRetention(double accuracyRetention) { this.accuracyRetention = accuracyRetention; }
    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
}
