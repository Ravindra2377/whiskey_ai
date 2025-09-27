package com.boozer.nexus.cli.commands;

import com.boozer.nexus.nl.ConversationContext;
import com.boozer.nexus.persistence.VoiceCommandAnalyticsService;
import com.boozer.nexus.persistence.VoiceCommandLogService;
import com.boozer.nexus.persistence.VoiceCommandSummary;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.Callable;

public class NaturalLanguageCommand implements Command {
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("MMM d, HH:mm");

    private final ConversationContext conversationContext;
    private final VoiceCommandAnalyticsService analyticsService;
    private final VoiceCommandLogService logService;
    private Map<String, Command> commandRegistry = Map.of();

    public NaturalLanguageCommand(ConversationContext conversationContext,
                                  VoiceCommandAnalyticsService analyticsService,
                                  VoiceCommandLogService logService) {
        this.conversationContext = conversationContext;
        this.analyticsService = analyticsService;
        this.logService = logService;
    }

    public void setCommandRegistry(Map<String, Command> registry) {
        this.commandRegistry = Objects.requireNonNull(registry);
    }

    @Override
    public String name() {
        return "nl";
    }

    @Override
    public String description() {
        return "Conversational interface with context, analytics awareness, and resumable actions.";
    }

    @Override
    public int run(String[] args) throws Exception {
        if (args.length == 0) {
            System.out.println("Usage: nl \"your request...\"");
            return 2;
        }

        String request = String.join(" ", args).trim();
        NaturalLanguageResult result = process(request, false);
        result.getMessages().forEach(System.out::println);
        logForAnalytics(request, result);
        return result.getExitCode();
    }

    public NaturalLanguageResult process(String request, boolean voiceTriggered) throws Exception {
        if (request == null || request.isBlank()) {
            return NaturalLanguageResult.failure("empty-input", 2,
                    List.of("I didn't catch that. Could you repeat the request?"));
        }

        String normalized = request.toLowerCase(Locale.ROOT).trim();
        conversationContext.recordUtterance(request);

        // Resume pending operations if the user confirms.
        if (isAffirmative(normalized)) {
            Optional<ConversationContext.PendingCommand> pending = conversationContext.consumePendingCommand();
            if (pending.isPresent()) {
                List<String> messages = new ArrayList<>();
                messages.add("Got it, resuming your previous request: " + pending.get().getDescription() + ".");
                try {
                    int exit = pending.get().execute();
                    messages.add(exit == 0 ? "✅ Completed successfully." : "⚠️ Command exited with code " + exit);
                    return NaturalLanguageResult.executed("resume-pending", exit, messages);
                } catch (Exception ex) {
                    messages.add("❌ The command failed: " + ex.getMessage());
                    return NaturalLanguageResult.failure("resume-pending", 1, messages);
                }
            }
        }

        if (isNegative(normalized)) {
            if (conversationContext.getPendingCommand().isPresent()) {
                conversationContext.clearPendingCommand();
                return NaturalLanguageResult.info("cancel-pending",
                        List.of("No problem, I've canceled the pending action."));
            }
        }

        if (conversationContext.getPendingCommand().isPresent() && !voiceTriggered) {
            // Remind user about pending command if they issue a different request.
            conversationContext.clearPendingCommand();
        }

        if (isRepeat(normalized)) {
            return replayLastCommand();
        }

        if (normalized.contains("help") || normalized.contains("what can you do")) {
            return NaturalLanguageResult.info("help",
                    List.of("You can ask for deployments, code generation, voice analytics, or to list and run automations.",
                            "Try: 'generate a python script to import csv', 'show voice analytics', or 'deploy staging (confirm)'."));
        }

        if (normalized.contains("intent") && normalized.contains("analytics")) {
            return describeVoiceIntents();
        }

        if (normalized.contains("analytics")) {
            boolean voice = normalized.contains("voice") || normalized.contains("speech");
            boolean server = normalized.contains("server") || normalized.contains("dashboard") || normalized.contains("api");
            return runAnalyticsCommand(voice, server ? Optional.of(derivePort(normalized)) : Optional.empty());
        }

        if (normalized.contains("deploy")) {
            return handleDeploy(request, normalized);
        }

        if (normalized.contains("ingest")) {
            return executeCommand("ingest", new String[]{} , "ingest-operations");
        }

        if (normalized.contains("list") || normalized.contains("show") || normalized.contains("catalog")) {
            return handleCatalog(request, normalized);
        }

        if (normalized.contains("run") || normalized.contains("execute")) {
            return handleRun(request, normalized);
        }

        if (normalized.contains("compliance") || normalized.contains("security")) {
            return runAnalyticsCommand(false, Optional.empty());
        }

        if (normalized.contains("generate") || normalized.contains("build code") || normalized.contains("create")) {
            return handleGenerate(request, normalized, voiceTriggered);
        }

        return NaturalLanguageResult.failure("unknown", 2,
                List.of("Sorry, I couldn't map that request.",
                        "Try asking for voice analytics, deployments, or code generation."));
    }

