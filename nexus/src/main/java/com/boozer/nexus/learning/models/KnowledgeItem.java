package com.boozer.nexus.learning.models;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class KnowledgeItem {
    private String knowledgeId;
    private String learningUpdateId;
    private String knowledgeType;
    private double confidence;
    private LocalDateTime creationTime;
    private LocalDateTime lastAccessed;
    private Map<String, Double> featureWeights = new HashMap<>();
    private Map<String, Object> parameters = new HashMap<>();
    private EpisodicMemory episodicMemory;
    private int accessCount;
    private double importance = 0.5;

    public String getKnowledgeId() { return knowledgeId; }
    public void setKnowledgeId(String knowledgeId) { this.knowledgeId = knowledgeId; }
    public String getLearningUpdateId() { return learningUpdateId; }
    public void setLearningUpdateId(String learningUpdateId) { this.learningUpdateId = learningUpdateId; }
    public String getKnowledgeType() { return knowledgeType; }
    public void setKnowledgeType(String knowledgeType) { this.knowledgeType = knowledgeType; }
    public double getConfidence() { return confidence; }
    public void setConfidence(double confidence) { this.confidence = confidence; }
    public LocalDateTime getCreationTime() { return creationTime; }
    public void setCreationTime(LocalDateTime creationTime) { this.creationTime = creationTime; }
    public LocalDateTime getLastAccessed() { return lastAccessed; }
    public void setLastAccessed(LocalDateTime lastAccessed) { this.lastAccessed = lastAccessed; }
    public Map<String, Double> getFeatureWeights() { return featureWeights; }
    public void setFeatureWeights(Map<String, Double> featureWeights) { this.featureWeights = featureWeights; }
    public Map<String, Object> getParameters() { return parameters; }
    public void setParameters(Map<String, Object> parameters) { this.parameters = parameters; }
    public EpisodicMemory getEpisodicMemory() { return episodicMemory; }
    public void setEpisodicMemory(EpisodicMemory episodicMemory) { this.episodicMemory = episodicMemory; }
    public int getAccessCount() { return accessCount; }
    public void setAccessCount(int accessCount) { this.accessCount = accessCount; }
    public double getImportance() { return importance; }
    public void setImportance(double importance) { this.importance = importance; }
}
