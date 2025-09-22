package com.boozer.nexus.dto;

public class TaskResponseDTO {
    private String status;
    private String message;
    private String taskId;
    private String taskType;
    
    public TaskResponseDTO() {}
    
    public TaskResponseDTO(String status, String message, String taskId, String taskType) {
        this.status = status;
        this.message = message;
        this.taskId = taskId;
        this.taskType = taskType;
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
    
    public String getTaskId() {
        return taskId;
    }
    
    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }
    
    public String getTaskType() {
        return taskType;
    }
    
    public void setTaskType(String taskType) {
        this.taskType = taskType;
    }
}
