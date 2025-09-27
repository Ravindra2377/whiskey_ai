package com.boozer.nexus.consciousness.models;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class ConsciousnessState {
    private String stateId;
    private String description;
    private Map<String, Object> attributes = new HashMap<>();
    private LocalDateTime timestamp = LocalDateTime.now();

    public String getStateId() { return stateId; }
    public void setStateId(String stateId) { this.stateId = stateId; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Map<String, Object> getAttributes() { return attributes; }
    public void setAttributes(Map<String, Object> attributes) { this.attributes = attributes; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}
