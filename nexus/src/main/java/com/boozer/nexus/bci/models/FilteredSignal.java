package com.boozer.nexus.bci.models;

import java.time.LocalDateTime;
import java.util.Arrays;

public class FilteredSignal {
    private ProcessedSignal originalSignal;
    private double[] filteredData;
    private double noiseLevel;
    private LocalDateTime timestamp;

    public ProcessedSignal getOriginalSignal() { return originalSignal; }
    public void setOriginalSignal(ProcessedSignal originalSignal) { this.originalSignal = originalSignal; }
    public double[] getFilteredData() { return filteredData != null ? Arrays.copyOf(filteredData, filteredData.length) : null; }
    public void setFilteredData(double[] filteredData) { this.filteredData = filteredData != null ? Arrays.copyOf(filteredData, filteredData.length) : null; }
    public double getNoiseLevel() { return noiseLevel; }
    public void setNoiseLevel(double noiseLevel) { this.noiseLevel = noiseLevel; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}
