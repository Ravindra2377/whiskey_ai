package com.boozer.nexus.cli.commands;

import com.boozer.nexus.persistence.VoiceCommandLogService;
import com.boozer.nexus.voice.AudioRecorder;
import com.boozer.nexus.voice.VoiceErrorHints;
import com.boozer.nexus.voice.WhisperClient;

import javax.sound.sampled.LineUnavailableException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.Locale;
import java.util.Optional;
import java.util.StringJoiner;

public class VoiceCommand implements Command {
    private static final Duration CHUNK_DURATION = Duration.ofSeconds(1);
    private static final String DEFAULT_WAKE_WORD = "hey nexus";
    private static final int WAKE_WORD_HINT_THRESHOLD = 2;
    private static final int BLANK_TRANSCRIPT_THRESHOLD = 3;

    private final VoiceCommandLogService historyService;
    private final NaturalLanguageCommand naturalLanguage;

    public VoiceCommand(VoiceCommandLogService historyService, NaturalLanguageCommand naturalLanguage) {
        this.historyService = historyService;
        this.naturalLanguage = naturalLanguage;
    }

    @Override
    public String name() { return "voice"; }

    @Override
    public String description() {
        return "Voice assistant: use --file=audio.wav or --live for real-time capture (requires OpenAI Whisper key)." +
                " Include --check-config to validate microphone, analytics, and AI setup or --no-wake-word for continuous capture.";
    }

    @Override
    public int run(String[] args) {
        String file = null;
        boolean echo = false;
        boolean live = false;
        boolean noWakeWord = false;
        boolean check = false;
        String wakeWord = DEFAULT_WAKE_WORD;
        String openAiKey = null;

        for (String a : args) {
            if (a.startsWith("--file=")) file = a.substring("--file=".length());
            if (a.equals("--echo")) echo = true;
            if (a.equals("--live")) live = true;
            if (a.equals("--no-wake-word")) noWakeWord = true;
            if (a.equals("--check-config")) check = true;
            if (a.startsWith("--wake-word=")) wakeWord = a.substring("--wake-word=".length()).trim();
            if (a.startsWith("--openai-key=")) openAiKey = a.substring("--openai-key=".length()).trim();
        }

        String resolvedKey = resolveOpenAiKey(openAiKey);

        if (check) {
            return runConfigCheck(resolvedKey, live);
        }

        if (live) {
            return runLiveMode(resolvedKey, wakeWord, echo, noWakeWord);
        }

        if (file == null) {
            System.err.println("Usage: voice --file=audio.wav [--echo] | voice --live [--wake-word=phrase] [--echo] [--no-wake-word] | voice --check-config");
            return 2;
        }

        return runFileMode(file, echo, resolvedKey)
                .orElse(2);
    }

    private String resolveOpenAiKey(String explicit) {
        if (explicit != null && !explicit.isBlank()) {
            return explicit;
        }
        String key = System.getenv("NEXUS_OPENAI_KEY");
        if (key == null || key.isBlank()) {
            key = System.getenv("OPENAI_API_KEY");
        }
        return key;
    }

    private int runConfigCheck(String openAiKey, boolean liveRequested) {
        System.out.println("NEXUS Voice Configuration Check");
        boolean ok = true;

        if (openAiKey == null || openAiKey.isBlank()) {
            System.out.println("❌ OpenAI API key missing. Set NEXUS_OPENAI_KEY or OPENAI_API_KEY, or pass --openai-key=KEY.");
            ok = false;
        } else {
            System.out.println("✅ OpenAI API key detected.");
        }

        if (AudioRecorder.isSupported()) {
            System.out.println("✅ Microphone capture supported by this JVM.");
        } else {
            System.out.println("❌ Microphone capture not supported on this system.");
            if (liveRequested) ok = false;
        }

        if (historyService != null) {
            System.out.println("✅ Database logging enabled for voice commands.");
        } else {
            System.out.println("ℹ️  Database logging disabled (VoiceCommandLogService not available). Enable nexus.db.enabled=true to store history.");
        }

        if (naturalLanguage != null) {
            System.out.println("✅ Conversational AI pipeline active with resumable actions and analytics integration.");
        }

        System.out.println("Run `voice --live --echo` to start, add --no-wake-word for continuous capture, or pass --openai-key=KEY to override.");
        return ok ? 0 : 1;
    }

