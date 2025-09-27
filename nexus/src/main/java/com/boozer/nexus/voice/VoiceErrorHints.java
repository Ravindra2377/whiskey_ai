package com.boozer.nexus.voice;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * Maps common voice assistant error messages to actionable remediation tips.
 */
public final class VoiceErrorHints {
    private static final List<HintRule> RULES = new ArrayList<>();

    static {
        RULES.add(new HintRule(
                msg -> contains(msg, "awaiting confirmation"),
                "Say 'yes' to continue or 'no' to cancel the pending action."));
        RULES.add(new HintRule(
                msg -> contains(msg, "blank transcript") || contains(msg, "empty transcript"),
                "The microphone heard silence. Move closer, reduce background noise, or re-run `voice --check-config`."));
        RULES.add(new HintRule(
                msg -> contains(msg, "wake word"),
                "Try pausing briefly after the wake word or configure a shorter phrase with --wake-word."));
        RULES.add(new HintRule(
                msg -> contains(msg, "OpenAI API key"),
                "Export NEXUS_OPENAI_KEY or OPENAI_API_KEY before starting the voice assistant."));
        RULES.add(new HintRule(
                msg -> contains(msg, "microphone capture not supported") || contains(msg, "unable to access system microphone"),
                "Check OS microphone permissions or fall back to `voice --file=...` mode."));
        RULES.add(new HintRule(
                msg -> contains(msg, "nl processor unavailable"),
                "Ensure the CLI registers the natural language command (launch via NexusCliApplication)."));
    }

    private VoiceErrorHints() {
    }

    public static Optional<String> lookup(String message) {
        if (message == null || message.isBlank()) {
            return Optional.empty();
        }
        for (HintRule rule : RULES) {
            if (rule.predicate.test(message)) {
                return Optional.of(rule.hint);
            }
        }
        return Optional.empty();
    }

    private static boolean contains(String message, String token) {
        return message != null && message.toLowerCase(Locale.ROOT).contains(token.toLowerCase(Locale.ROOT));
    }

    private record HintRule(Predicate<String> predicate, String hint) {}
}
