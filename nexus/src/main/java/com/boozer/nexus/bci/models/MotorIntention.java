package com.boozer.nexus.bci.models;

import java.time.LocalDateTime;

public class MotorIntention {
    private MovementVector movementVector;
    private String action;
    private double confidence;
    private LocalDateTime timestamp;

    public MovementVector getMovementVector() { return movementVector; }
    public void setMovementVector(MovementVector movementVector) { this.movementVector = movementVector; }
    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }
    public double getConfidence() { return confidence; }
    public void setConfidence(double confidence) { this.confidence = confidence; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}
