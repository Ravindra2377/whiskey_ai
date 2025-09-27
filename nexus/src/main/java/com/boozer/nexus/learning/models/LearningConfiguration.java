package com.boozer.nexus.learning.models;

import java.util.HashMap;
import java.util.Map;

public class LearningConfiguration {
    private String learningStrategy = "incremental";
    private boolean forgettingPreventionEnabled = true;
    private boolean experienceReplayEnabled = true;
    private boolean metaLearningEnabled = false;
    private boolean architectureAdaptationEnabled = false;
    private double learningRate = 0.01;
    private double forgettingThreshold = 0.7;
    private int batchSize = 10;
    private int maxExperienceBuffer = 1000;
    private Map<String, Object> parameters = new HashMap<>();

    public String getLearningStrategy() { return learningStrategy; }
    public void setLearningStrategy(String learningStrategy) { this.learningStrategy = learningStrategy; }
    public boolean isForgettingPreventionEnabled() { return forgettingPreventionEnabled; }
    public void setForgettingPreventionEnabled(boolean forgettingPreventionEnabled) { this.forgettingPreventionEnabled = forgettingPreventionEnabled; }
    public boolean isExperienceReplayEnabled() { return experienceReplayEnabled; }
    public void setExperienceReplayEnabled(boolean experienceReplayEnabled) { this.experienceReplayEnabled = experienceReplayEnabled; }
    public boolean isMetaLearningEnabled() { return metaLearningEnabled; }
    public void setMetaLearningEnabled(boolean metaLearningEnabled) { this.metaLearningEnabled = metaLearningEnabled; }
    public boolean isArchitectureAdaptationEnabled() { return architectureAdaptationEnabled; }
    public void setArchitectureAdaptationEnabled(boolean architectureAdaptationEnabled) { this.architectureAdaptationEnabled = architectureAdaptationEnabled; }
    public double getLearningRate() { return learningRate; }
    public void setLearningRate(double learningRate) { this.learningRate = learningRate; }
    public double getForgettingThreshold() { return forgettingThreshold; }
    public void setForgettingThreshold(double forgettingThreshold) { this.forgettingThreshold = forgettingThreshold; }
    public int getBatchSize() { return batchSize; }
    public void setBatchSize(int batchSize) { this.batchSize = batchSize; }
    public int getMaxExperienceBuffer() { return maxExperienceBuffer; }
    public void setMaxExperienceBuffer(int maxExperienceBuffer) { this.maxExperienceBuffer = maxExperienceBuffer; }
    public Map<String, Object> getParameters() { return parameters; }
    public void setParameters(Map<String, Object> parameters) { this.parameters = parameters; }
}
