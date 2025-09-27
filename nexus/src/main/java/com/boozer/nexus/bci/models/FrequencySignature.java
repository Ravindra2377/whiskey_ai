package com.boozer.nexus.bci.models;

public class FrequencySignature {
    private double dominantFrequency;
    private double peakAmplitude;
    private double bandwidth;

    public double getDominantFrequency() { return dominantFrequency; }
    public void setDominantFrequency(double dominantFrequency) { this.dominantFrequency = dominantFrequency; }
    public double getPeakAmplitude() { return peakAmplitude; }
    public void setPeakAmplitude(double peakAmplitude) { this.peakAmplitude = peakAmplitude; }
    public double getBandwidth() { return bandwidth; }
    public void setBandwidth(double bandwidth) { this.bandwidth = bandwidth; }
}
