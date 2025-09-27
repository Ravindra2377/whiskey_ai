package com.boozer.nexus.bci.models;

public class NeurofeedbackCycle {
    private int cycleNumber;
    private double performanceScore;
    private boolean targetAchieved;
    private String improvementDirection;

    public int getCycleNumber() { return cycleNumber; }
    public void setCycleNumber(int cycleNumber) { this.cycleNumber = cycleNumber; }
    public double getPerformanceScore() { return performanceScore; }
    public void setPerformanceScore(double performanceScore) { this.performanceScore = performanceScore; }
    public boolean isTargetAchieved() { return targetAchieved; }
    public void setTargetAchieved(boolean targetAchieved) { this.targetAchieved = targetAchieved; }
    public String getImprovementDirection() { return improvementDirection; }
    public void setImprovementDirection(String improvementDirection) { this.improvementDirection = improvementDirection; }
}
