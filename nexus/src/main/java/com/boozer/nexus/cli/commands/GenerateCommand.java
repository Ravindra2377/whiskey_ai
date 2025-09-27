package com.boozer.nexus.cli.commands;

import com.boozer.nexus.codegen.CodeGenerationRequest;
import com.boozer.nexus.codegen.CodeGenerationResult;
import com.boozer.nexus.codegen.CodeGenerationService;
import com.boozer.nexus.codegen.OpenAiChatClient;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class GenerateCommand implements Command {
    @Override
    public String name() {
        return "generate";
    }

    @Override
    public String description() {
        return "AI code generation (requires OpenAI key). Usage: generate --spec " +
                "\"Create a REST API\" [--language=java] [--include-tests]";
    }

    @Override
    public int run(String[] args) throws Exception {
        if (args.length == 0) {
            printUsage();
            return 2;
        }

        String spec = null;
        String language = null;
        String model = null;
        double temperature = 0;
        boolean includeTests = false;
        boolean includeComments = false;
        String output = null;
        String testsOutput = null;
        String openAiKey = null;
        List<String> remaining = new ArrayList<>();

        for (String arg : args) {
            if (arg.equals("--help") || arg.equals("-h")) {
                printUsage();
                return 0;
            } else if (arg.startsWith("--spec=")) {
                spec = arg.substring("--spec=".length()).trim();
            } else if (arg.startsWith("--language=")) {
                language = arg.substring("--language=".length()).trim();
            } else if (arg.startsWith("--model=")) {
                model = arg.substring("--model=".length()).trim();
            } else if (arg.startsWith("--temperature=")) {
                temperature = parseTemperature(arg.substring("--temperature=".length()));
            } else if (arg.equals("--include-tests") || arg.equals("--tests")) {
                includeTests = true;
            } else if (arg.equals("--include-comments")) {
                includeComments = true;
            } else if (arg.startsWith("--output=")) {
                output = arg.substring("--output=".length()).trim();
            } else if (arg.startsWith("--tests-output=")) {
                testsOutput = arg.substring("--tests-output=".length()).trim();
            } else if (arg.startsWith("--openai-key=")) {
                openAiKey = arg.substring("--openai-key=".length()).trim();
            } else {
                remaining.add(arg);
            }
        }

        if ((spec == null || spec.isBlank()) && !remaining.isEmpty()) {
            spec = String.join(" ", remaining);
        }

        if (spec == null || spec.isBlank()) {
            System.err.println("Specification is required. Provide via --spec=... or as free-form argument.");
            printUsage();
            return 2;
        }

        if (openAiKey == null || openAiKey.isBlank()) {
            openAiKey = System.getenv("NEXUS_OPENAI_KEY");
        }
        if (openAiKey == null || openAiKey.isBlank()) {
            System.err.println("OpenAI API key missing. Use --openai-key=KEY or set NEXUS_OPENAI_KEY environment variable.");
            return 2;
        }

        CodeGenerationRequest request = new CodeGenerationRequest(
                spec,
                language,
                includeTests,
                includeComments,
                model,
                temperature
        );

        CodeGenerationService service = new CodeGenerationService(new OpenAiChatClient(openAiKey));
        CodeGenerationResult result = service.generate(request);

        System.out.println("NEXUS AI code generation complete.");
        System.out.println("Model: " + result.getModel());
        System.out.println("Generated: " + DateTimeFormatter.ISO_INSTANT.format(result.getGeneratedAt()));
        System.out.println();

        if (output != null && !output.isBlank()) {
            writeToFile(Path.of(output), result.getCode());
            System.out.println("Implementation written to " + Path.of(output).toAbsolutePath());
        } else {
            System.out.println("=== Implementation ===");
            System.out.println(result.getCode());
        }

        if (result.hasTests()) {
            if (testsOutput != null && !testsOutput.isBlank()) {
                writeToFile(Path.of(testsOutput), result.getTests());
                System.out.println("Tests written to " + Path.of(testsOutput).toAbsolutePath());
            } else {
                System.out.println("=== Tests ===");
                System.out.println(result.getTests());
            }
        }

        if (result.hasNotes()) {
            System.out.println("=== Notes ===");
            System.out.println(result.getNotes());
        }

        return 0;
    }

    private void printUsage() {
        System.out.println("Usage: generate --spec \"Build a REST API\" [--language=java] [--include-tests] " +
                "[--include-comments] [--model=gpt-4o-mini] [--temperature=0.2] [--output=File] [--tests-output=File]");
    }

    private double parseTemperature(String value) {
        try {
            double t = Double.parseDouble(value);
            if (t < 0.0 || t > 1.0) {
                System.err.println("Temperature must be between 0 and 1. Using default 0.2.");
                return 0.2;
            }
            return t;
        } catch (NumberFormatException ex) {
            System.err.println("Invalid temperature value. Using default 0.2.");
            return 0.2;
        }
    }

    private void writeToFile(Path path, String content) throws Exception {
        if (content == null || content.isBlank()) {
            return;
        }
        Path parent = path.getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }
        Files.writeString(path, content, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE);
    }
}
