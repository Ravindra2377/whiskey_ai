package com.boozer.nexus.agent;

import java.util.List;
import java.util.Map;

public class TechnicalSolution {
    private String solutionType;
    private List<String> steps;
    private Map<String, Object> details;
    private double confidenceScore;
    private String estimatedTime;
    
    // Constructors
    public TechnicalSolution() {}
    
    public TechnicalSolution(String solutionType, List<String> steps, Map<String, Object> details, 
                           double confidenceScore, String estimatedTime) {
        this.solutionType = solutionType;
        this.steps = steps;
        this.details = details;
        this.confidenceScore = confidenceScore;
        this.estimatedTime = estimatedTime;
    }
    
    // Getters and Setters
    public String getSolutionType() {
        return solutionType;
    }
    
    public void setSolutionType(String solutionType) {
        this.solutionType = solutionType;
    }
    
    public List<String> getSteps() {
        return steps;
    }
    
    public void setSteps(List<String> steps) {
        this.steps = steps;
    }
    
    public Map<String, Object> getDetails() {
        return details;
    }
    
    public void setDetails(Map<String, Object> details) {
        this.details = details;
    }
    
    public double getConfidenceScore() {
        return confidenceScore;
    }
    
    public void setConfidenceScore(double confidenceScore) {
        this.confidenceScore = confidenceScore;
    }
    
    public String getEstimatedTime() {
        return estimatedTime;
    }
    
    public void setEstimatedTime(String estimatedTime) {
        this.estimatedTime = estimatedTime;
    }
    
    // Builder pattern
    public static class Builder {
        private TechnicalSolution solution = new TechnicalSolution();
        
        public Builder solutionType(String solutionType) {
            solution.solutionType = solutionType;
            return this;
        }
        
        public Builder steps(List<String> steps) {
            solution.steps = steps;
            return this;
        }
        
        public Builder details(Map<String, Object> details) {
            solution.details = details;
            return this;
        }
        
        public Builder confidenceScore(double confidenceScore) {
            solution.confidenceScore = confidenceScore;
            return this;
        }
        
        public Builder estimatedTime(String estimatedTime) {
            solution.estimatedTime = estimatedTime;
            return this;
        }
        
        public TechnicalSolution build() {
            return solution;
        }
    }
    
    public static Builder builder() {
        return new Builder();
    }
}
