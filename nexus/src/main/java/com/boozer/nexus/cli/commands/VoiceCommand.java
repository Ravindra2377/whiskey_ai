package com.boozer.nexus.cli.commands;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;

public class VoiceCommand implements Command {
    @Override
    public String name() { return "voice"; }

    @Override
    public String description() {
        return "Voice assistant (stub): transcribe an audio file and execute the parsed intent. Usage: voice --file=audio.wav [--echo]";
    }

    @Override
    public int run(String[] args) throws Exception {
        String file = null;
        boolean echo = false;
        for (String a : args) {
            if (a.startsWith("--file=")) file = a.substring("--file=".length());
            if (a.equals("--echo")) echo = true;
        }
        if (file == null) {
            System.err.println("Usage: voice --file=audio.wav [--echo]");
            return 2;
        }
        Path p = Paths.get(file);
        if (!Files.exists(p)) {
            System.err.println("Audio file not found: " + p.toAbsolutePath());
            return 2;
        }
        // Stub transcription (replace with Whisper or Azure later)
        String transcript = simulateTranscription(p);
        if (echo) System.out.println("[voice:transcript] " + transcript);

        // Reuse NL command to parse and execute
        System.out.println("[voice] -> nl: " + transcript);
        return new NaturalLanguageCommand().run(new String[]{ transcript });
    }

    private String simulateTranscription(Path audio) {
        // MVP: simple mapping by filename keywords
        String n = audio.getFileName().toString().toLowerCase(Locale.ROOT);
        if (n.contains("ingest")) return "ingest operations";
        if (n.contains("list") && n.contains("python")) return "list python scripts";
        if (n.contains("run") && n.contains("infra")) return "run infra tasks";
        return "catalog summary";
    }
}