    private Optional<Integer> runFileMode(String file, boolean echo, String openAiKey) {
        Path path = Paths.get(file);
        if (!Files.exists(path)) {
            System.err.println("Audio file not found: " + path.toAbsolutePath());
            return Optional.of(2);
        }

        try {
            String transcript;
            if (openAiKey != null && !openAiKey.isBlank()) {
                transcript = new WhisperClient(openAiKey).transcribe(path);
            } else {
                transcript = simulateTranscription(path);
            }
            if (transcript == null || transcript.isBlank()) {
                System.err.println("No speech detected in file.");
                logHistory(DEFAULT_WAKE_WORD, "", "", null, false, "empty transcript");
                return Optional.of(3);
            }

            if (echo) {
                System.out.println("[voice:transcript] " + transcript);
            }

            System.out.println("[voice] -> nl: " + transcript);
            int code = executeCommand(transcript, transcript, DEFAULT_WAKE_WORD);
            return Optional.of(code);
        } catch (Exception ex) {
            System.err.println("Failed to process audio file: " + ex.getMessage());
            logHistory(DEFAULT_WAKE_WORD, "", "", null, false, ex.getMessage());
            return Optional.of(1);
        }
    }

    private int runLiveMode(String openAiKey, String wakeWord, boolean echo, boolean noWakeWord) {
        String resolvedWakeWord = wakeWord == null || wakeWord.isBlank() ? DEFAULT_WAKE_WORD : wakeWord.toLowerCase(Locale.ROOT);
        TranscriptTracker tracker = new TranscriptTracker();
        int wakeWordMisses = 0;
        int blankTranscriptEvents = 0;

        if (openAiKey == null || openAiKey.isBlank()) {
            System.err.println("OpenAI API key not provided. Use --openai-key=KEY or set NEXUS_OPENAI_KEY / OPENAI_API_KEY environment variables.");
            return 2;
        }
        if (!AudioRecorder.isSupported()) {
            System.err.println("Microphone capture is not supported on this system.");
            return 3;
        }

        if (noWakeWord) {
            System.out.println("🎤 NEXUS voice mode active (continuous). Speak commands directly or say 'exit' to finish.");
        } else {
            System.out.println("🎤 NEXUS voice mode active. Say '" + resolvedWakeWord + " ...' followed by a command. Say '" + resolvedWakeWord + " exit' to stop.");
        }
        WhisperClient whisperClient = new WhisperClient(openAiKey);

        try (AudioRecorder recorder = new AudioRecorder()) {
            recorder.start();
            Runtime.getRuntime().addShutdownHook(new Thread(recorder::stop));
            int silentChunks = 0;

            while (true) {
                byte[] audio = recorder.capture(CHUNK_DURATION);
                if (audio.length == 0) {
                    silentChunks++;
                    if (echo && silentChunks % 5 == 0) {
                        System.out.println("…listening…");
                    }
                    if (silentChunks >= 15) {
                        if (recorder.restart()) {
                            System.out.println("🔄 Microphone capture restarted.");
                            silentChunks = 0;
                            continue;
                        }
                    }
                    continue;
                }
                silentChunks = 0;

                String transcript;
                try {
                    transcript = whisperClient.transcribe(audio, recorder.getFormat());
                } catch (Exception ex) {
                    System.err.println("Whisper transcription failed: " + ex.getMessage());
                    logHistory(resolvedWakeWord, "", "", null, false, ex.getMessage());
                    continue;
                }

                if (transcript == null || transcript.isBlank()) {
                    blankTranscriptEvents++;
                    if (blankTranscriptEvents % BLANK_TRANSCRIPT_THRESHOLD == 0) {
                        System.out.println("⚠️  I'm hearing silence or background noise. Move closer to the mic or re-run `voice --check-config`.");
                        logHistory(resolvedWakeWord, "", "", "no-speech", false, "blank transcript chunk");
                    }
                    continue;
                }
                blankTranscriptEvents = 0;

                String delta = tracker.delta(transcript);
                if (echo && !delta.isBlank()) {
                    System.out.println("[voice:transcript] " + delta);
                }

                boolean wakeWordHeard = !noWakeWord && transcript.toLowerCase(Locale.ROOT).contains(resolvedWakeWord);
                String command = noWakeWord ? transcript.trim() : extractCommand(transcript, resolvedWakeWord);
                if (command == null) {
                    if (wakeWordHeard) {
                        wakeWordMisses++;
                        if (wakeWordMisses % WAKE_WORD_HINT_THRESHOLD == 0) {
                            System.out.println("⚠️  I heard '" + resolvedWakeWord + "' but not the command. Pause briefly after the wake word or choose a shorter phrase.");
                            logHistory(resolvedWakeWord, transcript, "", "wake-word-miss", false, "wake word detected without command");
                        }
                    }
                    continue;
                }
                wakeWordMisses = 0;

                if (isExitCommand(command)) {
                    System.out.println("👋 Exiting voice mode.");
                    logHistory(resolvedWakeWord, transcript, command, "exit", true, null);
                    return 0;
                }

                int code = executeCommand(command, transcript, noWakeWord ? "(no wake word)" : resolvedWakeWord);
                if (code == 0) {
                    System.out.println("✔ Command completed: " + command);
                } else {
                    System.err.println("Command returned exit code " + code);
                }
            }
        } catch (LineUnavailableException e) {
            System.err.println("Unable to access system microphone: " + e.getMessage());
            return 4;
        }
    }

