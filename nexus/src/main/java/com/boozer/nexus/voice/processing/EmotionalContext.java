package com.boozer.nexus.voice.processing;

/**
 * Simple sentiment/emotion wrapper used by the response pipeline.
 */
public final class EmotionalContext {
    private final String label;
    private final double confidence;

    private EmotionalContext(String label, double confidence) {
        this.label = label;
        this.confidence = confidence;
    }

    public static EmotionalContext neutral() {
        return new EmotionalContext("neutral", 0.0);
    }

    public static EmotionalContext of(String label, double confidence) {
        return new EmotionalContext(label == null ? "neutral" : label, confidence);
    }

    public String getLabel() {
        return label;
    }

    public double getConfidence() {
        return confidence;
    }
}
