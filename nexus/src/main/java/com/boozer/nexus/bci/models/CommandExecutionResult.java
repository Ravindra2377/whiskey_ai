package com.boozer.nexus.bci.models;

import java.time.LocalDateTime;

public class CommandExecutionResult {
    private String commandId;
    private boolean successful;
    private String message;
    private LocalDateTime executionTime;

    public String getCommandId() { return commandId; }
    public void setCommandId(String commandId) { this.commandId = commandId; }
    public boolean isSuccessful() { return successful; }
    public void setSuccessful(boolean successful) { this.successful = successful; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public LocalDateTime getExecutionTime() { return executionTime; }
    public void setExecutionTime(LocalDateTime executionTime) { this.executionTime = executionTime; }
}
