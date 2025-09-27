package com.boozer.nexus.bci.models;

public class BCIMetrics {
    private long totalSignalsProcessed;
    private double averageLatency;
    private double averageSignalQuality;
    private int commandsExecuted;
    private double accuracy;

    public void incrementSignalsProcessed() { totalSignalsProcessed++; }
    public void updateLatency(long latency) { averageLatency = (averageLatency + latency) / 2.0; }
    public void updateSignalQuality(double quality) { averageSignalQuality = (averageSignalQuality + quality) / 2.0; }

    public long getTotalSignalsProcessed() { return totalSignalsProcessed; }
    public double getAverageLatency() { return averageLatency; }
    public double getAverageSignalQuality() { return averageSignalQuality; }
    public int getCommandsExecuted() { return commandsExecuted; }
    public void setCommandsExecuted(int commandsExecuted) { this.commandsExecuted = commandsExecuted; }
    public double getAccuracy() { return accuracy; }
    public void setAccuracy(double accuracy) { this.accuracy = accuracy; }
}
