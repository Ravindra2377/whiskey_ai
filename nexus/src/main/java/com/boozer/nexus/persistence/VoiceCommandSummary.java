package com.boozer.nexus.persistence;

import java.time.OffsetDateTime;
import java.util.Map;

public class VoiceCommandSummary {
    private final long total;
    private final long successful;
    private final Map<String, Long> commandCounts;
    private final Map<String, Long> errorCounts;
    private final Map<String, Long> intentCounts;
    private final long awaitingConfirmation;
    private final long wakeWordMisses;
    private final long noSpeechEvents;
    private final long resumedCommands;
    private final long repeatedCommands;
    private final OffsetDateTime first;
    private final OffsetDateTime last;

    public VoiceCommandSummary(long total,
                               long successful,
                               Map<String, Long> commandCounts,
                               Map<String, Long> errorCounts,
                               Map<String, Long> intentCounts,
                               long awaitingConfirmation,
                               long wakeWordMisses,
                               long noSpeechEvents,
                               long resumedCommands,
                               long repeatedCommands,
                               OffsetDateTime first,
                               OffsetDateTime last) {
        this.total = total;
        this.successful = successful;
        this.commandCounts = commandCounts;
        this.errorCounts = errorCounts;
        this.intentCounts = intentCounts;
        this.awaitingConfirmation = awaitingConfirmation;
        this.wakeWordMisses = wakeWordMisses;
        this.noSpeechEvents = noSpeechEvents;
        this.resumedCommands = resumedCommands;
        this.repeatedCommands = repeatedCommands;
        this.first = first;
        this.last = last;
    }

    public long getTotal() {
        return total;
    }

    public long getSuccessful() {
        return successful;
    }

    public Map<String, Long> getCommandCounts() {
        return commandCounts;
    }

    public Map<String, Long> getErrorCounts() {
        return errorCounts;
    }

    public Map<String, Long> getIntentCounts() {
        return intentCounts;
    }

    public long getAwaitingConfirmation() {
        return awaitingConfirmation;
    }

    public long getWakeWordMisses() {
        return wakeWordMisses;
    }

    public long getNoSpeechEvents() {
        return noSpeechEvents;
    }

    public long getResumedCommands() {
        return resumedCommands;
    }

    public long getRepeatedCommands() {
        return repeatedCommands;
    }

    public OffsetDateTime getFirst() {
        return first;
    }

    public OffsetDateTime getLast() {
        return last;
    }

    public double getSuccessRate() {
        if (total == 0) {
            return 0.0;
        }
        return (double) successful / (double) total;
    }

    public double getConfirmationRate() {
        if (total == 0) {
            return 0.0;
        }
        return (double) awaitingConfirmation / (double) total;
    }
}
