package com.boozer.nexus.enhanced;

import java.util.Map;
import java.util.HashMap;

public class EnhancedPolicyValidation {
    private boolean approved;
    private String validationMessage;
    private double confidenceScore;
    private Map<String, Object> validationDetails;
    private String policyType;
    private long validationTimestamp;

    public EnhancedPolicyValidation() {
        this.validationDetails = new HashMap<>();
        this.validationTimestamp = System.currentTimeMillis();
    }

    public EnhancedPolicyValidation(boolean approved, String validationMessage) {
        this();
        this.approved = approved;
        this.validationMessage = validationMessage;
    }

    // Getters and setters
    public boolean isApproved() {
        return approved;
    }

    public void setApproved(boolean approved) {
        this.approved = approved;
    }

    public String getValidationMessage() {
        return validationMessage;
    }

    public void setValidationMessage(String validationMessage) {
        this.validationMessage = validationMessage;
    }

    public double getConfidenceScore() {
        return confidenceScore;
    }

    public void setConfidenceScore(double confidenceScore) {
        this.confidenceScore = confidenceScore;
    }

    public Map<String, Object> getValidationDetails() {
        return validationDetails;
    }

    public void setValidationDetails(Map<String, Object> validationDetails) {
        this.validationDetails = validationDetails;
    }

    public String getPolicyType() {
        return policyType;
    }

    public void setPolicyType(String policyType) {
        this.policyType = policyType;
    }

    public long getValidationTimestamp() {
        return validationTimestamp;
    }

    public void setValidationTimestamp(long validationTimestamp) {
        this.validationTimestamp = validationTimestamp;
    }
}
