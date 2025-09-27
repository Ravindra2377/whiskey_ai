package com.boozer.nexus.voice.processing;

import java.util.Objects;
import java.util.UUID;

/**
 * Minimal representation of an identified speaker.
 */
public final class SpeakerProfile {
    private final String speakerId;
    private final double confidence;

    private SpeakerProfile(String speakerId, double confidence) {
        this.speakerId = speakerId;
        this.confidence = confidence;
    }

    public static SpeakerProfile unknown() {
        return new SpeakerProfile("unknown", 0.0);
    }

    public static SpeakerProfile of(String speakerId, double confidence) {
        return new SpeakerProfile(Objects.requireNonNullElse(speakerId, UUID.randomUUID().toString()), confidence);
    }

    public String getSpeakerId() {
        return speakerId;
    }

    public double getConfidence() {
        return confidence;
    }
}
