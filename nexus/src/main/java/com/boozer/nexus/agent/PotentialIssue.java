package com.boozer.nexus.agent;

public class PotentialIssue {
    private String issueType;
    private String severity;
    private String description;
    private double confidenceScore;
    private String estimatedImpact;
    private String estimatedResolutionTime;
    
    // Constructors
    public PotentialIssue() {}
    
    public PotentialIssue(String issueType, String severity, String description, 
                         double confidenceScore, String estimatedImpact, String estimatedResolutionTime) {
        this.issueType = issueType;
        this.severity = severity;
        this.description = description;
        this.confidenceScore = confidenceScore;
        this.estimatedImpact = estimatedImpact;
        this.estimatedResolutionTime = estimatedResolutionTime;
    }
    
    // Getters and Setters
    public String getIssueType() {
        return issueType;
    }
    
    public void setIssueType(String issueType) {
        this.issueType = issueType;
    }
    
    public String getSeverity() {
        return severity;
    }
    
    public void setSeverity(String severity) {
        this.severity = severity;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public double getConfidenceScore() {
        return confidenceScore;
    }
    
    public void setConfidenceScore(double confidenceScore) {
        this.confidenceScore = confidenceScore;
    }
    
    public String getEstimatedImpact() {
        return estimatedImpact;
    }
    
    public void setEstimatedImpact(String estimatedImpact) {
        this.estimatedImpact = estimatedImpact;
    }
    
    public String getEstimatedResolutionTime() {
        return estimatedResolutionTime;
    }
    
    public void setEstimatedResolutionTime(String estimatedResolutionTime) {
        this.estimatedResolutionTime = estimatedResolutionTime;
    }
}
