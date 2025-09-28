package com.boozer.nexus.cli.commands;

import com.boozer.nexus.persistence.VoiceCommandLogService;
import com.boozer.nexus.voice.AudioRecorder;
import com.boozer.nexus.voice.VoiceErrorHints;
import com.boozer.nexus.voice.WhisperClient;
import com.boozer.nexus.voice.intelligence.IntelligentVoicePipeline;
import com.boozer.nexus.voice.processing.TranscriptionResult;
import com.boozer.nexus.voice.processing.VoiceProcessingResult;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
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
        " Add --intelligent for multi-stage analytics, --check-config to validate setup, or --no-wake-word for continuous capture.";
    }

    @Override
    public int run(String[] args) {
        String file = null;
        boolean echo = false;
        boolean live = false;
        boolean intelligent = false;
        boolean noWakeWord = false;
        boolean check = false;
        String wakeWord = DEFAULT_WAKE_WORD;
        String openAiKey = null;

        for (String a : args) {
            if (a.startsWith("--file=")) file = a.substring("--file=".length());
            if (a.equals("--echo")) echo = true;
            if (a.equals("--live")) live = true;
            if (a.equals("--intelligent")) intelligent = true;
            if (a.equals("--no-wake-word")) noWakeWord = true;
            if (a.equals("--check-config")) check = true;
            if (a.startsWith("--wake-word=")) wakeWord = a.substring("--wake-word=".length()).trim();
            if (a.startsWith("--openai-key=")) openAiKey = a.substring("--openai-key=".length()).trim();
        }

        String resolvedKey = resolveOpenAiKey(openAiKey);

        if (intelligent && !check && (resolvedKey == null || resolvedKey.isBlank())) {
            System.err.println("Intelligent mode requires an OpenAI API key. Provide --openai-key=KEY or export NEXUS_OPENAI_KEY.");
            return 2;
        }

        if (intelligent && !live && !check) {
            live = true;
            System.out.println("ℹ Intelligent voice mode enables live capture automatically.");
        }

        IntelligentVoicePipeline pipeline = null;
        if (intelligent && !check) {
            pipeline = IntelligentVoicePipeline.createDefault(resolvedKey);
        }

        if (check) {
            return runConfigCheck(resolvedKey, live, intelligent);
        }

        if (live) {
            return runLiveMode(resolvedKey, wakeWord, echo, noWakeWord, intelligent, pipeline);
        }

        if (file == null) {
            System.err.println("Usage: voice --file=audio.wav [--echo] | voice --live [--intelligent] [--wake-word=phrase] [--echo] [--no-wake-word] | voice --check-config");
            return 2;
        }

        return runFileMode(file, echo, resolvedKey, intelligent, pipeline)
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

    private int runConfigCheck(String openAiKey, boolean liveRequested, boolean intelligentRequested) {
        System.out.println("NEXUS Voice Configuration Check");
        boolean ok = true;

        if (openAiKey == null || openAiKey.isBlank()) {
            System.out.println("❌ OpenAI API key missing. Set NEXUS_OPENAI_KEY or OPENAI_API_KEY, or pass --openai-key=KEY.");
            ok = false;
        } else {
            System.out.println("✅ OpenAI API key detected.");
        }

        if (intelligentRequested) {
            System.out.println("ℹ Intelligent pipeline requested: multi-stage analytics will use Whisper, biometrics heuristics, and emotion detection.");
            if (openAiKey == null || openAiKey.isBlank()) {
                System.out.println("   → Provide a valid key before enabling --intelligent.");
            }
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

        if (intelligentRequested) {
            System.out.println("Run `voice --live --intelligent --echo` for adaptive streaming, add --no-wake-word for continuous capture, or override credentials with --openai-key=KEY.");
        } else {
            System.out.println("Run `voice --live --echo` to start, add --no-wake-word for continuous capture, or pass --openai-key=KEY to override.");
        }
        return ok ? 0 : 1;
    }

    private Optional<Integer> runFileMode(String file, boolean echo, String openAiKey, boolean intelligent, IntelligentVoicePipeline pipeline) {
        Path path = Paths.get(file);
        if (!Files.exists(path)) {
            System.err.println("Audio file not found: " + path.toAbsolutePath());
            return Optional.of(2);
        }

        try {
            if (intelligent) {
                IntelligentVoicePipeline activePipeline = pipeline != null ? pipeline : IntelligentVoicePipeline.createDefault(openAiKey);
                VoiceProcessingResult result = processFileWithPipeline(path, activePipeline);
                Optional<TranscriptionResult> transcriptionOpt = result.getTranscription();
                if (transcriptionOpt.isEmpty() || transcriptionOpt.get().getText().isBlank()) {
                    System.err.println("No speech detected in file.");
                    logHistory(DEFAULT_WAKE_WORD, "", "", null, false, "empty transcript");
                    return Optional.of(3);
                }

                TranscriptionResult transcription = transcriptionOpt.get();
                if (!transcription.isSuccess()) {
                    System.err.println("Transcription failed: " + transcription.getError());
                    logHistory(DEFAULT_WAKE_WORD, "", "", null, false, transcription.getError());
                    return Optional.of(1);
                }

                String transcript = transcription.getText();
                if (echo) {
                    System.out.println("[voice:transcript] " + transcript);
                }
                printIntelligenceInsights(result);
                System.out.println("[voice] -> nl: " + transcript);
                int code = executeCommand(transcript, transcript, DEFAULT_WAKE_WORD);
                return Optional.of(code);
            }

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
        } catch (UnsupportedAudioFileException ex) {
            System.err.println("Unsupported audio format: " + ex.getMessage());
            logHistory(DEFAULT_WAKE_WORD, "", "", null, false, "unsupported audio format");
            return Optional.of(1);
        } catch (IOException ex) {
            System.err.println("Error reading audio file: " + ex.getMessage());
            logHistory(DEFAULT_WAKE_WORD, "", "", null, false, ex.getMessage());
            return Optional.of(1);
        } catch (Exception ex) {
            System.err.println("Failed to process audio file: " + ex.getMessage());
            logHistory(DEFAULT_WAKE_WORD, "", "", null, false, ex.getMessage());
            return Optional.of(1);
        }
    }

    private int runLiveMode(String openAiKey, String wakeWord, boolean echo, boolean noWakeWord, boolean intelligent, IntelligentVoicePipeline pipeline) {
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

        IntelligentVoicePipeline activePipeline = intelligent ? (pipeline != null ? pipeline : IntelligentVoicePipeline.createDefault(openAiKey)) : null;
        WhisperClient whisperClient = intelligent ? null : new WhisperClient(openAiKey);

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

                VoiceProcessingResult processingResult = null;
                TranscriptionResult transcriptionResult = null;
                String transcript;

                if (intelligent) {
                    processingResult = activePipeline.process(audio, recorder.getFormat());
                    if (processingResult == null || !processingResult.isVoiceDetected()) {
                        continue;
                    }
                    Optional<TranscriptionResult> transcriptionOpt = processingResult.getTranscription();
                    if (transcriptionOpt.isEmpty()) {
                        blankTranscriptEvents++;
                        if (blankTranscriptEvents % BLANK_TRANSCRIPT_THRESHOLD == 0) {
                            System.out.println("⚠️  I'm hearing silence or background noise. Move closer to the mic or re-run `voice --check-config`.");
                            logHistory(resolvedWakeWord, "", "", "no-speech", false, "no transcription");
                        }
                        continue;
                    }
                    transcriptionResult = transcriptionOpt.get();
                    transcript = transcriptionResult.getText() == null ? "" : transcriptionResult.getText().trim();
                    if (!transcriptionResult.isSuccess() || transcript.isBlank()) {
                        blankTranscriptEvents++;
                        if (blankTranscriptEvents % BLANK_TRANSCRIPT_THRESHOLD == 0) {
                            System.out.println("⚠️  I'm hearing silence or background noise. Move closer to the mic or re-run `voice --check-config`.");
                            String error = transcriptionResult.getError() == null ? "blank transcript chunk" : transcriptionResult.getError();
                            logHistory(resolvedWakeWord, transcript, "", "no-speech", false, error);
                        }
                        continue;
                    }
                    blankTranscriptEvents = 0;
                } else {
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
                    transcript = transcript.trim();
                    blankTranscriptEvents = 0;
                }

                String delta = tracker.delta(transcript);
                if (echo && !delta.isBlank()) {
                    System.out.println("[voice:transcript] " + delta);
                }

                if (intelligent && processingResult != null && !delta.isBlank()) {
                    printIntelligenceInsights(processingResult);
                }

                boolean wakeWordHeard = !noWakeWord && transcript.toLowerCase(Locale.ROOT).contains(resolvedWakeWord);
                String command = noWakeWord ? transcript : extractCommand(transcript, resolvedWakeWord);
                if (command == null || command.isBlank()) {
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

    private void printIntelligenceInsights(VoiceProcessingResult result) {
        if (result == null) {
            return;
        }

        StringJoiner joiner = new StringJoiner(" | ");
        result.getTranscription().ifPresent(tx -> {
            String provider = tx.getProvider() == null ? "unknown" : tx.getProvider();
            joiner.add(String.format(Locale.ROOT, "provider=%s conf=%.2f", provider, tx.getConfidence()));
            if (tx.getLatency() != null && !tx.getLatency().isZero()) {
                joiner.add(String.format(Locale.ROOT, "latency=%dms", tx.getLatency().toMillis()));
            }
        });

        if (result.getLanguageContext() != null) {
            joiner.add(String.format(Locale.ROOT, "language=%s(%.2f)",
                    result.getLanguageContext().getLocale(), result.getLanguageContext().getConfidence()));
        }

        if (result.getEmotionalContext() != null) {
            joiner.add(String.format(Locale.ROOT, "emotion=%s(%.2f)",
                    result.getEmotionalContext().getLabel(), result.getEmotionalContext().getConfidence()));
        }

        if (result.getSpeakerProfile() != null) {
            joiner.add(String.format(Locale.ROOT, "speaker=%s(%.2f)",
                    result.getSpeakerProfile().getSpeakerId(), result.getSpeakerProfile().getConfidence()));
        }

        joiner.add(String.format(Locale.ROOT, "overall=%.2f", result.getOverallConfidence()));

        String summary = joiner.toString();
        if (!summary.isBlank()) {
            System.out.println("[voice:intel] " + summary);
        }
    }

    private VoiceProcessingResult processFileWithPipeline(Path path, IntelligentVoicePipeline pipeline)
            throws IOException, UnsupportedAudioFileException {
        try (AudioInputStream baseStream = AudioSystem.getAudioInputStream(path.toFile())) {
            AudioFormat baseFormat = baseStream.getFormat();
            AudioFormat targetFormat = defaultPcmFormat();

            if (!baseFormat.matches(targetFormat) && AudioSystem.isConversionSupported(targetFormat, baseFormat)) {
                try (AudioInputStream converted = AudioSystem.getAudioInputStream(targetFormat, baseStream)) {
                    byte[] audioBytes = toByteArray(converted);
                    return pipeline.process(audioBytes, targetFormat);
                }
            }

            byte[] audioBytes = toByteArray(baseStream);
            return pipeline.process(audioBytes, baseFormat);
        }
    }

    private byte[] toByteArray(AudioInputStream stream) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buffer = new byte[8192];
        int read;
        while ((read = stream.read(buffer)) != -1) {
            out.write(buffer, 0, read);
        }
        return out.toByteArray();
    }

    private AudioFormat defaultPcmFormat() {
        return new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, 16_000F, 16, 1, 2, 16_000F, false);
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
