package com.boozer.nexus.bci.models;

import java.util.Arrays;

public class SpatialFeatures {
    private double[] cspFeatures;
    private double[] laplacianFeatures;

    public double[] getCspFeatures() { return cspFeatures != null ? Arrays.copyOf(cspFeatures, cspFeatures.length) : null; }
    public void setCspFeatures(double[] cspFeatures) { this.cspFeatures = cspFeatures != null ? Arrays.copyOf(cspFeatures, cspFeatures.length) : null; }
    public double[] getLaplacianFeatures() { return laplacianFeatures != null ? Arrays.copyOf(laplacianFeatures, laplacianFeatures.length) : null; }
    public void setLaplacianFeatures(double[] laplacianFeatures) { this.laplacianFeatures = laplacianFeatures != null ? Arrays.copyOf(laplacianFeatures, laplacianFeatures.length) : null; }
}
