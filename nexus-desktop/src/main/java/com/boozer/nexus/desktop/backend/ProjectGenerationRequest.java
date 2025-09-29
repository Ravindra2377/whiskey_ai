package com.boozer.nexus.desktop.backend;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Immutable request model describing a GPT-4 powered project generation run.
 */
public final class ProjectGenerationRequest {
    private final String description;
    private final List<String> featureTags;
    private final String projectNameHint;
    private final boolean includeTests;
    private final String targetStack;
    private final GenerationSource source;

    private ProjectGenerationRequest(Builder builder) {
        this.description = Objects.requireNonNull(builder.description, "description").trim();
        if (this.description.isEmpty()) {
            throw new IllegalArgumentException("Description must not be blank");
        }
        this.featureTags = Collections.unmodifiableList(new ArrayList<>(builder.featureTags));
        this.projectNameHint = builder.projectNameHint;
        this.includeTests = builder.includeTests;
        this.targetStack = builder.targetStack;
        this.source = builder.source;
    }

    public String getDescription() {
        return description;
    }

    public List<String> getFeatureTags() {
        return featureTags;
    }

    public String getProjectNameHint() {
        return projectNameHint;
    }

    public boolean isIncludeTests() {
        return includeTests;
    }

    public String getTargetStack() {
        return targetStack;
    }

    public GenerationSource getSource() {
        return source;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private String description;
        private final List<String> featureTags = new ArrayList<>();
        private String projectNameHint;
        private boolean includeTests;
        private String targetStack;
        private GenerationSource source = GenerationSource.GUI_BUTTON;

        private Builder() {
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder addFeatureTag(String tag) {
            if (tag != null && !tag.isBlank()) {
                featureTags.add(tag.trim());
            }
            return this;
        }

        public Builder featureTags(List<String> tags) {
            featureTags.clear();
            if (tags != null) {
                tags.stream().filter(Objects::nonNull).map(String::trim).filter(s -> !s.isBlank()).forEach(featureTags::add);
            }
            return this;
        }

        public Builder projectNameHint(String projectNameHint) {
            this.projectNameHint = projectNameHint;
            return this;
        }

        public Builder includeTests(boolean includeTests) {
            this.includeTests = includeTests;
            return this;
        }

        public Builder targetStack(String targetStack) {
            this.targetStack = targetStack;
            return this;
        }

        public Builder source(GenerationSource source) {
            if (source != null) {
                this.source = source;
            }
            return this;
        }

        public ProjectGenerationRequest build() {
            return new ProjectGenerationRequest(this);
        }
    }
}
