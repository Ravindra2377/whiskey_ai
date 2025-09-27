package com.boozer.nexus.voice.processing;

/**
 * Result from the voice activity detector.
 */
public final class VoiceActivityResult {
    private final boolean hasVoice;
    private final double confidence;

    private VoiceActivityResult(boolean hasVoice, double confidence) {
        this.hasVoice = hasVoice;
        this.confidence = confidence;
    }

    public static VoiceActivityResult voiceDetected(double confidence) {
        return new VoiceActivityResult(true, confidence);
    }

    public static VoiceActivityResult noVoice() {
        return new VoiceActivityResult(false, 0.0);
    }

    public boolean hasVoice() {
        return hasVoice;
    }

    public double getConfidence() {
        return confidence;
    }
}
