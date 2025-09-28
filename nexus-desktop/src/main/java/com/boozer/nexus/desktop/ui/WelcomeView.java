package com.boozer.nexus.desktop.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.util.function.Consumer;

/**
 * Central welcome surface providing quick actions and future integration
 * placeholders. Designed to feel like a professional IDE landing page.
 */
public final class WelcomeView {
    private final StackPane view;
    private final Button primaryAction;
    private final Runnable primaryActionCallback;

    public WelcomeView(Consumer<String> statusNotifier, Runnable primaryActionHandler) {
        this.primaryActionCallback = primaryActionHandler;
        this.view = new StackPane();
        this.view.getStyleClass().add("welcome-view");

        BorderPane layout = new BorderPane();
        layout.getStyleClass().add("welcome-layout");
        layout.setPadding(new Insets(32));

        VBox intro = createIntroSection(statusNotifier);
        layout.setTop(intro);

        GridPane featureGrid = createFeatureGrid(statusNotifier);
        layout.setCenter(featureGrid);

        VBox quickLinks = createQuickLinks(statusNotifier);
        layout.setBottom(quickLinks);

        this.primaryAction = (Button) intro.lookup("#primaryActionButton");

        this.view.getChildren().add(layout);
    }

    public Node getView() {
        return view;
    }

    public void requestPrimaryActionFocus() {
        if (primaryAction != null) {
            primaryAction.requestFocus();
        }
    }

    private VBox createIntroSection(Consumer<String> statusNotifier) {
        Label title = new Label("NEXUS AI Enterprise Studio");
        title.getStyleClass().add("welcome-title");

        Label subtitle = new Label("Generate full-stack applications, orchestrate deployments, and collaborate with AI in a single enterprise-grade experience.");
        subtitle.getStyleClass().add("welcome-subtitle");
        subtitle.setWrapText(true);

        Button primary = new Button("Generate Application");
        primary.setId("primaryActionButton");
        primary.getStyleClass().addAll("accent-button", "primary-action");
        primary.setMinWidth(220);
        primary.setOnAction(e -> {
            statusNotifier.accept("Launching AI application generation studio");
            primaryActionCallback.run();
        });

        Button secondary = new Button("Open Voice Assistant");
        secondary.getStyleClass().add("secondary-action");
        secondary.setOnAction(e -> statusNotifier.accept("Voice assistant interface will surface soon"));

        HBox actions = new HBox(12, primary, secondary);
        actions.setAlignment(Pos.CENTER_LEFT);

        VBox container = new VBox(12, title, subtitle, actions);
        container.getStyleClass().add("welcome-intro");
        return container;
    }

    private GridPane createFeatureGrid(Consumer<String> statusNotifier) {
        GridPane grid = new GridPane();
        grid.getStyleClass().add("feature-grid");
        grid.setHgap(18);
        grid.setVgap(18);
        grid.setPadding(new Insets(24, 0, 24, 0));

        ColumnConstraints columnConstraints = new ColumnConstraints();
        columnConstraints.setPercentWidth(50);
        grid.getColumnConstraints().addAll(columnConstraints, columnConstraints);

        grid.add(createFeatureCard("Natural Language to Code",
                "Transform plain language requirements into engineered solutions.",
                "Replace 6 months of work with 5 minutes of AI collaboration.",
                statusNotifier), 0, 0);

        grid.add(createFeatureCard("Full-Stack Generation",
                "Generate frontend, backend, and database in one flow.",
                "React, Spring Boot, PostgreSQL, Docker & more.",
                statusNotifier), 1, 0);

        grid.add(createFeatureCard("Company Integration",
                "Connect to enterprise infrastructure and deployment targets.",
                "Single-click deployment to company Kubernetes or VM clusters.",
                statusNotifier), 0, 1);

        grid.add(createFeatureCard("5-Minute Delivery",
                "Deliver production-ready solutions in minutes.",
                "Automated compliance, security checks, and deployment scripts.",
                statusNotifier), 1, 1);

        return grid;
    }

    private VBox createFeatureCard(String heading, String description, String caption, Consumer<String> notifier) {
        Label title = new Label(heading);
        title.getStyleClass().add("feature-card-title");

        Label body = new Label(description);
        body.getStyleClass().add("feature-card-body");
        body.setWrapText(true);

        Label footer = new Label(caption);
        footer.getStyleClass().add("feature-card-footer");
        footer.setWrapText(true);

        Button explore = new Button("Explore");
        explore.getStyleClass().add("ghost-button");
        explore.setOnAction(e -> notifier.accept(heading + " experience is scheduled for Phase 2 rollout"));

        VBox card = new VBox(10, title, body, footer, explore);
        card.getStyleClass().add("feature-card");
        card.setPadding(new Insets(18));
        return card;
    }

    private VBox createQuickLinks(Consumer<String> notifier) {
        Label heading = new Label("Quick actions");
        heading.getStyleClass().add("panel-heading");

        Button openCli = new Button("Launch NEXUS CLI Console");
        openCli.getStyleClass().add("secondary-action");
        openCli.setMaxWidth(Double.MAX_VALUE);
        openCli.setOnAction(e -> notifier.accept("CLI console integration is planned for Day 2"));

        Button connectServer = new Button("Connect to Company Server");
        connectServer.getStyleClass().add("secondary-action");
        connectServer.setMaxWidth(Double.MAX_VALUE);
        connectServer.setOnAction(e -> notifier.accept("Company server onboarding wizard in development"));

        Button reviewRoadmap = new Button("View Transformation Roadmap");
        reviewRoadmap.getStyleClass().add("secondary-action");
        reviewRoadmap.setMaxWidth(Double.MAX_VALUE);
        reviewRoadmap.setOnAction(e -> notifier.accept("Opening AI transformation roadmap"));

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        VBox container = new VBox(12, heading, openCli, connectServer, reviewRoadmap, spacer);
        container.getStyleClass().add("quick-links");
        return container;
    }
}
