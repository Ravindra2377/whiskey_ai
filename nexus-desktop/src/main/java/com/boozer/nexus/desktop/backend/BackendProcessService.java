package com.boozer.nexus.desktop.backend;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.IntConsumer;
import java.util.stream.Stream;

/**
 * Bridges the JavaFX desktop to the existing NEXUS CLI by executing commands in a background process
 * and streaming output back to the UI.
 */
public class BackendProcessService implements AutoCloseable {

    public static final String CLI_PATH_PROPERTY = "nexus.cli.path";
    private static final String DEFAULT_JAR_NAME = "nexus.jar";
    private static final String BOOT_JAR_NAME = "nexus-1.0.0.jar";

    private final ExecutorService ioExecutor;
    private final Path workspaceRoot;
    private volatile Path cliJar;
    private final String javaExecutable;

    public BackendProcessService() {
        this(validateWorkingDirectory(Paths.get("")), resolveWorkspaceRoot(Paths.get("")));
    }

    BackendProcessService(Path workingDirectory, Path workspaceRoot) {
        this.workspaceRoot = Objects.requireNonNull(workspaceRoot, "workspaceRoot");
        this.cliJar = resolveCliJar(workspaceRoot);
        this.javaExecutable = resolveJavaExecutable();
        this.ioExecutor = Executors.newCachedThreadPool(new DesktopThreadFactory());
    }

    private static Path validateWorkingDirectory(Path cwd) {
        if (cwd == null) {
            throw new IllegalStateException("Unable to resolve current working directory");
        }
        return cwd.toAbsolutePath();
    }

    private static Path resolveWorkspaceRoot(Path cwd) {
        Path absolute = cwd.toAbsolutePath();
        Path parent = absolute.getParent();
        if (parent != null && parent.getFileName() != null && parent.getFileName().toString().equalsIgnoreCase("nexus-desktop")) {
            // Edge case: running inside build output
            return parent.getParent() != null ? parent.getParent() : absolute;
        }
        if (absolute.getFileName() != null && absolute.getFileName().toString().equalsIgnoreCase("nexus-desktop")) {
            return absolute.getParent() != null ? absolute.getParent() : absolute;
        }
        return absolute;
    }

    private Path resolveCliJar(Path workspace) {
        // 0. Stored preference
        Optional<Path> preferred = CliPreferences.getCliJarPath();
        if (preferred.isPresent()) {
            Path candidate = preferred.get();
            if (!candidate.isAbsolute()) {
                candidate = workspace.resolve(candidate).normalize();
            }
            if (Files.isRegularFile(candidate)) {
                return candidate;
            }
        }

        // 1. Explicit JVM property
        String configured = System.getProperty(CLI_PATH_PROPERTY);
        if (configured != null && !configured.isBlank()) {
            Path candidate = Path.of(configured.trim());
            if (!candidate.isAbsolute()) {
                candidate = workspace.resolve(candidate).normalize();
            }
            if (Files.isRegularFile(candidate)) {
                return candidate;
            }
        }

        // 2. Root-level nexus.jar
        Path directJar = workspace.resolve(DEFAULT_JAR_NAME);
        if (Files.isRegularFile(directJar)) {
            return directJar;
        }

        // 3. Spring Boot jar produced by nexus module
        Path targetJar = workspace.resolve("nexus").resolve("target").resolve(BOOT_JAR_NAME);
        if (Files.isRegularFile(targetJar)) {
            return targetJar;
        }

        // 4. Any jar within nexus/target sorted by newest
        Path targetDir = workspace.resolve("nexus").resolve("target");
        if (Files.isDirectory(targetDir)) {
            try (Stream<Path> stream = Files.list(targetDir)) {
                Optional<Path> jar = stream
                        .filter(p -> Files.isRegularFile(p) && p.getFileName().toString().toLowerCase(Locale.ROOT).endsWith(".jar"))
                        .sorted(Comparator.comparingLong(this::lastModifiedOrZero).reversed())
                        .findFirst();
                if (jar.isPresent()) {
                    return jar.get();
                }
            } catch (IOException ignored) {
                // fall through to next strategy
            }
        }

        // 5. Any jar named nexus*.jar in workspace root
        try (Stream<Path> stream = Files.list(workspace)) {
            Optional<Path> jar = stream
                    .filter(p -> Files.isRegularFile(p) && p.getFileName().toString().toLowerCase(Locale.ROOT).endsWith(".jar"))
                    .filter(p -> p.getFileName().toString().toLowerCase(Locale.ROOT).startsWith("nexus"))
                    .findFirst();
            if (jar.isPresent()) {
                return jar.get();
            }
        } catch (IOException ignored) {
            // ignore
        }
        return null;
    }

    private long lastModifiedOrZero(Path path) {
        try {
            return Files.getLastModifiedTime(path).toMillis();
        } catch (IOException e) {
            return 0L;
        }
    }

    private String resolveJavaExecutable() {
        String javaHome = System.getProperty("java.home");
        if (javaHome != null) {
            Path java = Paths.get(javaHome).resolve("bin").resolve(isWindows() ? "java.exe" : "java");
            if (Files.isRegularFile(java)) {
                return java.toString();
            }
        }
        return "java";
    }

    private boolean isWindows() {
        String os = System.getProperty("os.name", "");
        return os.toLowerCase(Locale.ROOT).contains("win");
    }

    public boolean isCliAvailable() {
        Path jar = this.cliJar;
        return jar != null && Files.isRegularFile(jar);
    }

    public Optional<Path> getCliJar() {
        return Optional.ofNullable(cliJar);
    }

