package com.boozer.nexus.bci.models;

import java.time.LocalDateTime;
import java.util.UUID;

public class NeurofeedbackSession {
    private String sessionId = UUID.randomUUID().toString();
    private String protocolId;
    private LocalDateTime startTime;

    public NeurofeedbackSession(String protocolId, LocalDateTime startTime) {
        this.protocolId = protocolId;
        this.startTime = startTime;
    }

    public String getSessionId() { return sessionId; }
    public String getProtocolId() { return protocolId; }
    public LocalDateTime getStartTime() { return startTime; }
}
