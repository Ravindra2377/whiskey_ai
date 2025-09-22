package com.boozer.nexus.dto;

import java.util.List;

public class ClientOnboardingRequest {
    private String clientName;
    private String clientEmail;
    private String clientIndustry;
    private List<String> requiredCapabilities;
    private String subscriptionTier;
    private String contactPerson;
    private String companySize;
    
    // Constructors
    public ClientOnboardingRequest() {}
    
    public ClientOnboardingRequest(String clientName, String clientEmail, String clientIndustry, 
                                 List<String> requiredCapabilities, String subscriptionTier, 
                                 String contactPerson, String companySize) {
        this.clientName = clientName;
        this.clientEmail = clientEmail;
        this.clientIndustry = clientIndustry;
        this.requiredCapabilities = requiredCapabilities;
        this.subscriptionTier = subscriptionTier;
        this.contactPerson = contactPerson;
        this.companySize = companySize;
    }
    
    // Getters and Setters
    public String getClientName() {
        return clientName;
    }
    
    public void setClientName(String clientName) {
        this.clientName = clientName;
    }
    
    public String getClientEmail() {
        return clientEmail;
    }
    
    public void setClientEmail(String clientEmail) {
        this.clientEmail = clientEmail;
    }
    
    public String getClientIndustry() {
        return clientIndustry;
    }
    
    public void setClientIndustry(String clientIndustry) {
        this.clientIndustry = clientIndustry;
    }
    
    public List<String> getRequiredCapabilities() {
        return requiredCapabilities;
    }
    
    public void setRequiredCapabilities(List<String> requiredCapabilities) {
        this.requiredCapabilities = requiredCapabilities;
    }
    
    public String getSubscriptionTier() {
        return subscriptionTier;
    }
    
    public void setSubscriptionTier(String subscriptionTier) {
        this.subscriptionTier = subscriptionTier;
    }
    
    public String getContactPerson() {
        return contactPerson;
    }
    
    public void setContactPerson(String contactPerson) {
        this.contactPerson = contactPerson;
    }
    
    public String getCompanySize() {
        return companySize;
    }
    
    public void setCompanySize(String companySize) {
        this.companySize = companySize;
    }
}
