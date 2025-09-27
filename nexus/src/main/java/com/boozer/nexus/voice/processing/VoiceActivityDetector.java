package com.boozer.nexus.voice.processing;

import javax.sound.sampled.AudioFormat;

/**
 * Determines whether meaningful speech is present in an audio buffer.
 */
public interface VoiceActivityDetector {

    VoiceActivityResult detect(byte[] audio, AudioFormat format);
}
