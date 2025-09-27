package com.boozer.nexus.codegen;

import java.time.Instant;
import java.util.Objects;

public class CodeGenerationResult {
    private final String code;
    private final String tests;
    private final String notes;
    private final String model;
    private final Instant generatedAt;

    public CodeGenerationResult(String code, String tests, String notes, String model) {
        this(code, tests, notes, model, Instant.now());
    }

    public CodeGenerationResult(String code, String tests, String notes, String model, Instant generatedAt) {
        this.code = code == null ? "" : code;
        this.tests = tests == null ? "" : tests;
        this.notes = notes == null ? "" : notes;
        this.model = model == null ? "unknown" : model;
        this.generatedAt = Objects.requireNonNullElseGet(generatedAt, Instant::now);
    }

    public String getCode() {
        return code;
    }

    public String getTests() {
        return tests;
    }

    public String getNotes() {
        return notes;
    }

    public String getModel() {
        return model;
    }

    public Instant getGeneratedAt() {
        return generatedAt;
    }

    public boolean hasTests() {
        return !tests.isBlank();
    }

    public boolean hasNotes() {
        return !notes.isBlank();
    }
}
