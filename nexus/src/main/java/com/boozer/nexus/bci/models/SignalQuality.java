package com.boozer.nexus.bci.models;

public class SignalQuality {
    private double overallScore;
    private double noiseLevel;
    private double artifactLevel;

    public double getOverallScore() { return overallScore; }
    public void setOverallScore(double overallScore) { this.overallScore = overallScore; }
    public double getNoiseLevel() { return noiseLevel; }
    public void setNoiseLevel(double noiseLevel) { this.noiseLevel = noiseLevel; }
    public double getArtifactLevel() { return artifactLevel; }
    public void setArtifactLevel(double artifactLevel) { this.artifactLevel = artifactLevel; }
}