    private NaturalLanguageResult handleGenerate(String original, String normalized, boolean voiceTriggered) {
        List<String> args = new ArrayList<>();
        String spec = buildSpec(original);
        if (!spec.isBlank()) {
            args.add("--spec=" + spec);
        }
        addLanguageHints(normalized, args);
        if (normalized.contains("test")) {
            args.add("--include-tests");
        }

        String description = "generate code" + (spec.isBlank() ? "" : " for: " + spec);
        if (voiceTriggered) {
            conversationContext.setPendingCommand(description, buildExecutor("generate", args.toArray(new String[0])));
            return NaturalLanguageResult.awaitingConfirmation("generate", description,
                    List.of("I can generate that. Say 'yes' to confirm or 'no' to cancel."));
        }

        try {
            String[] execArgs = args.toArray(new String[0]);
            Callable<Integer> executor = buildExecutor("generate", execArgs);
            int exit = executor.call();
            if (exit == 0) {
                conversationContext.rememberLastCommand("generate", describeCommand("generate", execArgs), executor);
            }
            return NaturalLanguageResult.executed("generate", exit,
                    List.of("Launching code generation with spec: " + spec));
        } catch (Exception ex) {
            return NaturalLanguageResult.failure("generate", 1,
                    List.of("Generation failed: " + ex.getMessage()));
        }
    }

    private NaturalLanguageResult handleDeploy(String original, String normalized) {
        List<String> args = new ArrayList<>();
        args.add("--tag=deploy");
        if (normalized.contains("staging")) args.add("--name=staging");
        if (normalized.contains("production") || normalized.contains("prod")) args.add("--name=prod");
        if (normalized.contains("confirm") || normalized.contains("execute")) args.add("--confirm");

        String description = "deployment" + (args.stream().anyMatch(a -> a.contains("name=")) ?
                " for " + args.stream().filter(a -> a.startsWith("--name=")).map(a -> a.substring(7)).findFirst().orElse("target") : "");

        if (!args.contains("--confirm")) {
        conversationContext.setPendingCommand("deploy workflow" + (description.isBlank() ? "" : " (" + description + ")"),
            buildExecutor("run", args.toArray(new String[0])));
            return NaturalLanguageResult.awaitingConfirmation("deploy", description,
                    List.of("Deployment requires confirmation. Say 'yes' to proceed or 'no' to cancel."));
        }

        return executeCommand("run", args.toArray(new String[0]), "deploy");
    }

    private NaturalLanguageResult handleCatalog(String original, String normalized) {
        List<String> args = new ArrayList<>();
        args.add("list");
        if (normalized.contains("python")) args.add("--type=python");
        if (normalized.contains("powershell") || normalized.contains("ps1")) args.add("--type=powershell");
        if (normalized.contains("shell") || normalized.contains("bash") || normalized.contains("sh")) args.add("--type=shell");
        if (normalized.contains("java")) args.add("--type=java");
        if (normalized.contains("data")) args.add("--tag=data");

        return executeCommand("catalog", args.toArray(new String[0]), "catalog");
    }

    private NaturalLanguageResult handleRun(String original, String normalized) {
        List<String> args = new ArrayList<>();
        if (normalized.contains("python")) args.add("--type=python");
        if (normalized.contains("powershell") || normalized.contains("ps1")) args.add("--type=powershell");
        if (normalized.contains("shell") || normalized.contains("bash") || normalized.contains("sh")) args.add("--type=shell");
        if (normalized.contains("java")) args.add("--type=java");
        if (normalized.contains("data")) args.add("--tag=data");
        boolean confirmed = normalized.contains("confirm") || normalized.contains("now");
        List<String> messages = new ArrayList<>();
        if (!confirmed) {
            args.add("--dry-run");
            messages.add("Running as dry-run. Add 'confirm' if you want to execute for real.");
        }
        NaturalLanguageResult result = executeCommand("run", args.toArray(new String[0]), "run");
        if (messages.isEmpty()) {
            return result;
        }
        List<String> merged = new ArrayList<>(messages);
        merged.addAll(result.getMessages());
        return result.withMessages(merged);
    }

