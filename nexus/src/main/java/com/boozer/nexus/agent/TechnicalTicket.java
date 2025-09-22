package com.boozer.nexus.agent;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class TechnicalTicket {
    private String ticketId;
    private String clientId;
    private String description;
    private String priority;
    private String status;
    private Map<String, Object> metadata;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Multi-modal fields
    private List<String> codeSnippets;
    private List<String> screenshots;
    private List<String> logFiles;
    private List<String> configFiles;
    
    // Constructors
    public TechnicalTicket() {}
    
    public TechnicalTicket(String ticketId, String clientId, String description, String priority, 
                          String status, Map<String, Object> metadata) {
        this.ticketId = ticketId;
        this.clientId = clientId;
        this.description = description;
        this.priority = priority;
        this.status = status;
        this.metadata = metadata;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    // Getters and Setters
    public String getTicketId() {
        return ticketId;
    }
    
    public void setTicketId(String ticketId) {
        this.ticketId = ticketId;
    }
    
    public String getClientId() {
        return clientId;
    }
    
    public void setClientId(String clientId) {
        this.clientId = clientId;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getPriority() {
        return priority;
    }
    
    public void setPriority(String priority) {
        this.priority = priority;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public Map<String, Object> getMetadata() {
        return metadata;
    }
    
    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    // Multi-modal getters and setters
    public List<String> getCodeSnippets() {
        return codeSnippets;
    }
    
    public void setCodeSnippets(List<String> codeSnippets) {
        this.codeSnippets = codeSnippets;
    }
    
    public List<String> getScreenshots() {
        return screenshots;
    }
    
    public void setScreenshots(List<String> screenshots) {
        this.screenshots = screenshots;
    }
    
    public List<String> getLogFiles() {
        return logFiles;
    }
    
    public void setLogFiles(List<String> logFiles) {
        this.logFiles = logFiles;
    }
    
    public List<String> getConfigFiles() {
        return configFiles;
    }
    
    public void setConfigFiles(List<String> configFiles) {
        this.configFiles = configFiles;
    }
}
