package com.boozer.nexus.bci.models;

public class FeedbackPreferences {
    private FeedbackType feedbackType = FeedbackType.VISUAL;
    private int intensity = 50;
    private boolean adaptiveFeedback = true;

    public FeedbackType getFeedbackType() { return feedbackType; }
    public void setFeedbackType(FeedbackType feedbackType) { this.feedbackType = feedbackType; }
    public int getIntensity() { return intensity; }
    public void setIntensity(int intensity) { this.intensity = intensity; }
    public boolean isAdaptiveFeedback() { return adaptiveFeedback; }
    public void setAdaptiveFeedback(boolean adaptiveFeedback) { this.adaptiveFeedback = adaptiveFeedback; }
}
