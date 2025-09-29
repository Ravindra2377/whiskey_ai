package com.boozer.nexus.desktop.backend;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Objects;

/**
 * Persists generated project files into the developer workspace.
 */
public final class GeneratedProjectWriter {
    private static final DateTimeFormatter TIMESTAMP = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss")
            .withZone(ZoneId.systemDefault());

    private final Path workspaceRoot;

    public GeneratedProjectWriter(Path workspaceRoot) {
        this.workspaceRoot = Objects.requireNonNull(workspaceRoot, "workspaceRoot").toAbsolutePath().normalize();
    }

    public Path write(ProjectGenerationResult result) throws IOException {
        Objects.requireNonNull(result, "result");
        Path base = workspaceRoot.resolve("generated-apps");
        Files.createDirectories(base);

        String safeName = sanitize(result.getProjectName());
        Path target = base.resolve(TIMESTAMP.format(result.getGeneratedAt()) + "-" + safeName).normalize();
        Files.createDirectories(target);

        for (GeneratedFile file : result.getFiles()) {
            Path resolved = target.resolve(file.getPath()).normalize();
            if (!resolved.startsWith(target)) {
                throw new IOException("Illegal file path returned by AI: " + file.getPath());
            }
            Path parent = resolved.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            Files.write(resolved, file.getBytes());
        }

        // Write summary file for audit trail.
        Path summary = target.resolve("GENERATION_SUMMARY.md");
        StringBuilder summaryContent = new StringBuilder();
        summaryContent.append("# Generation Summary\n\n");
        summaryContent.append("**Project:** ").append(result.getProjectName()).append("\n\n");
        summaryContent.append("**Model:** ").append(result.getModel()).append("\n\n");
        summaryContent.append("**Generated At:** ").append(result.getGeneratedAt()).append("\n\n");
        summaryContent.append(result.getSummary()).append("\n");
        if (result.hasNotes()) {
            summaryContent.append("\n## Notes\n");
            result.getNotes().forEach(note -> summaryContent.append("- ").append(note).append("\n"));
        }
        Files.writeString(summary, summaryContent.toString(), StandardCharsets.UTF_8);

        return target;
    }

    private String sanitize(String name) {
        String base = name == null || name.isBlank() ? "generated-project" : name.toLowerCase(Locale.ROOT);
        base = base.replaceAll("[^a-z0-9]+", "-");
        base = base.replaceAll("-+", "-");
        base = base.replaceAll("^-|-$", "");
        if (base.isBlank()) {
            base = "project";
        }
        return base;
    }
}
