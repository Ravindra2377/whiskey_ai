package com.boozer.nexus.desktop.backend;

import java.nio.file.Path;
import java.util.Optional;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

/**
 * Persists user-configured preferences for the desktop shell, including the
 * location of the NEXUS CLI jar used for backend delegation.
 */
public final class CliPreferences {
    private static final String KEY_CLI_PATH = "cliJarPath";
    private static final Preferences PREFS = Preferences.userNodeForPackage(CliPreferences.class);

    private CliPreferences() {
    }

    public static Optional<Path> getCliJarPath() {
        try {
            String value = PREFS.get(KEY_CLI_PATH, null);
            if (value == null || value.isBlank()) {
                return Optional.empty();
            }
            return Optional.of(Path.of(value));
        } catch (Exception ex) {
            return Optional.empty();
        }
    }

    public static void setCliJarPath(Path path) {
        if (path == null) {
            clearCliJarPath();
            return;
        }
        try {
            PREFS.put(KEY_CLI_PATH, path.toString());
        } catch (Exception ignored) {
            // Ignore preference write failures; the system property will still carry the value.
        }
    }

    public static void clearCliJarPath() {
        try {
            PREFS.remove(KEY_CLI_PATH);
        } catch (Exception ignored) {
            // Ignore preference clearing errors
        }
    }

    public static void flush() {
        try {
            PREFS.flush();
        } catch (BackingStoreException ignored) {
            // No-op
        }
    }
}
