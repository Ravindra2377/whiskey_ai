package com.boozer.nexus.consciousness.models;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class ConsciousnessInput {
    private String entityId;
    private String sensoryData;
    private Map<String, Object> context = new HashMap<>();
    private LocalDateTime timestamp = LocalDateTime.now();

    public String getEntityId() { return entityId; }
    public void setEntityId(String entityId) { this.entityId = entityId; }
    public String getSensoryData() { return sensoryData; }
    public void setSensoryData(String sensoryData) { this.sensoryData = sensoryData; }
    public Map<String, Object> getContext() { return context; }
    public void setContext(Map<String, Object> context) { this.context = context; }
    public LocalDateTime getTimestamp() { return timestamp; }
}
