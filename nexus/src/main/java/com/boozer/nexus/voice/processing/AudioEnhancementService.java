package com.boozer.nexus.voice.processing;

import javax.sound.sampled.AudioFormat;

/**
 * Provides noise reduction, normalization, and related DSP enhancements.
 */
public interface AudioEnhancementService {

    byte[] enhance(byte[] audio, AudioFormat format);
}