    private int executeCommand(String commandText, String transcript, String wakeWord) {
        try {
            if (naturalLanguage == null) {
                System.err.println("Natural language processor unavailable. Enable nl command to execute voice requests.");
                logHistory(wakeWord, transcript, commandText, null, false, "nl processor unavailable");
                return 1;
            }

            NaturalLanguageCommand.NaturalLanguageResult nlResult = naturalLanguage.process(commandText, true);
            nlResult.getMessages().forEach(message -> System.out.println("[voice:nl] " + message));
            if (nlResult.isAwaitingConfirmation()) {
                System.out.println("Awaiting your confirmation. Say 'yes' to continue or 'no' to cancel.");
            }

            boolean success = nlResult.isExecuted() && nlResult.getExitCode() == 0;
            String error = null;
            if (nlResult.isAwaitingConfirmation()) {
                error = "awaiting confirmation";
            } else if (!success && nlResult.getExitCode() != 0) {
                error = summarize(nlResult.getMessages());
            }

            logHistory(wakeWord, transcript, commandText, nlResult.getIntentLabel(), success, error);
            VoiceErrorHints.lookup(error).ifPresent(hint -> System.out.println("[voice:hint] " + hint));
            return nlResult.getExitCode();
        } catch (Exception ex) {
            logHistory(wakeWord, transcript, commandText, null, false, ex.getMessage());
            System.err.println("Command execution failed: " + ex.getMessage());
            VoiceErrorHints.lookup(ex.getMessage()).ifPresent(hint -> System.out.println("[voice:hint] " + hint));
            return 1;
        }
    }

    private String extractCommand(String transcript, String wakeWord) {
        String lower = transcript.toLowerCase(Locale.ROOT);
        int idx = lower.indexOf(wakeWord.toLowerCase(Locale.ROOT));
        if (idx < 0) {
            return null;
        }

        String command = transcript.substring(idx + wakeWord.length()).trim();
        if (command.isBlank()) {
            return null;
        }
        return command;
    }

    private boolean isExitCommand(String command) {
        String normalized = command.trim().toLowerCase(Locale.ROOT);
        return normalized.equals("exit") || normalized.equals("quit") || normalized.equals("cancel voice");
    }

    private void logHistory(String wakeWord, String transcript, String commandText, String intentLabel, boolean success, String error) {
        if (historyService != null) {
            historyService.record(wakeWord, transcript, commandText, intentLabel, success, error);
        }
    }

    private String simulateTranscription(Path audio) {
        String n = audio.getFileName().toString().toLowerCase(Locale.ROOT);
        if (n.contains("ingest")) return "ingest operations";
        if (n.contains("list") && n.contains("python")) return "list python scripts";
        if (n.contains("run") && n.contains("infra")) return "run infra tasks";
        return "catalog summary";
    }

    private String summarize(java.util.List<String> messages) {
        if (messages == null || messages.isEmpty()) {
            return null;
        }
        StringJoiner joiner = new StringJoiner(" | ");
        messages.forEach(joiner::add);
        return joiner.toString();
    }

    private static class TranscriptTracker {
        private String last = "";

        String delta(String transcript) {
            if (transcript == null) {
                return "";
            }
            String normalized = transcript.trim();
            if (normalized.isBlank()) {
                return "";
            }

            if (last.isEmpty()) {
                last = normalized;
                return normalized;
            }

            if (normalized.equalsIgnoreCase(last)) {
                return "";
            }

            if (normalized.startsWith(last)) {
                String diff = normalized.substring(last.length()).trim();
                last = normalized;
                return diff;
            }

            last = normalized;
            return normalized;
        }
    }
}
