package com.boozer.nexus.voice.processing;

import javax.sound.sampled.AudioFormat;

/**
 * Abstraction for speech-to-text providers used by the advanced voice pipeline.
 */
public interface SpeechRecognitionClient {

    /**
     * Human readable provider name (e.g. "whisper", "gcp", "azure").
     */
    String name();

    /**
     * @return true when the underlying provider can accept requests (API keys configured, etc.).
     */
    default boolean isAvailable() {
        return true;
    }

    /**
     * Execute transcription against the provider.
     *
     * @param audio  raw audio samples (typically PCM 16-bit little endian)
     * @param format audio format metadata
     * @return structured transcription result (never {@code null})
     */
    TranscriptionResult transcribe(byte[] audio, AudioFormat format);
}
