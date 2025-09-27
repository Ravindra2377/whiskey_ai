package com.boozer.nexus.bci.models;

import java.time.LocalDateTime;

public class LearnedPattern {
    private String patternType;
    private double[] featureVector;
    private double accuracy;
    private LocalDateTime learnedAt;

    public String getPatternType() { return patternType; }
    public void setPatternType(String patternType) { this.patternType = patternType; }
    public double[] getFeatureVector() { return featureVector; }
    public void setFeatureVector(double[] featureVector) { this.featureVector = featureVector; }
    public double getAccuracy() { return accuracy; }
    public void setAccuracy(double accuracy) { this.accuracy = accuracy; }
    public LocalDateTime getLearnedAt() { return learnedAt; }
    public void setLearnedAt(LocalDateTime learnedAt) { this.learnedAt = learnedAt; }
}
