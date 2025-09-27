package com.boozer.nexus.neuromorphic.models;

import java.time.LocalDateTime;
import java.util.List;

public class NeuromorphicResult {
    private String networkId;
    private List<?> inputSpikes;
    private List<?> outputSpikes;
    private Object temporalPattern;
    private long processingTime;
    private Object networkState;
    private boolean successful;
    private LocalDateTime timestamp;

    public String getNetworkId() { return networkId; }
    public void setNetworkId(String networkId) { this.networkId = networkId; }
    public List<?> getInputSpikes() { return inputSpikes; }
    public void setInputSpikes(List<?> inputSpikes) { this.inputSpikes = inputSpikes; }
    public List<?> getOutputSpikes() { return outputSpikes; }
    public void setOutputSpikes(List<?> outputSpikes) { this.outputSpikes = outputSpikes; }
    public Object getTemporalPattern() { return temporalPattern; }
    public void setTemporalPattern(Object temporalPattern) { this.temporalPattern = temporalPattern; }
    public long getProcessingTime() { return processingTime; }
    public void setProcessingTime(long processingTime) { this.processingTime = processingTime; }
    public Object getNetworkState() { return networkState; }
    public void setNetworkState(Object networkState) { this.networkState = networkState; }
    public boolean isSuccessful() { return successful; }
    public void setSuccessful(boolean successful) { this.successful = successful; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}
