package com.boozer.nexus.desktop.ui;

import com.boozer.nexus.desktop.backend.BackendProcessService;
import com.boozer.nexus.desktop.backend.CliPreferences;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Separator;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.ToolBar;
import javafx.scene.control.Tooltip;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import javafx.util.Duration;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Builds the primary desktop layout including top menu/tool bar, navigation,
 * main workspace, contextual insights and status bar.
 */
public class DesktopShell {
    private final BorderPane root = new BorderPane();
    private final Label statusText = new Label("Ready. Welcome to NEXUS AI Desktop.");
    private final Label environmentText = new Label("Environment: Not connected");
    private final Label clockText = new Label();
    private final Label pipelineStatusLabel = insightLabel("• Generator idle – queue a run from the toolbar");
    private final Label voiceStatusLabel = insightLabel("• Voice assistant idle – run a configuration check");
    private final Label healthStatusLabel = insightLabel("• Health check pending");
    private final Label diagnosticsStatusLabel = insightLabel("• Diagnostics not executed yet");
    private final Label activityStatusLabel = insightLabel("• No recent CLI activity");
    private final WelcomeView welcomeView;
    private final ScheduledExecutorService backgroundScheduler = Executors.newSingleThreadScheduledExecutor(r -> {
        Thread thread = new Thread(r, "nexus-desktop-background");
        thread.setDaemon(true);
        return thread;
    });
    private final BackendProcessService backendService;
    private final ConsoleView consoleView = new ConsoleView();
    private final Map<String, BackendProcessService.BackendJob> activeJobs = new HashMap<>();
    private final boolean ownsBackendService;

    public DesktopShell() {
        this(new BackendProcessService(), true);
    }

    public DesktopShell(BackendProcessService backendService) {
        this(backendService, false);
    }

    private DesktopShell(BackendProcessService backendService, boolean ownsBackendService) {
        this.backendService = Objects.requireNonNull(backendService, "backendService");
        this.ownsBackendService = ownsBackendService;
        this.welcomeView = new WelcomeView(this::updateStatus, this::onPrimaryActionRequested);

        root.getStyleClass().add("root-pane");
        root.setPadding(new Insets(0));
        root.setTop(buildTopBar());
        root.setLeft(buildNavigationPanel());
        BorderPane mainContent = new BorderPane();
        mainContent.setCenter(welcomeView.getView());
        mainContent.setBottom(consoleView.getView());
        BorderPane.setMargin(consoleView.getView(), new Insets(12, 0, 0, 0));
        root.setCenter(mainContent);
        root.setRight(buildInsightPanel());
        root.setBottom(buildStatusBar());

        BorderPane.setMargin(root.getLeft(), new Insets(0, 0, 0, 0));
        BorderPane.setMargin(root.getRight(), new Insets(0, 12, 0, 0));
        BorderPane.setMargin(root.getCenter(), new Insets(12));

        startClock();
    }

    public Parent getRoot() {
        return root;
    }

    /**
     * Requests keyboard focus for the welcome screen's primary action to make keyboard usage fast.
     */
    public void focusWelcomePrimaryAction() {
        welcomeView.requestPrimaryActionFocus();
    }

    private VBox buildTopBar() {
        MenuBar menuBar = createMenuBar();
        ToolBar toolBar = createToolBar();

        VBox container = new VBox(menuBar, toolBar);
        container.getStyleClass().add("top-bar");
        return container;
    }

