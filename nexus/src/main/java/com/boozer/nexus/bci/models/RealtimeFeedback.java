package com.boozer.nexus.bci.models;

import java.time.LocalDateTime;

public class RealtimeFeedback {
    private int cycleNumber;
    private double performanceScore;
    private boolean targetAchieved;
    private String feedbackType;
    private LocalDateTime timestamp;

    public int getCycleNumber() { return cycleNumber; }
    public void setCycleNumber(int cycleNumber) { this.cycleNumber = cycleNumber; }
    public double getPerformanceScore() { return performanceScore; }
    public void setPerformanceScore(double performanceScore) { this.performanceScore = performanceScore; }
    public boolean isTargetAchieved() { return targetAchieved; }
    public void setTargetAchieved(boolean targetAchieved) { this.targetAchieved = targetAchieved; }
    public String getFeedbackType() { return feedbackType; }
    public void setFeedbackType(String feedbackType) { this.feedbackType = feedbackType; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}
