package com.boozer.nexus.learning.models;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class LearningResult {
    private String sessionId;
    private String experienceId;
    private boolean success;
    private LocalDateTime timestamp = LocalDateTime.now();
    private LearningUpdate learningUpdate;
    private List<String> insights = new ArrayList<>();
    private double confidenceScore;
    private double knowledgeGain;
    private String errorMessage;

    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }
    public String getExperienceId() { return experienceId; }
    public void setExperienceId(String experienceId) { this.experienceId = experienceId; }
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    public LearningUpdate getLearningUpdate() { return learningUpdate; }
    public void setLearningUpdate(LearningUpdate learningUpdate) { this.learningUpdate = learningUpdate; }
    public List<String> getInsights() { return insights; }
    public void setInsights(List<String> insights) { this.insights = insights; }
    public double getConfidenceScore() { return confidenceScore; }
    public void setConfidenceScore(double confidenceScore) { this.confidenceScore = confidenceScore; }
    public double getKnowledgeGain() { return knowledgeGain; }
    public void setKnowledgeGain(double knowledgeGain) { this.knowledgeGain = knowledgeGain; }
    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
}
