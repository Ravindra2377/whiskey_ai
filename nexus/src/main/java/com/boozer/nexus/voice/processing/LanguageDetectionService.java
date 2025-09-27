package com.boozer.nexus.voice.processing;

/**
 * Lightweight abstraction over language detection.
 */
public interface LanguageDetectionService {

    LanguageContext detect(String text);
}
