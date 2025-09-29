package com.boozer.nexus.desktop.ui;

import com.boozer.nexus.desktop.voice.VoiceRecorder;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.util.Duration;
import javafx.stage.Window;

import javax.sound.sampled.LineUnavailableException;
import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

/** Simple dialog that captures microphone audio and returns WAV bytes. */
public final class VoiceCaptureDialog {
    private VoiceCaptureDialog() {
    }

    public static Optional<byte[]> capture(Node owner) {
        Dialog<byte[]> dialog = new Dialog<>();
        dialog.setTitle("Voice to App");
        dialog.setHeaderText("Record a short description and NEXUS will generate your application.");
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);

        Label status = new Label("Press record to begin");
        status.getStyleClass().add("voice-dialog-status");
        Label timerLabel = new Label("00:00");
        timerLabel.getStyleClass().add("voice-dialog-timer");

    Button recordButton = new Button("Start Recording");
        recordButton.getStyleClass().add("accent-button");

        GridPane grid = new GridPane();
        grid.setHgap(12);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));
        grid.add(status, 0, 0, 2, 1);
        grid.add(timerLabel, 0, 1);
        grid.add(recordButton, 1, 1);

        dialog.getDialogPane().setContent(grid);

        if (owner != null && owner.getScene() != null) {
            Window window = owner.getScene().getWindow();
            if (window != null) {
                dialog.initOwner(window);
            }
        }

        VoiceRecorder recorder = new VoiceRecorder();
        AtomicInteger seconds = new AtomicInteger();
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), evt -> {
            int value = seconds.incrementAndGet();
            timerLabel.setText(String.format("%02d:%02d", value / 60, value % 60));
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);

        recordButton.setOnAction(event -> {
            if (!recorder.isRecording()) {
                try {
                    recorder.start();
                    seconds.set(0);
                    timerLabel.setText("00:00");
                    timeline.playFromStart();
                    status.setText("Recording… speak clearly about the app you need.");
                    recordButton.setText("Stop & Generate");
                } catch (LineUnavailableException ex) {
                    showError("Microphone unavailable", ex.getMessage());
                    resetAfterFailure(status, recordButton, seconds, timerLabel);
                }
            } else {
                finishRecording(dialog, recorder, timeline, status, recordButton, seconds, timerLabel);
            }
        });

        dialog.setResultConverter(button -> null);
        dialog.setOnHidden(event -> {
            timeline.stop();
            try {
                recorder.close();
            } catch (IOException ignored) {
            }
        });
        return dialog.showAndWait();
    }

    private static void finishRecording(Dialog<byte[]> dialog,
                                       VoiceRecorder recorder,
                                       Timeline timeline,
                                       Label status,
                                       Button recordButton,
                                       AtomicInteger seconds,
                                       Label timerLabel) {
        timeline.stop();
        status.setText("Processing recording…");
        try {
            byte[] wav = recorder.stopAndGetWav();
            if (wav.length == 0) {
                showError("No audio captured", "Try again and ensure your microphone is enabled.");
                resetAfterFailure(status, recordButton, seconds, timerLabel);
                return;
            }
            dialog.setResult(wav);
            dialog.setResultConverter(button -> wav);
            dialog.close();
        } catch (IOException ex) {
            showError("Failed to process recording", ex.getMessage());
            resetAfterFailure(status, recordButton, seconds, timerLabel);
        }
    }

    private static void resetAfterFailure(Label status, Button recordButton, AtomicInteger seconds, Label timerLabel) {
        seconds.set(0);
        timerLabel.setText("00:00");
        recordButton.setText("Start Recording");
        status.setText("Press record to begin");
    }

    private static void showError(String title, String details) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR, details, ButtonType.OK);
            alert.setTitle(title);
            alert.setHeaderText(title);
            alert.showAndWait();
        });
    }
}
