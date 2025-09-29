package com.boozer.nexus.desktop.ui;

import javafx.animation.*;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.effect.Glow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;

public class SplashScreen extends Stage {
    public SplashScreen(Runnable onFinish) {
        // Load logo image (place logo.png in resources)
        ImageView logo = new ImageView(new Image(getClass().getResourceAsStream("/logo.png")));
        logo.setFitWidth(180);
        logo.setPreserveRatio(true);
        logo.setOpacity(0);

        Label title = new Label("NEXUSAI");
        title.setFont(Font.font("Montserrat", 38));
        title.setTextFill(Color.WHITE);
        title.setOpacity(0);
        title.setStyle("-fx-font-weight: bold;");

        Label tagline = new Label("JUST SAY IT, WE'LL BUILD IT");
        tagline.setFont(Font.font("Montserrat", 16));
        tagline.setTextFill(Color.web("#B32BF7"));
        tagline.setOpacity(0);
        tagline.setStyle("-fx-letter-spacing: 2px;");

        StackPane root = new StackPane(logo, title, tagline);
        root.setStyle("-fx-background-color: #18122B;");
        StackPane.setAlignment(logo, Pos.CENTER);
        StackPane.setAlignment(title, Pos.BOTTOM_CENTER);
        StackPane.setAlignment(tagline, Pos.BOTTOM_CENTER);
        tagline.setTranslateY(40);
        title.setTranslateY(-10);

        Scene scene = new Scene(root, 480, 320);
        setScene(scene);
        setAlwaysOnTop(true);
        setResizable(false);

        // Fade in logo
        FadeTransition logoFade = new FadeTransition(Duration.seconds(1.2), logo);
        logoFade.setFromValue(0);
        logoFade.setToValue(1);
        logoFade.setDelay(Duration.seconds(0.2));

        // Glow pulse
        Glow glow = new Glow(0.5);
        logo.setEffect(glow);
        Timeline pulse = new Timeline(
            new KeyFrame(Duration.seconds(0), new KeyValue(glow.levelProperty(), 0.5)),
            new KeyFrame(Duration.seconds(0.7), new KeyValue(glow.levelProperty(), 0.9)),
            new KeyFrame(Duration.seconds(1.4), new KeyValue(glow.levelProperty(), 0.5))
        );
        pulse.setCycleCount(4);
        pulse.setAutoReverse(true);

        // Fade in title
        FadeTransition titleFade = new FadeTransition(Duration.seconds(0.7), title);
        titleFade.setFromValue(0);
        titleFade.setToValue(1);
        titleFade.setDelay(Duration.seconds(1.1));
        ScaleTransition titleScale = new ScaleTransition(Duration.seconds(0.7), title);
        titleScale.setFromX(0.8);
        titleScale.setToX(1);
        titleScale.setFromY(0.8);
        titleScale.setToY(1);
        titleScale.setDelay(Duration.seconds(1.1));

        // Fade in tagline
        FadeTransition taglineFade = new FadeTransition(Duration.seconds(0.7), tagline);
        taglineFade.setFromValue(0);
        taglineFade.setToValue(1);
        taglineFade.setDelay(Duration.seconds(1.7));

        SequentialTransition sequence = new SequentialTransition(
            logoFade,
            new ParallelTransition(titleFade, titleScale),
            taglineFade,
            pulse
        );
        sequence.setOnFinished(e -> {
            PauseTransition pause = new PauseTransition(Duration.seconds(0.7));
            pause.setOnFinished(ev -> {
                close();
                if (onFinish != null) Platform.runLater(onFinish);
            });
            pause.play();
        });
        sequence.play();
    }
}
