package com.boozer.nexus.learning.models;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class LearningMetrics {
    private int totalExperiences;
    private LocalDateTime lastUpdateTime;
    private Map<String, Integer> experienceTypeCounts = new HashMap<>();
    private double averageImportance;
    private double averageConfidence;
    private double knowledgeGrowthRate;
    private int forgettingPreventionTriggers;
    private int experienceReplayCount;
    private Map<String, Double> performanceMetrics = new HashMap<>();

    public int getTotalExperiences() { return totalExperiences; }
    public void setTotalExperiences(int totalExperiences) { this.totalExperiences = totalExperiences; }
    public LocalDateTime getLastUpdateTime() { return lastUpdateTime; }
    public void setLastUpdateTime(LocalDateTime lastUpdateTime) { this.lastUpdateTime = lastUpdateTime; }
    public Map<String, Integer> getExperienceTypeCounts() { return experienceTypeCounts; }
    public void setExperienceTypeCounts(Map<String, Integer> experienceTypeCounts) { this.experienceTypeCounts = experienceTypeCounts; }
    public double getAverageImportance() { return averageImportance; }
    public void setAverageImportance(double averageImportance) { this.averageImportance = averageImportance; }
    public double getAverageConfidence() { return averageConfidence; }
    public void setAverageConfidence(double averageConfidence) { this.averageConfidence = averageConfidence; }
    public double getKnowledgeGrowthRate() { return knowledgeGrowthRate; }
    public void setKnowledgeGrowthRate(double knowledgeGrowthRate) { this.knowledgeGrowthRate = knowledgeGrowthRate; }
    public int getForgettingPreventionTriggers() { return forgettingPreventionTriggers; }
    public void setForgettingPreventionTriggers(int forgettingPreventionTriggers) { this.forgettingPreventionTriggers = forgettingPreventionTriggers; }
    public int getExperienceReplayCount() { return experienceReplayCount; }
    public void setExperienceReplayCount(int experienceReplayCount) { this.experienceReplayCount = experienceReplayCount; }
    public Map<String, Double> getPerformanceMetrics() { return performanceMetrics; }
    public void setPerformanceMetrics(Map<String, Double> performanceMetrics) { this.performanceMetrics = performanceMetrics; }
}
