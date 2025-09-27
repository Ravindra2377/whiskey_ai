package com.boozer.nexus.consciousness.models;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SelfAwarenessResult {
    private double selfModelConfidence;
    private Map<String,Object> selfAttributes = new HashMap<>();
    private List<String> activeMonitors;
    private LocalDateTime timestamp = LocalDateTime.now();

    public double getSelfModelConfidence() { return selfModelConfidence; }
    public void setSelfModelConfidence(double selfModelConfidence) { this.selfModelConfidence = selfModelConfidence; }
    public Map<String, Object> getSelfAttributes() { return selfAttributes; }
    public void setSelfAttributes(Map<String, Object> selfAttributes) { this.selfAttributes = selfAttributes; }
    public List<String> getActiveMonitors() { return activeMonitors; }
    public void setActiveMonitors(List<String> activeMonitors) { this.activeMonitors = activeMonitors; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}
