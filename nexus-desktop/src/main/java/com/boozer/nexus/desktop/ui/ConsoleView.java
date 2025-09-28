package com.boozer.nexus.desktop.ui;

import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;

/**
 * Simple console surface that captures streaming output from backend processes
 * and presents it within the desktop shell.
 */
public class ConsoleView {
    private static final int MAX_LINES = 500;

    private final VBox container;
    private final TextArea textArea;
    private final Deque<String> lines = new ArrayDeque<>();

    public ConsoleView() {
        Label title = new Label("Activity Console");
        title.getStyleClass().add("console-title");

        Button clearButton = new Button("Clear");
        clearButton.getStyleClass().add("console-button");
        clearButton.setOnAction(e -> clear());

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox header = new HBox(title, spacer, clearButton);
        header.getStyleClass().add("console-header");

        textArea = new TextArea();
        textArea.setEditable(false);
        textArea.setWrapText(true);
        textArea.setPrefRowCount(8);
        textArea.getStyleClass().add("console-area");

        container = new VBox(header, textArea);
        container.getStyleClass().add("console-pane");
    }

    public VBox getView() {
        return container;
    }

    public void appendCommand(String message) {
        appendLine(format("cmd", message));
    }

    public void appendInfo(String message) {
        appendLine(format("info", message));
    }

    public void appendWarning(String message) {
        appendLine(format("warn", message));
    }

    public void appendError(String message) {
        appendLine(format("error", message));
    }

    public void appendStdout(String scope, String message) {
        appendLine(format(scope + "·out", message));
    }

    public void appendStderr(String scope, String message) {
        appendLine(format(scope + "·err", message));
    }

    public void clear() {
        Platform.runLater(() -> {
            lines.clear();
            textArea.clear();
        });
    }

    private void appendLine(String entry) {
        if (entry == null || entry.isBlank()) {
            return;
        }
        Platform.runLater(() -> {
            lines.addLast(entry);
            while (lines.size() > MAX_LINES) {
                lines.removeFirst();
            }
            StringBuilder builder = new StringBuilder();
            Iterator<String> iterator = lines.iterator();
            while (iterator.hasNext()) {
                builder.append(iterator.next());
                if (iterator.hasNext()) {
                    builder.append(System.lineSeparator());
                }
            }
            textArea.setText(builder.toString());
            textArea.positionCaret(textArea.getText().length());
        });
    }

    private String format(String prefix, String message) {
        String cleanMessage = message == null ? "" : message.trim();
        String cleanPrefix = prefix == null ? "misc" : prefix.trim();
        if (cleanPrefix.isEmpty()) {
            return cleanMessage;
        }
        return "[" + cleanPrefix + "] " + cleanMessage;
    }
}
