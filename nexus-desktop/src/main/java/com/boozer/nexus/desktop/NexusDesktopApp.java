package com.boozer.nexus.desktop;

import com.boozer.nexus.desktop.backend.BackendProcessService;
import com.boozer.nexus.desktop.backend.CliPreferences;
import com.boozer.nexus.desktop.ui.DesktopShell;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.stage.Stage;

/**
 * Entry-point for the NEXUS AI professional desktop application.
 */
public class NexusDesktopApp extends Application {

    private DesktopShell shell;
    private BackendProcessService backendService;

    @Override
    public void start(Stage primaryStage) {
    CliPreferences.getCliJarPath()
        .filter(path -> path.toString() != null && !path.toString().isBlank())
        .ifPresent(path -> System.setProperty(BackendProcessService.CLI_PATH_PROPERTY, path.toString()));

        backendService = new BackendProcessService();
        shell = new DesktopShell(backendService);

        Scene scene = new Scene(shell.getRoot(), 1280, 800);
        scene.getStylesheets().add(
                NexusDesktopApp.class.getResource("/com/boozer/nexus/desktop/styles/main.css").toExternalForm()
        );

        primaryStage.setTitle("NEXUS AI Desktop Platform");
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(1100);
        primaryStage.setMinHeight(720);
        primaryStage.getIcons().add(createAppIcon(96));
        primaryStage.show();

        Platform.runLater(shell::focusWelcomePrimaryAction);
    }

    @Override
    public void stop() throws Exception {
        if (shell != null) {
            shell.shutdown();
        }
        if (backendService != null) {
            backendService.close();
        }
        super.stop();
    }

    private Image createAppIcon(int size) {
        WritableImage image = new WritableImage(size, size);
        PixelWriter writer = image.getPixelWriter();

        int borderWidth = Math.max(2, size / 12);
        int accentRadius = size / 6;

        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                double dx = x - size / 2.0;
                double dy = y - size / 2.0;
                double distance = Math.sqrt(dx * dx + dy * dy);

                if (distance > size / 2.0 - borderWidth) {
                    writer.setArgb(x, y, rgba(17, 25, 40, 255));
                } else {
                    double gradient = Math.max(0, 1 - distance / (size / 2.0));
                    int red = (int) (23 + 50 * gradient);
                    int green = (int) (67 + 120 * gradient);
                    int blue = (int) (99 + 140 * gradient);
                    writer.setArgb(x, y, rgba(red, green, blue, 255));
                }
            }
        }

        int centerX = size / 2;
        int centerY = size / 2;
        for (int dy = -accentRadius; dy <= accentRadius; dy++) {
            for (int dx = -accentRadius; dx <= accentRadius; dx++) {
                if (dx * dx + dy * dy <= accentRadius * accentRadius) {
                    writer.setArgb(centerX + dx, centerY + dy, rgba(255, 99, 132, 230));
                }
            }
        }

        return image;
    }

    private int rgba(int r, int g, int b, int a) {
        return ((a & 0xFF) << 24)
                | ((r & 0xFF) << 16)
                | ((g & 0xFF) << 8)
                | (b & 0xFF);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
