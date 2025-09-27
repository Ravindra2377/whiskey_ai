package com.boozer.nexus.bci.models;

public class NeurofeedbackResult {
    private String sessionId;
    private String protocolId;
    private NeurofeedbackAnalysis trainingAnalysis;
    private double improvementScore;
    private String recommendedNextSession;
    private boolean successful;
    private String errorMessage;

    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }
    public String getProtocolId() { return protocolId; }
    public void setProtocolId(String protocolId) { this.protocolId = protocolId; }
    public NeurofeedbackAnalysis getTrainingAnalysis() { return trainingAnalysis; }
    public void setTrainingAnalysis(NeurofeedbackAnalysis trainingAnalysis) { this.trainingAnalysis = trainingAnalysis; }
    public double getImprovementScore() { return improvementScore; }
    public void setImprovementScore(double improvementScore) { this.improvementScore = improvementScore; }
    public String getRecommendedNextSession() { return recommendedNextSession; }
    public void setRecommendedNextSession(String recommendedNextSession) { this.recommendedNextSession = recommendedNextSession; }
    public boolean isSuccessful() { return successful; }
    public void setSuccessful(boolean successful) { this.successful = successful; }
    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
}