    private MenuBar createMenuBar() {
        Menu fileMenu = new Menu("File");
        MenuItem newProject = new MenuItem("New Project…");
        newProject.setOnAction(e -> updateStatus("Launching project creation wizard"));
        MenuItem openProject = new MenuItem("Open Project…");
        openProject.setOnAction(e -> updateStatus("Opening project selector"));
        MenuItem preferences = new MenuItem("Preferences");
        preferences.setOnAction(e -> updateStatus("Preferences dialog coming soon"));
    MenuItem cliLocation = new MenuItem("Set NEXUS CLI Location…");
    cliLocation.setOnAction(e -> openCliLocationChooser());
        MenuItem exit = new MenuItem("Exit");
        exit.setOnAction(e -> Platform.exit());
    fileMenu.getItems().addAll(newProject, openProject, preferences, cliLocation, new SeparatorMenuItem(), exit);

        Menu generateMenu = new Menu("Generate");
        MenuItem generateApp = new MenuItem("Generate Application");
        generateApp.setOnAction(e -> triggerGenerationPrep());
        MenuItem templateGallery = new MenuItem("Open Template Gallery");
        templateGallery.setOnAction(e -> updateStatus("Loading full-stack template previews"));
        MenuItem voiceGen = new MenuItem("Voice-Powered Session");
        voiceGen.setOnAction(e -> launchVoiceWorkflow());
        generateMenu.getItems().addAll(generateApp, templateGallery, voiceGen);

        Menu companyMenu = new Menu("Company");
        MenuItem connectCompany = new MenuItem("Connect to Company Server");
        connectCompany.setOnAction(e -> simulateEnvironmentConnection());
        MenuItem manageEnvironments = new MenuItem("Manage Environments");
        manageEnvironments.setOnAction(e -> updateStatus("Environment manager is under construction"));
        MenuItem deploymentDashboard = new MenuItem("Deployment Dashboard");
        deploymentDashboard.setOnAction(e -> updateStatus("Deployment dashboard coming soon"));
        companyMenu.getItems().addAll(connectCompany, manageEnvironments, deploymentDashboard);

        Menu helpMenu = new Menu("Help");
        MenuItem documentation = new MenuItem("View Documentation");
        documentation.setOnAction(e -> updateStatus("Opening documentation in browser"));
        MenuItem diagnostics = new MenuItem("Run Diagnostics");
    diagnostics.setOnAction(e -> runDiagnosticsSweep());
        MenuItem about = new MenuItem("About NEXUS AI Desktop");
        about.setOnAction(e -> updateStatus("NEXUS AI Desktop 0.1.0 – Enterprise Preview"));
        helpMenu.getItems().addAll(documentation, diagnostics, new SeparatorMenuItem(), about);

        MenuBar menuBar = new MenuBar(fileMenu, generateMenu, companyMenu, helpMenu);
        menuBar.getStyleClass().add("menu-bar");
        return menuBar;
    }

    private ToolBar createToolBar() {
        Button newProjectButton = primaryToolbarButton("New", "Create a new AI project", () ->
                updateStatus("New AI-assisted project creation starting"));
        Button generateButton = primaryToolbarButton("Generate", "Launch AI code generation", () ->
        triggerGenerationPrep());
        Button voiceButton = primaryToolbarButton("Voice", "Open voice development assistant", () ->
        launchVoiceWorkflow());
        Button deployButton = primaryToolbarButton("Deploy", "Deploy to enterprise infrastructure", () ->
                updateStatus("Preparing enterprise deployment pipeline"));
    Button healthButton = primaryToolbarButton("Health", "Check backend availability", this::runBackendHealthCheck);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label stageLabel = new Label("Enterprise Preview");
        stageLabel.getStyleClass().add("build-stage-chip");

    ToolBar toolBar = new ToolBar(newProjectButton, generateButton, voiceButton, deployButton, healthButton,
                new Separator(Orientation.VERTICAL), spacer, stageLabel);
        toolBar.getStyleClass().add("command-toolbar");
        return toolBar;
    }

    private Button primaryToolbarButton(String text, String tooltip, Runnable action) {
        Button button = new Button(text);
        button.getStyleClass().add("toolbar-button");
        button.setTooltip(new Tooltip(tooltip));
        button.setOnAction(e -> action.run());
        return button;
    }

    private VBox buildNavigationPanel() {
        Label heading = new Label("Workspaces");
        heading.getStyleClass().add("panel-heading");

        ListView<String> projects = new ListView<>(FXCollections.observableArrayList(
                "AI Accelerators",
                "Enterprise Templates",
                "Company Integrations",
                "Voice Development Sessions",
                "Deployment Playbooks"
        ));
        projects.getSelectionModel().selectFirst();
        projects.getStyleClass().add("navigation-list");

        Button addWorkspace = new Button("Create Workspace");
        addWorkspace.getStyleClass().add("accent-button");
        addWorkspace.setMaxWidth(Double.MAX_VALUE);
        addWorkspace.setOnAction(e -> updateStatus("Workspace creation wizard queued"));

        projects.getSelectionModel().selectedItemProperty().addListener(this::onWorkspaceSelected);
        projects.setOnMouseClicked(e -> {
            String selected = projects.getSelectionModel().getSelectedItem();
            if (selected != null) {
                updateStatus("Workspace " + selected + " ready for AI collaboration");
            }
        });

        VBox container = new VBox(heading, projects, addWorkspace);
        container.getStyleClass().add("navigation-panel");
        VBox.setVgrow(projects, Priority.ALWAYS);
        container.setPrefWidth(240);
        return container;
    }

