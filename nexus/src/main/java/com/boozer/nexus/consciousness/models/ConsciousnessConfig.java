package com.boozer.nexus.consciousness.models;

import java.util.HashMap;
import java.util.Map;

public class ConsciousnessConfig {
    private boolean enableEmergence = true;
    private boolean enableMetacognition = true;
    private Map<String, Object> parameters = new HashMap<>();

    public boolean isEnableEmergence() { return enableEmergence; }
    public void setEnableEmergence(boolean enableEmergence) { this.enableEmergence = enableEmergence; }
    public boolean isEnableMetacognition() { return enableMetacognition; }
    public void setEnableMetacognition(boolean enableMetacognition) { this.enableMetacognition = enableMetacognition; }
    public Map<String, Object> getParameters() { return parameters; }
    public void setParameters(Map<String, Object> parameters) { this.parameters = parameters; }
}
