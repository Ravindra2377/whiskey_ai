package com.boozer.nexus.desktop.ui;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.util.Callback;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/** Modal dialog for collecting GPT-4 generation parameters. */
public final class GenerationDialog {
    private GenerationDialog() {
    }

    public static Optional<Result> show(Node owner, GenerationIntent intent) {
        Dialog<Result> dialog = new Dialog<>();
        dialog.setTitle("Generate Application");
        dialog.setHeaderText("Describe the application you want NEXUS AI to build.");

        ButtonType generateType = new ButtonType("Generate", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(generateType, ButtonType.CANCEL);

        TextArea descriptionArea = new TextArea();
        descriptionArea.setPromptText("Example: Build a modern expense management web app with OAuth login and analytics dashboard.");
        descriptionArea.setWrapText(true);
        descriptionArea.setMinHeight(140);

        if (intent != null && intent.getSeedDescription() != null) {
            descriptionArea.setText(intent.getSeedDescription());
        }

        TextField projectNameField = new TextField();
        projectNameField.setPromptText("Optional: Project name hint");

        TextField stackField = new TextField();
        stackField.setPromptText("Optional: Preferred stack (e.g. React + Spring Boot + PostgreSQL)");

        TextField featureField = new TextField();
        featureField.setPromptText("Additional feature tags (comma separated)");
        if (intent != null && !intent.getFeatureTags().isEmpty()) {
            featureField.setText(String.join(", ", intent.getFeatureTags()));
        }

        CheckBox includeTests = new CheckBox("Include automated tests");
        includeTests.setSelected(true);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 15, 10, 15));

        grid.add(new Label("Application description"), 0, 0);
        grid.add(descriptionArea, 0, 1, 2, 1);
        grid.add(new Label("Project name"), 0, 2);
        grid.add(projectNameField, 1, 2);
        grid.add(new Label("Preferred stack"), 0, 3);
        grid.add(stackField, 1, 3);
        grid.add(new Label("Feature tags"), 0, 4);
        grid.add(featureField, 1, 4);
        grid.add(includeTests, 0, 5, 2, 1);

        dialog.getDialogPane().setContent(grid);

        Node generateButton = dialog.getDialogPane().lookupButton(generateType);
        generateButton.setDisable(descriptionArea.getText().trim().isEmpty());

        descriptionArea.textProperty().addListener((obs, oldValue, newValue) ->
                generateButton.setDisable(newValue == null || newValue.trim().isEmpty()));

        dialog.setResultConverter(new Callback<>() {
            @Override
            public Result call(ButtonType dialogButton) {
                if (dialogButton == generateType) {
                    String description = descriptionArea.getText() == null ? "" : descriptionArea.getText().trim();
                    String projectName = projectNameField.getText() == null ? null : projectNameField.getText().trim();
                    String stack = stackField.getText() == null ? null : stackField.getText().trim();
                    List<String> tags = new ArrayList<>();
                    if (intent != null && intent.getFeatureTags() != null) {
                        tags.addAll(intent.getFeatureTags());
                    }
                    if (featureField.getText() != null && !featureField.getText().isBlank()) {
                        tags.addAll(Arrays.stream(featureField.getText().split(","))
                                .map(String::trim)
                                .filter(s -> !s.isEmpty())
                                .collect(Collectors.toList()));
                    }
                    return new Result(description, projectName, stack, includeTests.isSelected(), tags);
                }
                return null;
            }
        });

        dialog.getDialogPane().getScene().getWindow().sizeToScene();
        descriptionArea.requestFocus();

        return dialog.showAndWait();
    }

    public record Result(String description,
                         String projectNameHint,
                         String targetStack,
                         boolean includeTests,
                         List<String> featureTags) {
    }
}
