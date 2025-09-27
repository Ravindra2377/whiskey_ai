package com.boozer.nexus.consciousness.models;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Extracted public ConsciousnessSession minimal version used by other packages.
 */
public class ConsciousnessSession {
    private String sessionId;
    private String entityId;
    private ConsciousnessState currentState; // placeholder type (will stub if missing)
    private List<Object> sessionMemories;
    private LocalDateTime creationTime;
    private LocalDateTime lastActivity;

    public ConsciousnessSession(String sessionId, String entityId) {
        this.sessionId = sessionId;
        this.entityId = entityId;
        this.creationTime = LocalDateTime.now();
        this.lastActivity = LocalDateTime.now();
        this.sessionMemories = new ArrayList<>();
    }

    public String getSessionId() { return sessionId; }
    public String getEntityId() { return entityId; }
    public LocalDateTime getCreationTime() { return creationTime; }
    public LocalDateTime getLastActivity() { return lastActivity; }
    public ConsciousnessState getCurrentState() { return currentState; }
    public void setCurrentState(ConsciousnessState currentState) { this.currentState = currentState; }

    // Stub updater referenced by engine
    public void updateWithExperience(Object input, ConsciousnessState state) {
        this.currentState = state;
        this.lastActivity = java.time.LocalDateTime.now();
    }
}
