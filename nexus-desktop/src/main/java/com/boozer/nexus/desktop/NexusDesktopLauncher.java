package com.boozer.nexus.desktop;

/**
 * Launcher class required by some JavaFX packaging tools to correctly
 * bootstrap the application.
 */
public final class NexusDesktopLauncher {
    private NexusDesktopLauncher() {
    }

    public static void main(String[] args) {
        NexusDesktopApp.main(args);
    }
}
