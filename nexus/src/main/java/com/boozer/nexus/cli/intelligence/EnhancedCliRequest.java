package com.boozer.nexus.cli.intelligence;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public final class EnhancedCliRequest {
    private final String command;
    private final String mode;
    private final String baseOutput;
    private final Map<String, Object> context;
    private final String apiKey;
    private final String model;
    private final double temperature;

    private EnhancedCliRequest(Builder builder) {
        this.command = builder.command;
        this.mode = builder.mode;
        this.baseOutput = builder.baseOutput;
        this.context = Collections.unmodifiableMap(new LinkedHashMap<>(builder.context));
        this.apiKey = builder.apiKey;
        this.model = builder.model;
        this.temperature = builder.temperature;
    }

    public String command() {
        return command;
    }

    public String mode() {
        return mode;
    }

    public String baseOutput() {
        return baseOutput;
    }

    public Map<String, Object> context() {
        return context;
    }

    public String apiKey() {
        return apiKey;
    }

    public String model() {
        return model;
    }

    public double temperature() {
        return temperature;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private String command;
        private String mode;
        private String baseOutput;
        private Map<String, Object> context = new LinkedHashMap<>();
        private String apiKey;
        private String model = "gpt-4o-mini";
        private double temperature = 0.2;

        private Builder() {
        }

        public Builder command(String command) {
            this.command = command;
            return this;
        }

        public Builder mode(String mode) {
            this.mode = mode;
            return this;
        }

        public Builder baseOutput(String baseOutput) {
            this.baseOutput = baseOutput;
            return this;
        }

        public Builder context(Map<String, Object> context) {
            this.context = context == null ? new LinkedHashMap<>() : new LinkedHashMap<>(context);
            return this;
        }

        public Builder apiKey(String apiKey) {
            this.apiKey = apiKey;
            return this;
        }

        public Builder model(String model) {
            if (model != null && !model.isBlank()) {
                this.model = model;
            }
            return this;
        }

        public Builder temperature(double temperature) {
            if (!Double.isNaN(temperature) && temperature >= 0 && temperature <= 1) {
                this.temperature = temperature;
            }
            return this;
        }

        public EnhancedCliRequest build() {
            Objects.requireNonNull(command, "command");
            String safeOutput = baseOutput == null ? "" : baseOutput;
            Map<String, Object> safeContext = context == null ? new LinkedHashMap<>() : context;
            this.baseOutput = safeOutput;
            this.context = safeContext;
            return new EnhancedCliRequest(this);
        }
    }
}
