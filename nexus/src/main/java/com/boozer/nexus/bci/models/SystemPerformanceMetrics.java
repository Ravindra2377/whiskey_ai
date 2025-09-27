package com.boozer.nexus.bci.models;

public class SystemPerformanceMetrics {
    private double averageLatency;
    private double averageAccuracy;

    public double getAverageLatency() { return averageLatency; }
    public void setAverageLatency(double averageLatency) { this.averageLatency = averageLatency; }
    public double getAverageAccuracy() { return averageAccuracy; }
    public void setAverageAccuracy(double averageAccuracy) { this.averageAccuracy = averageAccuracy; }
}
