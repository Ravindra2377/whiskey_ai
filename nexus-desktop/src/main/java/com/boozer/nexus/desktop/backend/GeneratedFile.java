package com.boozer.nexus.desktop.backend;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

/** Represents an individual generated file within a project scaffold. */
public final class GeneratedFile {
    private final String path;
    private final String content;

    public GeneratedFile(String path, String content) {
        this.path = Objects.requireNonNull(path, "path").trim();
        if (this.path.isEmpty()) {
            throw new IllegalArgumentException("File path must not be blank");
        }
        this.content = content == null ? "" : content;
    }

    public String getPath() {
        return path;
    }

    public String getContent() {
        return content;
    }

    public byte[] getBytes() {
        return content.getBytes(StandardCharsets.UTF_8);
    }
}
