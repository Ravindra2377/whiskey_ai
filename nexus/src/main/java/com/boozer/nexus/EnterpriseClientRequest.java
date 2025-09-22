package com.boozer.nexus;

import java.util.List;

public class EnterpriseClientRequest {
    private String clientName;
    private String clientEmail;
    private String clientIndustry;
    private String subscriptionTier;
    private String contactPerson;
    private String companySize;
    private List<String> requiredCapabilities;
    
    // Constructors
    public EnterpriseClientRequest() {}
    
    public EnterpriseClientRequest(String clientName, String clientEmail, String clientIndustry, 
                                 String subscriptionTier, String contactPerson, String companySize, 
                                 List<String> requiredCapabilities) {
        this.clientName = clientName;
        this.clientEmail = clientEmail;
        this.clientIndustry = clientIndustry;
        this.subscriptionTier = subscriptionTier;
        this.contactPerson = contactPerson;
        this.companySize = companySize;
        this.requiredCapabilities = requiredCapabilities;
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
    
    public List<String> getRequiredCapabilities() {
        return requiredCapabilities;
    }
    
    public void setRequiredCapabilities(List<String> requiredCapabilities) {
        this.requiredCapabilities = requiredCapabilities;
    }
}
