package com.boozer.nexus.bci.models;

public class BCICommandResult {
    private String sessionId;
    private String commandId;
    private CommandExecutionResult executionResult;
    private boolean successful;
    private String errorMessage;
    private boolean feedbackProvided;

    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }
    public String getCommandId() { return commandId; }
    public void setCommandId(String commandId) { this.commandId = commandId; }
    public CommandExecutionResult getExecutionResult() { return executionResult; }
    public void setExecutionResult(CommandExecutionResult executionResult) { this.executionResult = executionResult; }
    public boolean isSuccessful() { return successful; }
    public void setSuccessful(boolean successful) { this.successful = successful; }
    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
    public boolean isFeedbackProvided() { return feedbackProvided; }
    public void setFeedbackProvided(boolean feedbackProvided) { this.feedbackProvided = feedbackProvided; }
}
