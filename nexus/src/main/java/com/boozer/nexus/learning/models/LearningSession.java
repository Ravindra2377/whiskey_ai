package com.boozer.nexus.learning.models;

import com.boozer.nexus.consciousness.models.ConsciousnessSession;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class LearningSession {
    private String sessionId;
    private LearningConfiguration configuration;
    private LearningStatus status;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private LearningMetrics metrics;
    private List<KnowledgeItem> knowledgeBase;
    private List<LearningExperience> experienceBuffer;
    private ConsciousnessSession consciousnessSession;
    private String errorMessage;

    public LearningSession() {
        this.knowledgeBase = new ArrayList<>();
        this.experienceBuffer = new ArrayList<>();
        this.metrics = new LearningMetrics();
    }

    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }
    public LearningConfiguration getConfiguration() { return configuration; }
    public void setConfiguration(LearningConfiguration configuration) { this.configuration = configuration; }
    public LearningStatus getStatus() { return status; }
    public void setStatus(LearningStatus status) { this.status = status; }
    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }
    public LocalDateTime getEndTime() { return endTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }
    public LearningMetrics getMetrics() { return metrics; }
    public void setMetrics(LearningMetrics metrics) { this.metrics = metrics; }
    public List<KnowledgeItem> getKnowledgeBase() { return knowledgeBase; }
    public void setKnowledgeBase(List<KnowledgeItem> knowledgeBase) { this.knowledgeBase = knowledgeBase; }
    public List<LearningExperience> getExperienceBuffer() { return experienceBuffer; }
    public void setExperienceBuffer(List<LearningExperience> experienceBuffer) { this.experienceBuffer = experienceBuffer; }
    public ConsciousnessSession getConsciousnessSession() { return consciousnessSession; }
    public void setConsciousnessSession(ConsciousnessSession consciousnessSession) { this.consciousnessSession = consciousnessSession; }
    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
}
