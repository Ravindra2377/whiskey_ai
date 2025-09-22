package com.boozer.nexus.agent;

public class ProactiveRecommendation {
    private String issueType;
    private String severity;
    private String recommendedAction;
    private String estimatedImpact;
    private String implementationTime;
    private double confidenceScore;
    
    // Constructors
    public ProactiveRecommendation() {}
    
    public ProactiveRecommendation(String issueType, String severity, String recommendedAction, 
                                 String estimatedImpact, String implementationTime, double confidenceScore) {
        this.issueType = issueType;
        this.severity = severity;
        this.recommendedAction = recommendedAction;
        this.estimatedImpact = estimatedImpact;
        this.implementationTime = implementationTime;
        this.confidenceScore = confidenceScore;
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
    
    public String getRecommendedAction() {
        return recommendedAction;
    }
    
    public void setRecommendedAction(String recommendedAction) {
        this.recommendedAction = recommendedAction;
    }
    
    public String getEstimatedImpact() {
        return estimatedImpact;
    }
    
    public void setEstimatedImpact(String estimatedImpact) {
        this.estimatedImpact = estimatedImpact;
    }
    
    public String getImplementationTime() {
        return implementationTime;
    }
    
    public void setImplementationTime(String implementationTime) {
        this.implementationTime = implementationTime;
    }
    
    public double getConfidenceScore() {
        return confidenceScore;
    }
    
    public void setConfidenceScore(double confidenceScore) {
        this.confidenceScore = confidenceScore;
    }
    
    // Builder pattern
    public static class Builder {
        private ProactiveRecommendation recommendation = new ProactiveRecommendation();
        
        public Builder issueType(String issueType) {
            recommendation.issueType = issueType;
            return this;
        }
        
        public Builder severity(String severity) {
            recommendation.severity = severity;
            return this;
        }
        
        public Builder recommendedAction(String recommendedAction) {
            recommendation.recommendedAction = recommendedAction;
            return this;
        }
        
        public Builder estimatedImpact(String estimatedImpact) {
            recommendation.estimatedImpact = estimatedImpact;
            return this;
        }
        
        public Builder implementationTime(String implementationTime) {
            recommendation.implementationTime = implementationTime;
            return this;
        }
        
        public Builder confidenceScore(double confidenceScore) {
            recommendation.confidenceScore = confidenceScore;
            return this;
        }
        
        public ProactiveRecommendation build() {
            return recommendation;
        }
    }
    
    public static Builder builder() {
        return new Builder();
    }
}
