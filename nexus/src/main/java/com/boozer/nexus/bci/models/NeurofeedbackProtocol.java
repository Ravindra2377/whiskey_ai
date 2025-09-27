package com.boozer.nexus.bci.models;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class NeurofeedbackProtocol {
    private String protocolId = UUID.randomUUID().toString();
    private String protocolName;
    private int numberOfCycles;
    private String feedbackType;
    private Map<String,Object> parameters = new HashMap<>();

    public String getProtocolId() { return protocolId; }
    public String getProtocolName() { return protocolName; }
    public void setProtocolName(String protocolName) { this.protocolName = protocolName; }
    public int getNumberOfCycles() { return numberOfCycles; }
    public void setNumberOfCycles(int numberOfCycles) { this.numberOfCycles = numberOfCycles; }
    public String getFeedbackType() { return feedbackType; }
    public void setFeedbackType(String feedbackType) { this.feedbackType = feedbackType; }
    public Map<String,Object> getParameters() { return new HashMap<>(parameters); }
    public void setParameters(Map<String,Object> parameters) { this.parameters = new HashMap<>(parameters); }
}
