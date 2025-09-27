package com.boozer.nexus.bci.models;

import java.time.LocalDateTime;

public class CalibrationResult {
    private String sessionId;
    private String calibrationType;
    private double accuracy;
    private LocalDateTime timestamp;

    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }
    public String getCalibrationType() { return calibrationType; }
    public void setCalibrationType(String calibrationType) { this.calibrationType = calibrationType; }
    public double getAccuracy() { return accuracy; }
    public void setAccuracy(double accuracy) { this.accuracy = accuracy; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}
