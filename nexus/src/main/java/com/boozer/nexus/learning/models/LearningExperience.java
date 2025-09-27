package com.boozer.nexus.learning.models;

import java.time.LocalDateTime;
import java.util.*;

public class LearningExperience {
    private String experienceId = UUID.randomUUID().toString();
    private String experienceType;
    private String content;
    private LocalDateTime timestamp;
    private double importanceScore = 0.5;
    private double emotionalValence = 0.0;
    private Map<String, Object> context = new HashMap<>();
    private List<String> tags = new ArrayList<>();
    private String outcome;
    private double confidence = 0.5;

    public String getExperienceId() { return experienceId; }
    public void setExperienceId(String experienceId) { this.experienceId = experienceId; }
    public String getExperienceType() { return experienceType; }
    public void setExperienceType(String experienceType) { this.experienceType = experienceType; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    public double getImportanceScore() { return importanceScore; }
    public void setImportanceScore(double importanceScore) { this.importanceScore = importanceScore; }
    public double getEmotionalValence() { return emotionalValence; }
    public void setEmotionalValence(double emotionalValence) { this.emotionalValence = emotionalValence; }
    public Map<String, Object> getContext() { return context; }
    public void setContext(Map<String, Object> context) { this.context = context; }
    public List<String> getTags() { return tags; }
    public void setTags(List<String> tags) { this.tags = tags; }
    public String getOutcome() { return outcome; }
    public void setOutcome(String outcome) { this.outcome = outcome; }
    public double getConfidence() { return confidence; }
    public void setConfidence(double confidence) { this.confidence = confidence; }
}
