package com.boozer.nexus.voice.processing;

import java.time.Duration;
import java.time.Instant;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;

/**
 * Canonical representation of a speech recognition outcome.
 */
public final class TranscriptionResult {
    private final String provider;
    private final String text;
    private final double confidence;
    private final Instant completedAt;
    private final Duration latency;
    private final boolean success;
    private final String error;
    private final Map<String, Object> metadata;

    private TranscriptionResult(Builder builder) {
        this.provider = builder.provider;
        this.text = builder.text;
        this.confidence = builder.confidence;
        this.completedAt = builder.completedAt;
        this.latency = builder.latency;
        this.success = builder.success;
        this.error = builder.error;
        this.metadata = builder.metadata == null ? Collections.emptyMap() : Collections.unmodifiableMap(builder.metadata);
    }

    public String getProvider() {
        return provider;
    }

    public String getText() {
        return text;
    }

    public double getConfidence() {
        return confidence;
    }

    public Instant getCompletedAt() {
        return completedAt;
    }

    public Duration getLatency() {
        return latency;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getError() {
        return error;
    }

    public Map<String, Object> getMetadata() {
        return metadata;
    }

    public static Builder builder(String provider) {
        return new Builder(provider);
    }

    public static TranscriptionResult success(String provider, String text, double confidence) {
        return builder(provider)
                .text(text)
                .confidence(confidence)
                .success(true)
                .build();
    }

    public static TranscriptionResult failure(String provider, String error) {
        return builder(provider)
                .success(false)
                .error(error)
                .confidence(0.0)
                .build();
    }

    public static final class Builder {
        private final String provider;
        private String text = "";
        private double confidence = 0.0;
        private Instant completedAt = Instant.now();
        private Duration latency = Duration.ZERO;
        private boolean success = true;
        private String error;
        private Map<String, Object> metadata;

        private Builder(String provider) {
            this.provider = Objects.requireNonNull(provider, "provider");
        }

        public Builder text(String text) {
            this.text = text == null ? "" : text;
            return this;
        }

        public Builder confidence(double confidence) {
            this.confidence = confidence;
            return this;
        }

        public Builder completedAt(Instant completedAt) {
            this.completedAt = completedAt == null ? Instant.now() : completedAt;
            return this;
        }

        public Builder latency(Duration latency) {
            this.latency = latency == null ? Duration.ZERO : latency;
            return this;
        }

        public Builder success(boolean success) {
            this.success = success;
            return this;
        }

        public Builder error(String error) {
            this.error = error;
            return this;
        }

        public Builder metadata(Map<String, Object> metadata) {
            this.metadata = metadata;
            return this;
        }

        public TranscriptionResult build() {
            return new TranscriptionResult(this);
        }
    }
}
