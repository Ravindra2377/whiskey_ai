package com.boozer.nexus.agent;

public class IssueClassification {
    private String category;
    private String subcategory;
    private String severity;
    private double confidence;
    
    // Constructors
    public IssueClassification() {}
    
    public IssueClassification(String category, String subcategory, String severity, double confidence) {
        this.category = category;
        this.subcategory = subcategory;
        this.severity = severity;
        this.confidence = confidence;
    }
    
    // Getters and Setters
    public String getCategory() {
        return category;
    }
    
    public void setCategory(String category) {
        this.category = category;
    }
    
    public String getSubcategory() {
        return subcategory;
    }
    
    public void setSubcategory(String subcategory) {
        this.subcategory = subcategory;
    }
    
    public String getSeverity() {
        return severity;
    }
    
    public void setSeverity(String severity) {
        this.severity = severity;
    }
    
    public double getConfidence() {
        return confidence;
    }
    
    public void setConfidence(double confidence) {
        this.confidence = confidence;
    }
}
