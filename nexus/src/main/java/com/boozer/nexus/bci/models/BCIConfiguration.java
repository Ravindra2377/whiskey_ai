package com.boozer.nexus.bci.models;

import java.util.*;

public class BCIConfiguration {
    private final int bufferSize;
    private final double samplingRate;
    private final String calibrationType;
    private final List<String> electrodePositions;
    private final Map<String, Double> filterSettings;
    private final boolean realTimeProcessing;
    private final int processingDelay;

    public BCIConfiguration() {
        this.bufferSize = 1000;
        this.samplingRate = 256.0; // 256 Hz
        this.calibrationType = "STANDARD";
        this.electrodePositions = Arrays.asList("C3", "C4", "Cz", "F3", "F4", "P3", "P4");
        this.filterSettings = createDefaultFilterSettings();
        this.realTimeProcessing = true;
        this.processingDelay = 50; // 50ms
    }

    private Map<String, Double> createDefaultFilterSettings() {
        Map<String, Double> settings = new HashMap<>();
        settings.put("highPass", 1.0);
        settings.put("lowPass", 50.0);
        settings.put("notch", 50.0);
        return settings;
    }

    public int getBufferSize() { return bufferSize; }
    public double getSamplingRate() { return samplingRate; }
    public String getCalibrationType() { return calibrationType; }
    public List<String> getElectrodePositions() { return new ArrayList<>(electrodePositions); }
    public Map<String, Double> getFilterSettings() { return new HashMap<>(filterSettings); }
    public boolean isRealTimeProcessing() { return realTimeProcessing; }
    public int getProcessingDelay() { return processingDelay; }
}