    private VBox buildInsightPanel() {
        Label analyticsHeading = new Label("Today at a glance");
        analyticsHeading.getStyleClass().add("panel-heading");

        TitledPane pipelinePane = new TitledPane("AI Generation Pipeline", createInsightContent(
                pipelineStatusLabel,
                insightLabel("• Template compiler ready"),
                insightLabel("• Voice-to-app workflow in preview")
        ));
        pipelinePane.setExpanded(true);

        TitledPane infraPane = new TitledPane("Infrastructure", createInsightContent(
                healthStatusLabel,
                insightLabel("• Company VPN: Pending"),
                insightLabel("• Deployment agents: Provisioned")
        ));
        infraPane.setExpanded(false);

        TitledPane activityPane = new TitledPane("Recent Activity", createInsightContent(
                activityStatusLabel,
                voiceStatusLabel,
                diagnosticsStatusLabel
        ));
        activityPane.setExpanded(true);

        VBox container = new VBox(analyticsHeading, pipelinePane, infraPane, activityPane);
        container.getStyleClass().add("insight-panel");
        return container;
    }

    private VBox createInsightContent(Label... labels) {
        VBox box = new VBox();
        box.getStyleClass().add("insight-content");
        for (Label label : labels) {
            box.getChildren().add(label);
        }
        return box;
    }

    private Label insightLabel(String text) {
        Label label = new Label(text);
        label.getStyleClass().add("insight-item");
        return label;
    }

    private void updateInsightLabel(Label label, String message) {
        Platform.runLater(() -> label.setText(ensureBullet(truncate(message, 90))));
    }

    private void appendActivity(String message) {
        updateInsightLabel(activityStatusLabel, message);
        consoleView.appendInfo(message);
    }

    private void updateEnvironment(String message) {
        Platform.runLater(() -> environmentText.setText(message));
    }

    private String ensureBullet(String message) {
        String trimmed = message == null ? "" : message.trim();
        if (trimmed.startsWith("•")) {
            return trimmed;
        }
        return "• " + trimmed;
    }

    private String truncate(String message, int maxLength) {
        if (message == null || message.length() <= maxLength) {
            return message == null ? "" : message;
        }
        return message.substring(0, Math.max(0, maxLength - 1)).trim() + "…";
    }

    private HBox buildStatusBar() {
        HBox container = new HBox(12);
        container.getStyleClass().add("status-bar");
        container.setAlignment(Pos.CENTER_LEFT);
        container.setPadding(new Insets(8, 16, 8, 16));

        statusText.getStyleClass().add("status-primary-text");
        environmentText.getStyleClass().add("status-secondary-text");
        clockText.getStyleClass().add("status-clock-text");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        container.getChildren().addAll(statusText, new Separator(Orientation.VERTICAL), environmentText, spacer, clockText);
        return container;
    }

