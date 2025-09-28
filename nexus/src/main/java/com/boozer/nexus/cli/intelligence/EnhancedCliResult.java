package com.boozer.nexus.cli.intelligence;

public final class EnhancedCliResult {
    private final boolean successful;
    private final String insight;
    private final String message;

    private EnhancedCliResult(boolean successful, String insight, String message) {
        this.successful = successful;
        this.insight = insight;
        this.message = message;
    }

    public static EnhancedCliResult success(String insight) {
        return new EnhancedCliResult(true, insight, null);
    }

    public static EnhancedCliResult failure(String message) {
        return new EnhancedCliResult(false, null, message);
    }

    public boolean successful() {
        return successful;
    }

    public String insight() {
        return insight;
    }

    public String message() {
        return message;
    }
}
