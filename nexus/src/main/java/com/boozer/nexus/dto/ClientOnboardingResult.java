package com.boozer.nexus.dto;

import java.util.List;

public class ClientOnboardingResult {
    private String tenantId;
    private List<String> deployedCapabilities;
    private String dashboardUrl;
    private String status;
    private String message;
    
    // Constructors
    public ClientOnboardingResult() {}
    
    public ClientOnboardingResult(String tenantId, List<String> deployedCapabilities, String dashboardUrl) {
        this.tenantId = tenantId;
        this.deployedCapabilities = deployedCapabilities;
        this.dashboardUrl = dashboardUrl;
        this.status = "SUCCESS";
    }
    
    // Getters and Setters
    public String getTenantId() {
        return tenantId;
    }
    
    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }
    
    public List<String> getDeployedCapabilities() {
        return deployedCapabilities;
    }
    
    public void setDeployedCapabilities(List<String> deployedCapabilities) {
        this.deployedCapabilities = deployedCapabilities;
    }
    
    public String getDashboardUrl() {
        return dashboardUrl;
    }
    
    public void setDashboardUrl(String dashboardUrl) {
        this.dashboardUrl = dashboardUrl;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
}
