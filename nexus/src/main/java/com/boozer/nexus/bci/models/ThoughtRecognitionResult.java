package com.boozer.nexus.bci.models;

public class ThoughtRecognitionResult {
    private String sessionId;
    private ThoughtPattern recognizedPattern;
    private double confidence;
    private long processingTime;
    private boolean successful;
    private String errorMessage;

    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }
    public ThoughtPattern getRecognizedPattern() { return recognizedPattern; }
    public void setRecognizedPattern(ThoughtPattern recognizedPattern) { this.recognizedPattern = recognizedPattern; }
    public double getConfidence() { return confidence; }
    public void setConfidence(double confidence) { this.confidence = confidence; }
    public long getProcessingTime() { return processingTime; }
    public void setProcessingTime(long processingTime) { this.processingTime = processingTime; }
    public boolean isSuccessful() { return successful; }
    public void setSuccessful(boolean successful) { this.successful = successful; }
    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
}
