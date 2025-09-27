package com.boozer.nexus.bci.models;

public class TimeFeatures {
    private double mean;
    private double variance;
    private double skewness;
    private double kurtosis;
    private double rms;

    public double getMean() { return mean; }
    public void setMean(double mean) { this.mean = mean; }
    public double getVariance() { return variance; }
    public void setVariance(double variance) { this.variance = variance; }
    public double getSkewness() { return skewness; }
    public void setSkewness(double skewness) { this.skewness = skewness; }
    public double getKurtosis() { return kurtosis; }
    public void setKurtosis(double kurtosis) { this.kurtosis = kurtosis; }
    public double getRms() { return rms; }
    public void setRms(double rms) { this.rms = rms; }
}
