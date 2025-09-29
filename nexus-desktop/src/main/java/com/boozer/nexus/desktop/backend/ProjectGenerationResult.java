package com.boozer.nexus.desktop.backend;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/** Result payload from GPT-4 describing the generated project files. */
public final class ProjectGenerationResult {
    private final String projectName;
    private final String summary;
    private final String model;
    private final Instant generatedAt;
    private final List<GeneratedFile> files;
    private final List<String> notes;

    public ProjectGenerationResult(String projectName,
                                   String summary,
                                   String model,
                                   Instant generatedAt,
                                   List<GeneratedFile> files,
                                   List<String> notes) {
        this.projectName = Objects.requireNonNull(projectName, "projectName").trim();
        this.summary = summary == null ? "" : summary.trim();
        this.model = model == null ? "" : model.trim();
        this.generatedAt = generatedAt == null ? Instant.now() : generatedAt;
        if (this.projectName.isEmpty()) {
            throw new IllegalArgumentException("Project name must not be blank");
        }
        this.files = Collections.unmodifiableList(new ArrayList<>(Objects.requireNonNull(files, "files")));
        this.notes = Collections.unmodifiableList(new ArrayList<>(notes == null ? List.of() : notes));
    }

    public String getProjectName() {
        return projectName;
    }

    public String getSummary() {
        return summary;
    }

    public String getModel() {
        return model;
    }

    public Instant getGeneratedAt() {
        return generatedAt;
    }

    public List<GeneratedFile> getFiles() {
        return files;
    }

    public List<String> getNotes() {
        return notes;
    }

    public boolean hasNotes() {
        return !notes.isEmpty();
    }
}
