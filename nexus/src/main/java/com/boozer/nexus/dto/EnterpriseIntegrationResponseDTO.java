package com.boozer.nexus.dto;

import java.util.Map;

public class EnterpriseIntegrationResponseDTO {
    private String status;
    private String message;
    private String clientId;
    private String discoveryId;
    private int systemsDiscovered;
    private int connectionsConfigured;
    private int agentsDeployed;
    private Map<String, Object> details;
    
    public EnterpriseIntegrationResponseDTO() {
    }
    
    public EnterpriseIntegrationResponseDTO(String status, String message) {
        this.status = status;
        this.message = message;
    }
    
    // Getters and setters
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
    
    public String getClientId() {
        return clientId;
    }
    
    public void setClientId(String clientId) {
        this.clientId = clientId;
    }
    
    public String getDiscoveryId() {
        return discoveryId;
    }
    
    public void setDiscoveryId(String discoveryId) {
        this.discoveryId = discoveryId;
    }
    
    public int getSystemsDiscovered() {
        return systemsDiscovered;
    }
    
    public void setSystemsDiscovered(int systemsDiscovered) {
        this.systemsDiscovered = systemsDiscovered;
    }
    
    public int getConnectionsConfigured() {
        return connectionsConfigured;
    }
    
    public void setConnectionsConfigured(int connectionsConfigured) {
        this.connectionsConfigured = connectionsConfigured;
    }
    
    public int getAgentsDeployed() {
        return agentsDeployed;
    }
    
    public void setAgentsDeployed(int agentsDeployed) {
        this.agentsDeployed = agentsDeployed;
    }
    
    public Map<String, Object> getDetails() {
        return details;
    }
    
    public void setDetails(Map<String, Object> details) {
        this.details = details;
    }
}
