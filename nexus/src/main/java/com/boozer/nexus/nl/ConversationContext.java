package com.boozer.nexus.nl;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.Callable;

/**
 * Maintains a lightweight conversation context for natural language interactions.
 */
public class ConversationContext {
    private final Deque<String> transcript = new ArrayDeque<>();
    private PendingCommand pending;
    private CommandSnapshot lastCommand;
    private String lastIntent;

    public void recordUtterance(String utterance) {
        if (utterance == null || utterance.isBlank()) {
            return;
        }
        transcript.addLast(utterance);
        while (transcript.size() > 20) {
            transcript.removeFirst();
        }
    }

    public Optional<PendingCommand> getPendingCommand() {
        return Optional.ofNullable(pending);
    }

    public void setPendingCommand(String description, Callable<Integer> executor) {
        this.pending = new PendingCommand(description, executor);
    }

    public Optional<PendingCommand> consumePendingCommand() {
        PendingCommand cmd = this.pending;
        this.pending = null;
        return Optional.ofNullable(cmd);
    }

    public void clearPendingCommand() {
        this.pending = null;
    }

    public void rememberLastCommand(String intentLabel, String description, Callable<Integer> executor) {
        if (executor == null) {
            return;
        }
        this.lastCommand = new CommandSnapshot(description, executor);
        this.lastIntent = intentLabel;
    }

    public Optional<CommandSnapshot> getLastCommand() {
        return Optional.ofNullable(lastCommand);
    }

    public Optional<String> getLastIntent() {
        return Optional.ofNullable(lastIntent);
    }

    public Deque<String> getTranscript() {
        return transcript;
    }

    public static final class PendingCommand {
        private final String description;
        private final Callable<Integer> executor;

        private PendingCommand(String description, Callable<Integer> executor) {
            this.description = Objects.requireNonNullElse(description, "pending command");
            this.executor = Objects.requireNonNull(executor);
        }

        public String getDescription() {
            return description;
        }

        public int execute() throws Exception {
            return executor.call();
        }
    }

    public static final class CommandSnapshot {
        private final String description;
        private final Callable<Integer> executor;

        private CommandSnapshot(String description, Callable<Integer> executor) {
            this.description = Objects.requireNonNullElse(description, "recent command");
            this.executor = Objects.requireNonNull(executor);
        }

        public String getDescription() {
            return description;
        }

        public int replay() throws Exception {
            return executor.call();
        }
    }
}
