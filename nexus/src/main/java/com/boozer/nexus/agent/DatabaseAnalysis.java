package com.boozer.nexus.agent;

public class DatabaseAnalysis {
    private String issueType;
    private String complexity;
    private double confidenceScore;
    
    // Constructors
    public DatabaseAnalysis() {}
    
    public DatabaseAnalysis(String issueType, String complexity, double confidenceScore) {
        this.issueType = issueType;
        this.complexity = complexity;
        this.confidenceScore = confidenceScore;
    }
    
    // Getters and Setters
    public String getIssueType() {
        return issueType;
    }
    
    public void setIssueType(String issueType) {
        this.issueType = issueType;
    }
    
    public String getComplexity() {
        return complexity;
    }
    
    public void setComplexity(String complexity) {
        this.complexity = complexity;
    }
    
    public double getConfidenceScore() {
        return confidenceScore;
    }
    
    public void setConfidenceScore(double confidenceScore) {
        this.confidenceScore = confidenceScore;
    }
}
