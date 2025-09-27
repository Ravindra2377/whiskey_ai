package com.boozer.nexus.consciousness.models;

import java.time.LocalDateTime;

public class ConsciousnessResult {
    private String sessionId;
    private String entityId;
    private Object episodicMemory;
    private Object metacognition;
    private Object selfAwareness;
    private ConsciousnessState consciousnessState;
    private Object emergentBehaviors;
    private boolean successful;
    private long processingTime;
    private LocalDateTime timestamp;

    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }
    public String getEntityId() { return entityId; }
    public void setEntityId(String entityId) { this.entityId = entityId; }
    public Object getEpisodicMemory() { return episodicMemory; }
    public void setEpisodicMemory(Object episodicMemory) { this.episodicMemory = episodicMemory; }
    public Object getMetacognition() { return metacognition; }
    public void setMetacognition(Object metacognition) { this.metacognition = metacognition; }
    public Object getSelfAwareness() { return selfAwareness; }
    public void setSelfAwareness(Object selfAwareness) { this.selfAwareness = selfAwareness; }
    public ConsciousnessState getConsciousnessState() { return consciousnessState; }
    public void setConsciousnessState(ConsciousnessState consciousnessState) { this.consciousnessState = consciousnessState; }
    public Object getEmergentBehaviors() { return emergentBehaviors; }
    public void setEmergentBehaviors(Object emergentBehaviors) { this.emergentBehaviors = emergentBehaviors; }
    public boolean isSuccessful() { return successful; }
    public void setSuccessful(boolean successful) { this.successful = successful; }
    public long getProcessingTime() { return processingTime; }
    public void setProcessingTime(long processingTime) { this.processingTime = processingTime; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}
