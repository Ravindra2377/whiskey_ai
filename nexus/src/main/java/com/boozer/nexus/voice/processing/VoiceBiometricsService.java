package com.boozer.nexus.voice.processing;

import javax.sound.sampled.AudioFormat;

/**
 * Performs speaker identification / verification.
 */
public interface VoiceBiometricsService {

    SpeakerProfile identify(byte[] audio, AudioFormat format);
}
