package com.boozer.nexus.voice.processing;

import java.util.Optional;

/**
 * Aggregate output from the advanced voice processing pipeline.
 */
public final class VoiceProcessingResult {
    private final boolean voiceDetected;
    private final TranscriptionResult transcription;
    private final SpeakerProfile speakerProfile;
    private final EmotionalContext emotionalContext;
    private final LanguageContext languageContext;
    private final double overallConfidence;

    private VoiceProcessingResult(Builder builder) {
        this.voiceDetected = builder.voiceDetected;
        this.transcription = builder.transcription;
        this.speakerProfile = builder.speakerProfile;
        this.emotionalContext = builder.emotionalContext;
        this.languageContext = builder.languageContext;
        this.overallConfidence = builder.overallConfidence;
    }

    public static VoiceProcessingResult noVoice() {
        return builder().voiceDetected(false).overallConfidence(0.0).build();
    }

    public boolean isVoiceDetected() {
        return voiceDetected;
    }

    public Optional<TranscriptionResult> getTranscription() {
        return Optional.ofNullable(transcription);
    }

    public SpeakerProfile getSpeakerProfile() {
        return speakerProfile;
    }

    public EmotionalContext getEmotionalContext() {
        return emotionalContext;
    }

    public LanguageContext getLanguageContext() {
        return languageContext;
    }

    public double getOverallConfidence() {
        return overallConfidence;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private boolean voiceDetected = true;
        private TranscriptionResult transcription;
        private SpeakerProfile speakerProfile = SpeakerProfile.unknown();
        private EmotionalContext emotionalContext = EmotionalContext.neutral();
        private LanguageContext languageContext = LanguageContext.unknown();
        private double overallConfidence = 0.0;

        private Builder() {
        }

        public Builder voiceDetected(boolean voiceDetected) {
            this.voiceDetected = voiceDetected;
            return this;
        }

        public Builder transcription(TranscriptionResult transcription) {
            this.transcription = transcription;
            return this;
        }

        public Builder speakerProfile(SpeakerProfile speakerProfile) {
            this.speakerProfile = speakerProfile == null ? SpeakerProfile.unknown() : speakerProfile;
            return this;
        }

        public Builder emotionalContext(EmotionalContext emotionalContext) {
            this.emotionalContext = emotionalContext == null ? EmotionalContext.neutral() : emotionalContext;
            return this;
        }

        public Builder languageContext(LanguageContext languageContext) {
            this.languageContext = languageContext == null ? LanguageContext.unknown() : languageContext;
            return this;
        }

        public Builder overallConfidence(double overallConfidence) {
            this.overallConfidence = overallConfidence;
            return this;
        }

        public VoiceProcessingResult build() {
            return new VoiceProcessingResult(this);
        }
    }
}
