package com.boozer.nexus.bci.models;

public class BCISessionRequest {
    private String sessionType;
    private String participantId;
    private BCIConfiguration configuration;
    private boolean consciousnessIntegration;

    public String getSessionType() { return sessionType; }
    public void setSessionType(String sessionType) { this.sessionType = sessionType; }
    public String getParticipantId() { return participantId; }
    public void setParticipantId(String participantId) { this.participantId = participantId; }
    public BCIConfiguration getConfiguration() { return configuration; }
    public void setConfiguration(BCIConfiguration configuration) { this.configuration = configuration; }
    public boolean isConsciousnessIntegration() { return consciousnessIntegration; }
    public void setConsciousnessIntegration(boolean consciousnessIntegration) { this.consciousnessIntegration = consciousnessIntegration; }
}
