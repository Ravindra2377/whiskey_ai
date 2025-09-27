package com.boozer.nexus.voice.processing;

import java.util.Locale;

/**
 * Represents detected language metadata for a transcription.
 */
public final class LanguageContext {
    private final Locale locale;
    private final double confidence;

    private LanguageContext(Locale locale, double confidence) {
        this.locale = locale;
        this.confidence = confidence;
    }

    public static LanguageContext unknown() {
        return new LanguageContext(Locale.ROOT, 0.0);
    }

    public static LanguageContext of(Locale locale, double confidence) {
        return new LanguageContext(locale == null ? Locale.ROOT : locale, confidence);
    }

    public Locale getLocale() {
        return locale;
    }

    public double getConfidence() {
        return confidence;
    }
}
