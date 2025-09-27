package com.boozer.nexus.bci.models;

import java.time.LocalDateTime;

public class BCISessionInfo {
    private String sessionId;
    private String sessionType;
    private String participantId;
    private LocalDateTime startTime;
    private BCIMetrics currentMetrics;
    private SignalQuality signalQuality;
    private double calibrationAccuracy;
    private boolean active;

    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }
    public String getSessionType() { return sessionType; }
    public void setSessionType(String sessionType) { this.sessionType = sessionType; }
    public String getParticipantId() { return participantId; }
    public void setParticipantId(String participantId) { this.participantId = participantId; }
    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }
    public BCIMetrics getCurrentMetrics() { return currentMetrics; }
    public void setCurrentMetrics(BCIMetrics currentMetrics) { this.currentMetrics = currentMetrics; }
    public SignalQuality getSignalQuality() { return signalQuality; }
    public void setSignalQuality(SignalQuality signalQuality) { this.signalQuality = signalQuality; }
    public double getCalibrationAccuracy() { return calibrationAccuracy; }
    public void setCalibrationAccuracy(double calibrationAccuracy) { this.calibrationAccuracy = calibrationAccuracy; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}
