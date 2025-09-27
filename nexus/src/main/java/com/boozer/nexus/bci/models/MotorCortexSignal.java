package com.boozer.nexus.bci.models;

public class MotorCortexSignal {
    private NeuralSignalData sourceSignal;
    private double[] motorBandSignal;
    private double[] movementPotentials;

    public NeuralSignalData getSourceSignal() { return sourceSignal; }
    public void setSourceSignal(NeuralSignalData sourceSignal) { this.sourceSignal = sourceSignal; }
    public double[] getMotorBandSignal() { return motorBandSignal; }
    public void setMotorBandSignal(double[] motorBandSignal) { this.motorBandSignal = motorBandSignal; }
    public double[] getMovementPotentials() { return movementPotentials; }
    public void setMovementPotentials(double[] movementPotentials) { this.movementPotentials = movementPotentials; }
}