    private void startClock() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, MMM d yyyy - HH:mm:ss");
        clockText.setText(LocalDateTime.now().format(formatter));
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), event ->
                clockText.setText(LocalDateTime.now().format(formatter))));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    private void updateStatus(String message) {
        Platform.runLater(() -> statusText.setText(message));
    }

    private void onPrimaryActionRequested() {
        updateStatus("AI application generator is queued for implementation");
    }

    private void openCliLocationChooser() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Select NEXUS CLI Jar");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Executable Jar (*.jar)", "*.jar"));

        Path seedPath = backendService.getCliJar()
                .or(() -> CliPreferences.getCliJarPath())
                .orElse(null);
        if (seedPath != null) {
            Path directory = Files.isDirectory(seedPath) ? seedPath : seedPath.getParent();
            if (directory != null && Files.isDirectory(directory)) {
                chooser.setInitialDirectory(directory.toFile());
            }
            if (!Files.isDirectory(seedPath) && seedPath.getFileName() != null) {
                chooser.setInitialFileName(seedPath.getFileName().toString());
            }
        } else {
            Path workspace = Paths.get("").toAbsolutePath();
            if (Files.isDirectory(workspace)) {
                chooser.setInitialDirectory(workspace.toFile());
            }
        }

        Window window = root.getScene() != null ? root.getScene().getWindow() : null;
        File file = chooser.showOpenDialog(window);
        if (file == null) {
            consoleView.appendInfo("CLI location dialog dismissed");
            return;
        }

        Path selected = file.toPath();
        if (!Files.isRegularFile(selected)) {
            updateStatus("Selected file is not a valid jar: " + selected.getFileName());
            consoleView.appendWarning("CLI location rejected (not a file): " + selected);
            appendActivity("CLI selection failed: not a file");
            return;
        }

        Path absolute = selected.toAbsolutePath();
        System.setProperty(BackendProcessService.CLI_PATH_PROPERTY, absolute.toString());
        CliPreferences.setCliJarPath(absolute);
        CliPreferences.flush();
        backendService.refreshCliLocation();

        if (backendService.isCliAvailable()) {
            String message = "CLI path updated to " + absolute.getFileName();
            updateStatus(message);
            appendActivity(message);
            consoleView.appendInfo("CLI jar set to " + absolute);
            updateInsightLabel(pipelineStatusLabel, "Configured CLI jar: " + absolute.getFileName());
            updateInsightLabel(voiceStatusLabel, "Voice assistant ready – CLI configured");
            updateInsightLabel(healthStatusLabel, "Health checks available via CLI");
        } else {
            String message = "Configured CLI jar could not be accessed. See console for details.";
            updateStatus(message);
            appendActivity("CLI jar missing after selection: " + absolute);
            consoleView.appendWarning("CLI jar not accessible: " + absolute);
        }
    }

    private void triggerGenerationPrep() {
        if (!backendService.isCliAvailable()) {
            String message = "NEXUS CLI jar missing (" + backendService.describeCliLocation() + ")";
            updateStatus(message);
            updateInsightLabel(pipelineStatusLabel, "CLI unavailable – build the backend module");
            appendActivity("Generation skipped: CLI jar not found");
            consoleView.appendError(message);
            return;
        }

        updateStatus("Launching operations catalog summary via CLI…");
        updateInsightLabel(pipelineStatusLabel, "Catalog summary running…");
        appendActivity("Running `catalog summary` via CLI");
        List<String> args = List.of("catalog", "summary");
        logCommand("catalog summary", args);

        BackendProcessService.BackendCallbacks callbacks = BackendProcessService.BackendCallbacks.builder()
                .onStatus(this::updateStatus)
                .onStdout(line -> {
                    updateInsightLabel(pipelineStatusLabel, "Catalog: " + truncate(line, 70));
                    appendActivity("catalog: " + truncate(line, 80));
                    consoleView.appendStdout("catalog", line);
                })
                .onStderr(line -> {
                    updateInsightLabel(pipelineStatusLabel, "Catalog error: " + truncate(line, 70));
                    appendActivity("catalog (err): " + truncate(line, 80));
                    consoleView.appendStderr("catalog", line);
                })
                .onComplete(exit -> {
                    if (exit == 0) {
                        updateStatus("Catalog summary completed successfully.");
                        updateInsightLabel(pipelineStatusLabel, "Catalog summary complete");
                        consoleView.appendInfo("catalog summary completed successfully");
                    } else {
                        updateStatus("Catalog summary exited with code " + exit + ".");
                        updateInsightLabel(pipelineStatusLabel, "Completed with exit " + exit);
                        appendActivity("catalog exit code: " + exit);
                        consoleView.appendWarning("catalog summary exited with code " + exit);
                    }
                })
                .onFailure(ex -> {
                    String error = ex.getMessage() == null ? "unknown error" : ex.getMessage();
                    updateStatus("Unable to run catalog summary: " + error);
                    updateInsightLabel(pipelineStatusLabel, "Failure: " + truncate(error, 70));
                    appendActivity("catalog failure: " + truncate(error, 80));
                    consoleView.appendError("catalog summary failed: " + error);
                })
                .build();

        cancelActiveJob("catalog");
        BackendProcessService.BackendJob job = backendService.runCliCommand("catalog summary", args, callbacks);
        registerJob("catalog", job);
    }

    private void launchVoiceWorkflow() {
        if (!backendService.isCliAvailable()) {
            String message = "NEXUS CLI jar missing (" + backendService.describeCliLocation() + ")";
            updateStatus(message);
            updateInsightLabel(voiceStatusLabel, "Voice diagnostics unavailable – build CLI");
            appendActivity("Voice check skipped: CLI jar not found");
            consoleView.appendError(message);
            return;
        }

        updateStatus("Running voice configuration diagnostics via CLI…");
        updateInsightLabel(voiceStatusLabel, "Voice diagnostics running…");
        appendActivity("Running `voice --check-config`");
        List<String> args = List.of("voice", "--check-config");
        logCommand("voice configuration check", args);

        BackendProcessService.BackendCallbacks callbacks = BackendProcessService.BackendCallbacks.builder()
                .onStatus(this::updateStatus)
                .onStdout(line -> {
                    updateInsightLabel(voiceStatusLabel, truncate(line, 80));
                    appendActivity("voice: " + truncate(line, 80));
                    consoleView.appendStdout("voice", line);
                })
                .onStderr(line -> {
                    updateInsightLabel(voiceStatusLabel, "Warning: " + truncate(line, 70));
                    appendActivity("voice (err): " + truncate(line, 80));
                    consoleView.appendStderr("voice", line);
                })
                .onComplete(exit -> {
                    if (exit == 0) {
                        updateStatus("Voice configuration verified.");
                        consoleView.appendInfo("voice diagnostics completed successfully");
                    } else {
                        updateStatus("Voice diagnostics exited with code " + exit + ". Check activity feed for details.");
                        appendActivity("voice exit code: " + exit);
                        consoleView.appendWarning("voice diagnostics exited with code " + exit);
                    }
                })
                .onFailure(ex -> {
                    String error = ex.getMessage() == null ? "unknown error" : ex.getMessage();
                    updateStatus("Voice workflow failed: " + error);
                    updateInsightLabel(voiceStatusLabel, "Failure: " + truncate(error, 70));
                    appendActivity("voice failure: " + truncate(error, 80));
                    consoleView.appendError("voice diagnostics failed: " + error);
                })
                .build();

        cancelActiveJob("voice");
        BackendProcessService.BackendJob job = backendService.runCliCommand("voice configuration check", args, callbacks);
        registerJob("voice", job);
    }

    private void simulateEnvironmentConnection() {
        environmentText.setText("Environment: Connecting…");
        updateStatus("Establishing secure channel to company infrastructure");
        backgroundScheduler.schedule(() -> Platform.runLater(() -> {
            environmentText.setText("Environment: Connected (preview)");
            statusText.setText("Company infrastructure linked in preview mode");
        }), 1800, TimeUnit.MILLISECONDS);
    }

    private void runBackendHealthCheck() {
        if (!backendService.isCliAvailable()) {
            String message = "NEXUS CLI jar missing (" + backendService.describeCliLocation() + ")";
            updateStatus(message);
            updateEnvironment("Environment: CLI unavailable");
            updateInsightLabel(healthStatusLabel, "Health check blocked – CLI jar missing");
            appendActivity("Health check skipped: CLI jar not found");
            consoleView.appendError(message);
            return;
        }

        updateStatus("Running NEXUS CLI health check…");
        updateEnvironment("Environment: Checking CLI health…");
        updateInsightLabel(healthStatusLabel, "Health command running…");
        appendActivity("Running `health`");
        List<String> args = List.of("health");
        logCommand("health", args);

        BackendProcessService.BackendCallbacks callbacks = BackendProcessService.BackendCallbacks.builder()
                .onStatus(this::updateStatus)
                .onStdout(line -> {
                    if (line.trim().equalsIgnoreCase("OK")) {
                        updateEnvironment("Environment: CLI OK");
                        updateInsightLabel(healthStatusLabel, "Health check returned OK");
                        consoleView.appendInfo("health check returned OK");
                    } else {
                        updateInsightLabel(healthStatusLabel, truncate(line, 80));
                        consoleView.appendStdout("health", line);
                    }
                    appendActivity("health: " + truncate(line, 80));
                })
                .onStderr(line -> {
                    updateInsightLabel(healthStatusLabel, "Error: " + truncate(line, 70));
                    appendActivity("health (err): " + truncate(line, 80));
                    consoleView.appendStderr("health", line);
                })
                .onComplete(exit -> {
                    if (exit == 0) {
                        updateStatus("Health check completed successfully.");
                        consoleView.appendInfo("health command completed successfully");
                    } else {
                        updateStatus("Health check exited with code " + exit + ".");
                        updateInsightLabel(healthStatusLabel, "Exit code " + exit);
                        consoleView.appendWarning("health command exited with code " + exit);
                    }
                })
                .onFailure(ex -> {
                    String error = ex.getMessage() == null ? "unknown error" : ex.getMessage();
                    updateStatus("Health check failed: " + error);
                    updateInsightLabel(healthStatusLabel, "Failure: " + truncate(error, 70));
                    appendActivity("health failure: " + truncate(error, 80));
                    consoleView.appendError("health command failed: " + error);
                })
                .build();

        cancelActiveJob("health");
        BackendProcessService.BackendJob job = backendService.runCliCommand("health", args, callbacks);
        registerJob("health", job);
    }

    private void runDiagnosticsSweep() {
        if (!backendService.isCliAvailable()) {
            String message = "NEXUS CLI jar missing (" + backendService.describeCliLocation() + ")";
            updateStatus(message);
            updateInsightLabel(diagnosticsStatusLabel, "Diagnostics unavailable – CLI jar missing");
            appendActivity("Diagnostics skipped: CLI jar not found");
            consoleView.appendError(message);
            return;
        }

        updateStatus("Collecting CLI diagnostics…");
        updateInsightLabel(diagnosticsStatusLabel, "Running `version`");
        appendActivity("Running `version`");
        List<String> args = List.of("version");
        logCommand("version", args);

        BackendProcessService.BackendCallbacks callbacks = BackendProcessService.BackendCallbacks.builder()
                .onStatus(this::updateStatus)
                .onStdout(line -> {
                    updateInsightLabel(diagnosticsStatusLabel, "Version: " + truncate(line, 80));
                    appendActivity("version: " + truncate(line, 80));
                    consoleView.appendStdout("version", line);
                })
                .onStderr(line -> {
                    updateInsightLabel(diagnosticsStatusLabel, "Warning: " + truncate(line, 70));
                    appendActivity("version (err): " + truncate(line, 80));
                    consoleView.appendStderr("version", line);
                })
                .onComplete(exit -> {
                    if (exit == 0) {
                        updateStatus("Diagnostics completed.");
                        consoleView.appendInfo("version command completed successfully");
                    } else {
                        updateStatus("Diagnostics exited with code " + exit + ".");
                        consoleView.appendWarning("version command exited with code " + exit);
                    }
                })
                .onFailure(ex -> {
                    String error = ex.getMessage() == null ? "unknown error" : ex.getMessage();
                    updateStatus("Diagnostics failed: " + error);
                    updateInsightLabel(diagnosticsStatusLabel, "Failure: " + truncate(error, 70));
                    appendActivity("diagnostics failure: " + truncate(error, 80));
                    consoleView.appendError("version command failed: " + error);
                })
                .build();

        cancelActiveJob("version");
        BackendProcessService.BackendJob job = backendService.runCliCommand("version", args, callbacks);
        registerJob("version", job);
    }

    private void onWorkspaceSelected(ObservableValue<? extends String> observable, String oldValue, String newValue) {
        if (newValue != null) {
            updateStatus("Workspace selected: " + newValue);
        }
    }

    public void shutdown() {
        backgroundScheduler.shutdownNow();
        synchronized (activeJobs) {
            activeJobs.values().forEach(BackendProcessService.BackendJob::cancel);
            activeJobs.clear();
        }
        if (ownsBackendService) {
            try {
                backendService.close();
            } catch (Exception ex) {
                consoleView.appendError("Failed to close backend service: " + (ex.getMessage() == null ? ex.toString() : ex.getMessage()));
            }
        }
    }

    private void logCommand(String displayName, List<String> args) {
        String commandLine = String.join(" ", args);
        consoleView.appendCommand(displayName + " → " + commandLine + " (jar=" + backendService.describeCliLocation() + ")");
    }

    private void cancelActiveJob(String key) {
        synchronized (activeJobs) {
            BackendProcessService.BackendJob existing = activeJobs.remove(key);
            if (existing != null) {
                existing.cancel();
            }
        }
    }

    private void registerJob(String key, BackendProcessService.BackendJob job) {
        if (job == null || !job.isRunning()) {
            return;
        }
        synchronized (activeJobs) {
            activeJobs.put(key, job);
        }
        job.completion().whenComplete((exit, throwable) -> {
            synchronized (activeJobs) {
                activeJobs.remove(key, job);
            }
        });
    }
}