    private NaturalLanguageResult runAnalyticsCommand(boolean voice, Optional<Integer> serverPort) {
        List<String> args = new ArrayList<>();
        if (voice) {
            args.add("--voice");
        }
        serverPort.ifPresent(port -> args.add("--server=" + port));

        String intent = voice ? (serverPort.isPresent() ? "voice-analytics-server" : "voice-analytics") : "analytics";
        return executeCommand("analytics", args.toArray(new String[0]), intent);
    }

    private NaturalLanguageResult describeVoiceIntents() {
        if (analyticsService == null) {
            return NaturalLanguageResult.info("intent-analytics",
                    List.of("Voice analytics are unavailable because persistence is disabled."));
        }
        VoiceCommandSummary summary = analyticsService.summarize();
        if (summary.getTotal() == 0) {
            return NaturalLanguageResult.info("intent-analytics", List.of("No voice interactions logged yet."));
        }

        List<String> lines = new ArrayList<>();
        lines.add("Voice intents recorded: " + summary.getTotal() + " sessions, " + summary.getSuccessful() + " successful.");
        lines.add(String.format("Success rate: %.1f%%", summary.getSuccessRate() * 100.0));
        if (summary.getFirst() != null && summary.getLast() != null) {
            lines.add("Window: " + DATE_FMT.format(summary.getFirst()) + " → " + DATE_FMT.format(summary.getLast()));
        }
        if (summary.getAwaitingConfirmation() > 0) {
            lines.add("Awaiting confirmations: " + summary.getAwaitingConfirmation());
        }
        if (summary.getResumedCommands() > 0 || summary.getRepeatedCommands() > 0) {
            lines.add("Follow-ups -> resumed: " + summary.getResumedCommands() + ", repeated: " + summary.getRepeatedCommands());
        }
        if (summary.getWakeWordMisses() > 0 || summary.getNoSpeechEvents() > 0) {
            lines.add("Signal quality -> wake word misses: " + summary.getWakeWordMisses() + ", no speech: " + summary.getNoSpeechEvents());
        }
        if (!summary.getIntentCounts().isEmpty()) {
            lines.add("Top intents:");
            summary.getIntentCounts().forEach((intent, count) ->
                    lines.add(String.format("  - %s: %d", intent, count)));
        }
        return NaturalLanguageResult.info("intent-analytics", lines);
    }

    private NaturalLanguageResult replayLastCommand() {
        var snapshotOpt = conversationContext.getLastCommand();
        if (snapshotOpt.isEmpty()) {
            return NaturalLanguageResult.info("no-replay",
                    List.of("I don't have a recent action to repeat yet."));
        }

        var snapshot = snapshotOpt.get();
        List<String> messages = new ArrayList<>();
        messages.add("Replaying your last action: " + snapshot.getDescription() + ".");
        try {
            int exit = snapshot.replay();
            messages.add(exit == 0 ? "✅ Done." : "⚠️ Command exited with code " + exit);
            String intent = conversationContext.getLastIntent().orElse("repeat-last");
            return NaturalLanguageResult.replayed(intent, exit, messages);
        } catch (Exception ex) {
            messages.add("❌ Couldn't repeat it: " + ex.getMessage());
            return NaturalLanguageResult.failure("repeat-last", 1, messages);
        }
    }

    private NaturalLanguageResult executeCommand(String command, String[] args, String intentLabel) {
        try {
            Callable<Integer> executor = buildExecutor(command, args);
            int exit = executor.call();
            if (exit == 0) {
                conversationContext.rememberLastCommand(intentLabel, describeCommand(command, args), executor);
            }
            return NaturalLanguageResult.executed(intentLabel, exit,
                    List.of("Executing `" + describeCommand(command, args) + "`") );
        } catch (Exception ex) {
            return NaturalLanguageResult.failure(intentLabel, 1,
                    List.of("Failed to execute `" + command + "`: " + ex.getMessage()));
        }
    }

    private Callable<Integer> buildExecutor(String command, String[] args) {
        Command target = commandRegistry.get(command);
        if (target == null) {
            throw new IllegalStateException("Command not available: " + command);
        }
        String[] copy = Arrays.copyOf(args, args.length);
        return () -> target.run(copy);
    }

    private String describeCommand(String command, String[] args) {
        if (args == null || args.length == 0) {
            return command;
        }
        return command + " " + String.join(" ", args);
    }

