package com.boozer.nexus.bci.models;

import java.time.LocalDateTime;

public class ProcessedSignal {
    private NeuralSignalData originalSignal;
    private FrequencyFeatures frequencyFeatures;
    private TimeFeatures timeFeatures;
    private SpatialFeatures spatialFeatures;
    private LocalDateTime timestamp;

    public NeuralSignalData getOriginalSignal() { return originalSignal; }
    public void setOriginalSignal(NeuralSignalData originalSignal) { this.originalSignal = originalSignal; }
    public FrequencyFeatures getFrequencyFeatures() { return frequencyFeatures; }
    public void setFrequencyFeatures(FrequencyFeatures frequencyFeatures) { this.frequencyFeatures = frequencyFeatures; }
    public TimeFeatures getTimeFeatures() { return timeFeatures; }
    public void setTimeFeatures(TimeFeatures timeFeatures) { this.timeFeatures = timeFeatures; }
    public SpatialFeatures getSpatialFeatures() { return spatialFeatures; }
    public void setSpatialFeatures(SpatialFeatures spatialFeatures) { this.spatialFeatures = spatialFeatures; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}
