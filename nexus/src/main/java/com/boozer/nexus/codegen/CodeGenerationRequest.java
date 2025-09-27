package com.boozer.nexus.codegen;

public class CodeGenerationRequest {
    private final String specification;
    private final String language;
    private final boolean includeTests;
    private final boolean includeComments;
    private final String model;
    private final double temperature;

    public CodeGenerationRequest(String specification, String language, boolean includeTests, boolean includeComments, String model, double temperature) {
        this.specification = specification;
        this.language = language;
        this.includeTests = includeTests;
        this.includeComments = includeComments;
        this.model = model;
        this.temperature = temperature;
    }

    public String specification() {
        return specification;
    }

    public String language() {
        return language;
    }

    public boolean includeTests() {
        return includeTests;
    }

    public boolean includeComments() {
        return includeComments;
    }

    public String model() {
        return model;
    }

    public double temperature() {
        return temperature;
    }

    public String resolvedModel() {
        return (model == null || model.isBlank()) ? "gpt-4o-mini" : model;
    }

    public double resolvedTemperature() {
        return temperature <= 0 ? 0.2 : temperature;
    }

    public String resolvedLanguage() {
        return (language == null || language.isBlank()) ? "java" : language;
    }
}