    private void addLanguageHints(String normalized, List<String> args) {
        if (normalized.contains("python")) args.add("--language=python");
        if (normalized.contains("java")) args.add("--language=java");
        if (normalized.contains("kotlin")) args.add("--language=kotlin");
        if (normalized.contains("typescript") || normalized.contains("node")) args.add("--language=typescript");
    }

    private int derivePort(String normalized) {
        for (String token : normalized.split(" ")) {
            if (token.startsWith("port")) {
                String digits = token.replaceAll("\\D", "");
                if (!digits.isBlank()) {
                    try {
                        int p = Integer.parseInt(digits);
                        if (p > 1024 && p < 65535) {
                            return p;
                        }
                    } catch (NumberFormatException ignored) {
                    }
                }
            }
            if (token.matches("\\d{4,5}")) {
                int p = Integer.parseInt(token);
                if (p > 1024 && p < 65535) {
                    return p;
                }
            }
        }
        return 8088;
    }

    private String buildSpec(String original) {
        String cleaned = original.replaceAll("(?i)hey nexus", "").trim();
        int idx = cleaned.toLowerCase(Locale.ROOT).indexOf("generate");
        if (idx >= 0) {
            cleaned = cleaned.substring(idx + "generate".length()).trim();
        }
        if (cleaned.isBlank()) {
            return original.trim();
        }
        return cleaned;
    }

    private boolean isAffirmative(String normalized) {
        return normalized.equals("yes") || normalized.equals("yeah") || normalized.equals("confirm")
                || normalized.equals("do it") || normalized.contains("let's go") || normalized.contains("proceed");
    }

    private boolean isNegative(String normalized) {
        return normalized.equals("no") || normalized.contains("not now") || normalized.contains("cancel")
                || normalized.contains("stop");
    }

    private boolean isRepeat(String normalized) {
        return normalized.equals("again")
                || normalized.equals("repeat")
                || normalized.contains("repeat that")
                || normalized.contains("do it again")
                || normalized.contains("same as before")
                || normalized.contains("do that again")
                || normalized.contains("run it again");
    }

    private void logForAnalytics(String request, NaturalLanguageResult result) {
        if (logService == null) {
            return;
        }
        String intent = result.getIntentLabel() == null ? "unknown" : result.getIntentLabel();
        boolean success = result.isExecuted() && result.getExitCode() == 0;
        String error;
        if (result.isAwaitingConfirmation()) {
            error = "awaiting confirmation";
        } else if (!success && result.getExitCode() != 0) {
            error = String.join(" | ", result.getMessages());
        } else {
            error = null;
        }
        logService.record("(cli)", request, request, intent, success, error);
    }

    public static class NaturalLanguageResult {
        private final String intentLabel;
        private final int exitCode;
        private final List<String> messages;
        private final boolean executed;
        private final boolean awaitingConfirmation;

        private NaturalLanguageResult(String intentLabel, int exitCode, List<String> messages,
                                      boolean executed, boolean awaitingConfirmation) {
            this.intentLabel = intentLabel;
            this.exitCode = exitCode;
            this.messages = messages == null ? List.of() : List.copyOf(messages);
            this.executed = executed;
            this.awaitingConfirmation = awaitingConfirmation;
        }

        public static NaturalLanguageResult executed(String intentLabel, int exitCode, List<String> messages) {
            return new NaturalLanguageResult(intentLabel, exitCode, messages, true, false);
        }

        public static NaturalLanguageResult replayed(String intentLabel, int exitCode, List<String> messages) {
            return new NaturalLanguageResult(intentLabel, exitCode, messages, true, false);
        }

        public static NaturalLanguageResult awaitingConfirmation(String intentLabel, String description, List<String> messages) {
            return new NaturalLanguageResult(intentLabel, 0, messages, false, true);
        }

        public static NaturalLanguageResult failure(String intentLabel, int exitCode, List<String> messages) {
            return new NaturalLanguageResult(intentLabel, exitCode, messages, false, false);
        }

        public static NaturalLanguageResult info(String intentLabel, List<String> messages) {
            return new NaturalLanguageResult(intentLabel, 0, messages, false, false);
        }

        public NaturalLanguageResult withMessages(List<String> updated) {
            return new NaturalLanguageResult(intentLabel, exitCode, updated, executed, awaitingConfirmation);
        }

        public String getIntentLabel() {
            return intentLabel;
        }

        public int getExitCode() {
            return exitCode;
        }

        public List<String> getMessages() {
            return messages;
        }

        public boolean isExecuted() {
            return executed;
        }

        public boolean isAwaitingConfirmation() {
            return awaitingConfirmation;
        }
    }
}
