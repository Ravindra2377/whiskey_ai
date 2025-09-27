package com.boozer.nexus.bci.models;

public class MotorIntentionResult {
    private String sessionId;
    private RefinedMotorIntention motorIntention;
    private double confidence;
    private MovementVector movementVector;
    private String predictedAction;
    private boolean successful;
    private String errorMessage;

    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }
    public RefinedMotorIntention getMotorIntention() { return motorIntention; }
    public void setMotorIntention(RefinedMotorIntention motorIntention) { this.motorIntention = motorIntention; }
    public double getConfidence() { return confidence; }
    public void setConfidence(double confidence) { this.confidence = confidence; }
    public MovementVector getMovementVector() { return movementVector; }
    public void setMovementVector(MovementVector movementVector) { this.movementVector = movementVector; }
    public String getPredictedAction() { return predictedAction; }
    public void setPredictedAction(String predictedAction) { this.predictedAction = predictedAction; }
    public boolean isSuccessful() { return successful; }
    public void setSuccessful(boolean successful) { this.successful = successful; }
    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
}
