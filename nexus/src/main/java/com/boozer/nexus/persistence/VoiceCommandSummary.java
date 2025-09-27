package com.boozer.nexus.persistence;

import java.time.OffsetDateTime;
import java.util.Map;

public class VoiceCommandSummary {
    private final long total;
    private final long successful;
    private final Map<String, Long> commandCounts;
    private final Map<String, Long> errorCounts;
    private final OffsetDateTime first;
    private final OffsetDateTime last;

    public VoiceCommandSummary(long total, long successful, Map<String, Long> commandCounts, Map<String, Long> errorCounts, OffsetDateTime first, OffsetDateTime last) {
        this.total = total;
        this.successful = successful;
        this.commandCounts = commandCounts;
        this.errorCounts = errorCounts;
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

    public OffsetDateTime getFirst() {
        return first;
    }

    public OffsetDateTime getLast() {
        return last;
    }
}
