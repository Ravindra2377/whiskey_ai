package com.boozer.nexus;

public class AIAgent {
    private String agentId;
    private String agentType;
    private String capabilities;
    private String status;
    
    // Constructors
    public AIAgent() {}
    
    public AIAgent(String agentId, String agentType, String capabilities, String status) {
        this.agentId = agentId;
        this.agentType = agentType;
        this.capabilities = capabilities;
        this.status = status;
    }
    
    // Getters and Setters
    public String getAgentId() {
        return agentId;
    }
    
    public void setAgentId(String agentId) {
        this.agentId = agentId;
    }
    
    public String getAgentType() {
        return agentType;
    }
    
    public void setAgentType(String agentType) {
        this.agentType = agentType;
    }
    
    public String getCapabilities() {
        return capabilities;
    }
    
    public void setCapabilities(String capabilities) {
        this.capabilities = capabilities;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
}