    public String describeCliLocation() {
        Path jar = this.cliJar;
        if (jar != null) {
            return jar.toAbsolutePath().toString();
        }
        String configured = System.getProperty(CLI_PATH_PROPERTY);
        if (configured != null && !configured.isBlank()) {
            return configured + " (configured but missing)";
        }
        return "not found – build the CLI module (gradlew :nexus:bootJar) or set -Dnexus.cli.path";
    }

    public synchronized void refreshCliLocation() {
        this.cliJar = resolveCliJar(workspaceRoot);
    }

    public BackendJob runCliCommand(String displayName, List<String> cliArgs, BackendCallbacks callbacks) {
        Objects.requireNonNull(displayName, "displayName");
        Objects.requireNonNull(cliArgs, "cliArgs");
        Objects.requireNonNull(callbacks, "callbacks");

        Path jar = this.cliJar;
        if (jar == null || !Files.isRegularFile(jar)) {
            IllegalStateException ex = new IllegalStateException("NEXUS CLI jar " + describeCliLocation());
            callbacks.onFailure.accept(ex);
            return BackendJob.failed(ex);
        }

        List<String> command = new ArrayList<>();
        command.add(javaExecutable);
        command.add("-jar");
        command.add(jar.toString());
        command.addAll(cliArgs);
        return runProcess(displayName, command, callbacks);
    }

    public BackendJob runProcess(String displayName, List<String> command, BackendCallbacks callbacks) {
        Objects.requireNonNull(displayName, "displayName");
        Objects.requireNonNull(command, "command");
        Objects.requireNonNull(callbacks, "callbacks");

        callbacks.onStatus.accept("Launching " + displayName + "…");
        ProcessBuilder builder = new ProcessBuilder(command);
        builder.directory(workspaceRoot.toFile());
        builder.redirectErrorStream(false);
        try {
            Process process = builder.start();
            CompletableFuture<Integer> completion = new CompletableFuture<>();

            ioExecutor.submit(() -> streamLines(process.getInputStream(), callbacks.onStdout));
            ioExecutor.submit(() -> streamLines(process.getErrorStream(), callbacks.onStderr));
            ioExecutor.submit(() -> {
                try {
                    int exit = process.waitFor();
                    callbacks.onComplete.accept(exit);
                    completion.complete(exit);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    completion.completeExceptionally(e);
                }
            });

            return new BackendJob(process, completion);
        } catch (IOException ex) {
            callbacks.onFailure.accept(ex);
            return BackendJob.failed(ex);
        }
    }

    private void streamLines(InputStream stream, Consumer<String> consumer) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                consumer.accept(line);
            }
        } catch (IOException ignored) {
            // Stream closed – ignore.
        }
    }

    @Override
    public void close() {
        ioExecutor.shutdownNow();
        try {
            if (!ioExecutor.awaitTermination(500, TimeUnit.MILLISECONDS)) {
                ioExecutor.shutdownNow();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private static final class DesktopThreadFactory implements ThreadFactory {
        private int count = 0;

        @Override
        public synchronized Thread newThread(Runnable r) {
            Thread thread = new Thread(r, "nexus-desktop-cli-" + (++count));
            thread.setDaemon(true);
            return thread;
        }
    }

    public static final class BackendJob {
        private final Process process;
        private final CompletableFuture<Integer> completion;

        private BackendJob(Process process, CompletableFuture<Integer> completion) {
            this.process = process;
            this.completion = completion;
        }

        public boolean isRunning() {
            return process != null && process.isAlive();
        }

        public CompletableFuture<Integer> completion() {
            return completion;
        }

        public void cancel() {
            if (process == null) {
                return;
            }
            process.destroy();
            try {
                if (!process.waitFor(250, TimeUnit.MILLISECONDS)) {
                    process.destroyForcibly();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                process.destroyForcibly();
            }
        }

        private static BackendJob failed(Throwable throwable) {
            CompletableFuture<Integer> future = new CompletableFuture<>();
            future.completeExceptionally(throwable);
            return new BackendJob(null, future);
        }
    }

    public static final class BackendCallbacks {
        private final Consumer<String> onStatus;
        private final Consumer<String> onStdout;
        private final Consumer<String> onStderr;
        private final IntConsumer onComplete;
        private final Consumer<Throwable> onFailure;

        private BackendCallbacks(Consumer<String> onStatus,
                                 Consumer<String> onStdout,
                                 Consumer<String> onStderr,
                                 IntConsumer onComplete,
                                 Consumer<Throwable> onFailure) {
            this.onStatus = onStatus;
            this.onStdout = onStdout;
            this.onStderr = onStderr;
            this.onComplete = onComplete;
            this.onFailure = onFailure;
        }

        public static Builder builder() {
            return new Builder();
        }

        public static final class Builder {
            private Consumer<String> onStatus = s -> {};
            private Consumer<String> onStdout = s -> {};
            private Consumer<String> onStderr = s -> {};
            private IntConsumer onComplete = exit -> {};
            private Consumer<Throwable> onFailure = ex -> {};

            private Builder() {}

            public Builder onStatus(Consumer<String> consumer) {
                this.onStatus = Objects.requireNonNull(consumer);
                return this;
            }

            public Builder onStdout(Consumer<String> consumer) {
                this.onStdout = Objects.requireNonNull(consumer);
                return this;
            }

            public Builder onStderr(Consumer<String> consumer) {
                this.onStderr = Objects.requireNonNull(consumer);
                return this;
            }

            public Builder onComplete(IntConsumer consumer) {
                this.onComplete = Objects.requireNonNull(consumer);
                return this;
            }

            public Builder onFailure(Consumer<Throwable> consumer) {
                this.onFailure = Objects.requireNonNull(consumer);
                return this;
            }

            public BackendCallbacks build() {
                return new BackendCallbacks(onStatus, onStdout, onStderr, onComplete, onFailure);
            }
        }
    }
}
