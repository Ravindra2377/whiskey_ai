package com.boozer.nexus.learning.models;

import java.time.LocalDateTime;

public class MetaKnowledge {
    private String pattern;
    private int occurrenceCount;
    private double averageSuccess;
    private LocalDateTime lastUpdated;

    public String getPattern() { return pattern; }
    public void setPattern(String pattern) { this.pattern = pattern; }
    public int getOccurrenceCount() { return occurrenceCount; }
    public void setOccurrenceCount(int occurrenceCount) { this.occurrenceCount = occurrenceCount; }
    public double getAverageSuccess() { return averageSuccess; }
    public void setAverageSuccess(double averageSuccess) { this.averageSuccess = averageSuccess; }
    public LocalDateTime getLastUpdated() { return lastUpdated; }
    public void setLastUpdated(LocalDateTime lastUpdated) { this.lastUpdated = lastUpdated; }
}