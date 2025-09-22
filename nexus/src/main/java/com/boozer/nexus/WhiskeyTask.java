package com.boozer.nexus;

import java.util.Map;

public class WhiskeyTask {
    private TaskType type;
    private String description;
    private Map<String, Object> parameters;
    private String createdBy;
    private long createdAt;
    private String id; // Add ID field
    
    public WhiskeyTask() {
        this.createdAt = System.currentTimeMillis();
        this.id = String.valueOf(System.currentTimeMillis()); // Generate ID based on timestamp
    }
    
    public WhiskeyTask(TaskType type, String description, Map<String, Object> parameters) {
        this();
        this.type = type;
        this.description = description;
        this.parameters = parameters;
    }
    
    // Enums
    public enum TaskType {
        CODE_MODIFICATION,
        CI_CD_OPERATION,
        INFRASTRUCTURE_OPERATION,
        MONITORING_OPERATION,
        FEATURE_DEVELOPMENT,
        BUG_FIX,
        SECURITY_PATCH,
        PERFORMANCE_OPTIMIZATION,
        DATABASE_MIGRATION,
        AUTONOMOUS_MAINTENANCE
    }
    
    // Getters and setters
    public TaskType getType() { return type; }
    public void setType(TaskType type) { this.type = type; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public Map<String, Object> getParameters() { return parameters; }
    public void setParameters(Map<String, Object> parameters) { this.parameters = parameters; }
    
    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
    
    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }
    
    // Add the missing getId() method
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
}
