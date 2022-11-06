package adv.headsoccer_project.view;

import adv.headsoccer_project.controller.MediaController;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.CornerRadii;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.VBox;
import adv.headsoccer_project.Launcher;

public class MenuView extends BorderPane {
    public static final CornerRadii CORNER = new CornerRadii(10);

    public MenuView() {
        this.getStyleClass().addAll("container", "menu");
        this.getChildren().clear();
        VBox container = new VBox();
        Button startButton = new Button();
        startButton.setPrefWidth(322);
        startButton.setPrefHeight(81);
        startButton.setOnAction((x) -> {
            Launcher.getSceneController().activate("Game");
        });
        Button quitButton = new Button();
        quitButton.setPrefWidth(322);
        quitButton.setPrefHeight(81);
        quitButton.setOnAction((x) -> {
            Platform.exit();
        });
        container.getChildren().addAll(startButton, quitButton);
        container.setSpacing(36);
        container.setAlignment(Pos.CENTER_LEFT);
        container.setPadding(new Insets(245, 0, 0, 505));
        this.setLeft(container);
    }

    public static void onStart() {
        MediaController.playSFX("gameStart");
        MediaController.play("Cartoon - C U Again");
    }
}