package com.boozer.nexus.dto;

import com.boozer.nexus.WhiskeyTask;

import java.util.Map;

public class TaskRequestDTO {
    private WhiskeyTask.TaskType type;
    private String description;
    private Map<String, Object> parameters;
    private String createdBy;
    
    // Getters and setters
    public WhiskeyTask.TaskType getType() {
        return type;
    }
    
    public void setType(WhiskeyTask.TaskType type) {
        this.type = type;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public Map<String, Object> getParameters() {
        return parameters;
    }
    
    public void setParameters(Map<String, Object> parameters) {
        this.parameters = parameters;
    }
    
    public String getCreatedBy() {
        return createdBy;
    }
    
    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }
}
