package com.boozer.nexus.learning.models;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class LearningUpdate {
    private String updateId;
    private String learningType;
    private LocalDateTime timestamp;
    private String experienceId;
    private Map<String, Object> parameters = new HashMap<>();
    private Map<String, Double> weightUpdates = new HashMap<>();
    private double confidence = 0.5;
    private String knowledgeType;
    private EpisodicMemory episodicMemory;
    private String consciousnessInsight;
    private String errorMessage;

    public String getUpdateId() { return updateId; }
    public void setUpdateId(String updateId) { this.updateId = updateId; }
    public String getLearningType() { return learningType; }
    public void setLearningType(String learningType) { this.learningType = learningType; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    public String getExperienceId() { return experienceId; }
    public void setExperienceId(String experienceId) { this.experienceId = experienceId; }
    public Map<String, Object> getParameters() { return parameters; }
    public void setParameters(Map<String, Object> parameters) { this.parameters = parameters; }
    public Map<String, Double> getWeightUpdates() { return weightUpdates; }
    public void setWeightUpdates(Map<String, Double> weightUpdates) { this.weightUpdates = weightUpdates; }
    public double getConfidence() { return confidence; }
    public void setConfidence(double confidence) { this.confidence = confidence; }
    public String getKnowledgeType() { return knowledgeType; }
    public void setKnowledgeType(String knowledgeType) { this.knowledgeType = knowledgeType; }
    public EpisodicMemory getEpisodicMemory() { return episodicMemory; }
    public void setEpisodicMemory(EpisodicMemory episodicMemory) { this.episodicMemory = episodicMemory; }
    public String getConsciousnessInsight() { return consciousnessInsight; }
    public void setConsciousnessInsight(String consciousnessInsight) { this.consciousnessInsight = consciousnessInsight; }
    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
}
