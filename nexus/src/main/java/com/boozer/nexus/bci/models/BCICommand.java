package com.boozer.nexus.bci.models;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BCICommand {
    private final String commandId;
    private final String commandType;
    private final Map<String, Object> parameters;
    private final int safetyLevel;
    private final LocalDateTime timestamp;

    public BCICommand(String commandType, Map<String, Object> parameters, int safetyLevel) {
        this.commandId = UUID.randomUUID().toString();
        this.commandType = commandType;
        this.parameters = parameters != null ? new HashMap<>(parameters) : new HashMap<>();
        this.safetyLevel = safetyLevel;
        this.timestamp = LocalDateTime.now();
    }

    public String getCommandId() { return commandId; }
    public String getCommandType() { return commandType; }
    public Map<String, Object> getParameters() { return new HashMap<>(parameters); }
    public int getSafetyLevel() { return safetyLevel; }
    public LocalDateTime getTimestamp() { return timestamp; }
}
