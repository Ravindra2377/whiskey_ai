package com.boozer.nexus.bci.models;

import java.util.*;

public class NeuralSignalData {
    private final String signalId;
    private final double[] signalData;
    private final String[] electrodeLabels;
    private final double samplingRate;
    private final long timestamp;
    private final Map<String, Double> signalQualityMetrics;

    public NeuralSignalData(double[] signalData, String[] electrodeLabels, double samplingRate) {
        this.signalId = UUID.randomUUID().toString();
        this.signalData = Arrays.copyOf(signalData, signalData.length);
        this.electrodeLabels = Arrays.copyOf(electrodeLabels, electrodeLabels.length);
        this.samplingRate = samplingRate;
        this.timestamp = System.currentTimeMillis();
        this.signalQualityMetrics = new HashMap<>();
        calculateQualityMetrics();
    }

    private void calculateQualityMetrics() {
        double snr = calculateSNR(signalData);
        signalQualityMetrics.put("snr", snr);
        double impedance = estimateImpedance(signalData);
        signalQualityMetrics.put("impedance", impedance);
        double artifactLevel = detectArtifacts(signalData);
        signalQualityMetrics.put("artifactLevel", artifactLevel);
    }

    private double calculateSNR(double[] signal) {
        double signalPower = Arrays.stream(signal).map(x -> x * x).average().orElse(0.0);
        double noisePower = estimateNoisePower(signal);
        return 10 * Math.log10(signalPower / noisePower);
    }

    private double estimateImpedance(double[] signal) { return 5000.0; }
    private double detectArtifacts(double[] signal) { return 0.1; }
    private double estimateNoisePower(double[] signal) { return 0.01; }

    public String getSignalId() { return signalId; }
    public double[] getSignalData() { return Arrays.copyOf(signalData, signalData.length); }
    public String[] getElectrodeLabels() { return Arrays.copyOf(electrodeLabels, electrodeLabels.length); }
    public double getSamplingRate() { return samplingRate; }
    public long getTimestamp() { return timestamp; }
    public Map<String, Double> getSignalQualityMetrics() { return new HashMap<>(signalQualityMetrics); }
}
