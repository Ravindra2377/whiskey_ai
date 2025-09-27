package com.boozer.nexus.bci.models;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class MotorCalibration {
    private Map<String, Double> axisCalibration = new HashMap<>();
    private double sensitivity = 1.0;
    private LocalDateTime calibratedAt = LocalDateTime.now();

    public Map<String, Double> getAxisCalibration() { return new HashMap<>(axisCalibration); }
    public void setAxisCalibration(Map<String, Double> axisCalibration) { this.axisCalibration = new HashMap<>(axisCalibration); }
    public double getSensitivity() { return sensitivity; }
    public void setSensitivity(double sensitivity) { this.sensitivity = sensitivity; }
    public LocalDateTime getCalibratedAt() { return calibratedAt; }
    public void setCalibratedAt(LocalDateTime calibratedAt) { this.calibratedAt = calibratedAt; }
}
