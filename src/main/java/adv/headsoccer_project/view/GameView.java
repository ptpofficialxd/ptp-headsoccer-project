package adv.headsoccer_project.view;

import java.util.ArrayList;

import adv.headsoccer_project.controller.Game.DrawingLoop;
import adv.headsoccer_project.controller.Game.GameLoop;
import adv.headsoccer_project.controller.Game.MainGameController;
import adv.headsoccer_project.controller.MediaController;
import adv.headsoccer_project.controller.SceneController;
import adv.headsoccer_project.model.Character;
import adv.headsoccer_project.model.Goal;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import adv.headsoccer_project.controller.ImageHandler;
import adv.headsoccer_project.model.Ball;
import adv.headsoccer_project.view.SplashView.SplashType;

public class GameView extends BorderPane {
    private GameLoop gameLoop;
    private DrawingLoop drawingLoop;
    private Label p1ScoreLabel, p2ScoreLabel, timerLabel;
    public final static int GROUND = SceneController.getHeight() - 75;
    private ProgressBar p1Super, p2Super;
    private ArrayList<Character> characters = new ArrayList<>();
    private ArrayList<Goal> goalRegions = new ArrayList<>();
    private Ball ball;
    private ImageView foreground;

    public GameView() {
        this.getStyleClass().add("game-view");
    }

    private void start() {
        HBox containerBox = new HBox(64);
        MainGameController.startGame(this);
        p1ScoreLabel = new Label("ttt");
        p1ScoreLabel.getStyleClass().add("score-label");
        p1ScoreLabel.textProperty().bind(MainGameController.getP1Score().asString());
        p2ScoreLabel = new Label("ttt");
        p2ScoreLabel.getStyleClass().add("score-label");
        p2ScoreLabel.textProperty().bind(MainGameController.getP2Score().asString());
        timerLabel = new Label("ttt");
        timerLabel.getStyleClass().add("time-label");
        timerLabel.setPrefWidth(120);
        timerLabel.setAlignment(Pos.CENTER);
        timerLabel.textProperty().bind(MainGameController.getRoundTime().asString());
        p1Super = new ProgressBar();
        p1Super.progressProperty().bind(MainGameController.getP1Super().divide(100));
        p1Super.setPrefWidth(480);
        p2Super = new ProgressBar();
        p2Super.setPrefWidth(480);
        p2Super.setScaleX(-1);
        p2Super.progressProperty().bind(MainGameController.getP2Super().divide(100));
        VBox p1Box = new VBox(8, p1Super, p1ScoreLabel);
        p1Box.setAlignment(Pos.CENTER_RIGHT);
        VBox p2Box = new VBox(8, p2Super, p2ScoreLabel);
        p2Box.setAlignment(Pos.CENTER_LEFT);
        containerBox.setAlignment(Pos.CENTER);
        containerBox.getChildren().addAll(p1Box, timerLabel, p2Box);
        foreground = new ImageView(ImageHandler.getImage("/assets/background/headsoccer_foregroundgame.png"));
        foreground.setFitWidth(SceneController.getWidth());
        foreground.setPreserveRatio(true);
        this.setTop(containerBox);
        this.getChildren().add(foreground);
    }

    public ArrayList<Character> getCharacters() {
        return characters;
    }

    public SplashView createSplash(SplashType type) {
        SplashView splash = new SplashView(type);
        this.setCenter(splash);
        splash.toFront();
        return splash;
    }

    public void onStart() {
        start();
        gameLoop = new GameLoop(this);
        (new Thread(gameLoop)).start();
        drawingLoop = new DrawingLoop(this);
        (new Thread(drawingLoop)).start();
        MediaController.play("C418 - Sweden");
        MainGameController.startCountdown(60);
    }

    public Ball getBall() {
        return ball;
    }

    public void setBall(Ball ball) {
        this.ball = ball;
    }

    public GameLoop getGameLoop() {
        return gameLoop;
    }

    public DrawingLoop getDrawingLoop() {
        return drawingLoop;
    }

    public ArrayList<Goal> getGoalRegions() {
        return goalRegions;
    }

    public ImageView getForeground() {
        return foreground;
    }

    public ProgressBar getP1Super() {
        return p1Super;
    }

    public ProgressBar getP2Super() {
        return p2Super;
    }
}