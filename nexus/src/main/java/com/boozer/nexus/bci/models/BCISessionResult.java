package com.boozer.nexus.bci.models;

import java.time.LocalDateTime;

public class BCISessionResult {
    private String sessionId;
    private boolean successful;
    private String message;
    private double calibrationAccuracy;
    private LocalDateTime timestamp;

    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }
    public boolean isSuccessful() { return successful; }
    public void setSuccessful(boolean successful) { this.successful = successful; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public double getCalibrationAccuracy() { return calibrationAccuracy; }
    public void setCalibrationAccuracy(double calibrationAccuracy) { this.calibrationAccuracy = calibrationAccuracy; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}
