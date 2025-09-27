package com.boozer.nexus.voice.processing;

import javax.sound.sampled.AudioFormat;

/**
 * Estimates the emotional tone for the captured utterance.
 */
public interface EmotionAnalysisService {

    EmotionalContext analyze(byte[] audio, AudioFormat format);
}
