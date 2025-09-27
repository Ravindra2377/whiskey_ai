package com.boozer.nexus.bci.models;

import java.time.LocalDateTime;

public class BCISystemStatus {
    private int activeSessionCount;
    private double systemHealth;
    private double averageLatency;
    private long totalSignalsProcessed;
    private long uptimeHours;
    private LocalDateTime lastUpdate;

    public int getActiveSessionCount() { return activeSessionCount; }
    public void setActiveSessionCount(int activeSessionCount) { this.activeSessionCount = activeSessionCount; }
    public double getSystemHealth() { return systemHealth; }
    public void setSystemHealth(double systemHealth) { this.systemHealth = systemHealth; }
    public double getAverageLatency() { return averageLatency; }
    public void setAverageLatency(double averageLatency) { this.averageLatency = averageLatency; }
    public long getTotalSignalsProcessed() { return totalSignalsProcessed; }
    public void setTotalSignalsProcessed(long totalSignalsProcessed) { this.totalSignalsProcessed = totalSignalsProcessed; }
    public long getUptimeHours() { return uptimeHours; }
    public void setUptimeHours(long uptimeHours) { this.uptimeHours = uptimeHours; }
    public LocalDateTime getLastUpdate() { return lastUpdate; }
    public void setLastUpdate(LocalDateTime lastUpdate) { this.lastUpdate = lastUpdate; }
}
