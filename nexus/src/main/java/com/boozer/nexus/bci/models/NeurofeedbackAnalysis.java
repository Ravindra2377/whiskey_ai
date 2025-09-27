package com.boozer.nexus.bci.models;

public class NeurofeedbackAnalysis {
    private double improvementScore;
    private String nextSessionRecommendation;

    public double getImprovementScore() { return improvementScore; }
    public void setImprovementScore(double improvementScore) { this.improvementScore = improvementScore; }
    public String getNextSessionRecommendation() { return nextSessionRecommendation; }
    public void setNextSessionRecommendation(String nextSessionRecommendation) { this.nextSessionRecommendation = nextSessionRecommendation; }
}
