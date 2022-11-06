package adv.headsoccer_project.controller.Game;

import adv.headsoccer_project.view.GameView;
import adv.headsoccer_project.view.SplashView;
import adv.headsoccer_project.view.SplashView.SplashType;
import adv.headsoccer_project.model.Ball;
import adv.headsoccer_project.model.Character;
import adv.headsoccer_project.model.Goal;
import adv.headsoccer_project.Launcher;
import adv.headsoccer_project.controller.KeySettingController;
import adv.headsoccer_project.controller.MediaController;
import adv.headsoccer_project.controller.SceneController;
import java.util.HashMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.input.KeyCode;
import javafx.util.Duration;

public class MainGameController {
    public static final double GRAVITY = 0.75;
    public static final double FRICTION = 0.85;
    public static final double AIR_RESISTANCE = 0.95;
    public static final double ACCELERATION = 0.75;
    public static final double JUMP_VELOCITY = GRAVITY * -25;
    public static final double ELASTICITY = 0.9;
    public static final double PHYSICS_FACTOR = 1;
    private static String p1Char = "ElecMan";
    private static String p2Char = "MegaMan";
    private static SimpleIntegerProperty p1Score = new SimpleIntegerProperty(0);
    private static SimpleIntegerProperty p2Score = new SimpleIntegerProperty(0);
    private static SimpleDoubleProperty p1Super = new SimpleDoubleProperty(0);
    private static SimpleDoubleProperty p2Super = new SimpleDoubleProperty(0);
    private static SimpleIntegerProperty roundTime = new SimpleIntegerProperty(0);
    private static HashMap<String, KeyCode> keyConfig;
    private static Logger logger = LogManager.getLogger(MainGameController.class);
    private static boolean goalable = true;
    private static GameView gameView;

    static {
        keyConfig = KeySettingController.getKeyConfig();
    }

    public static void startGame(GameView _gameView) {
        goalable = true;
        gameView = _gameView;
        p1Score.set(0);
        p2Score.set(0);
        p1Super.set(0);
        p2Super.set(0);
        roundTime.set(0);
        setupGame(gameView);
    }

    public static void setupGame(GameView gameView) {
        gameView.getCharacters().add(new Character(100d, 50d, keyConfig.get("p1_left"), keyConfig.get("p1_right"),
                keyConfig.get("p1_jump"), keyConfig.get("p1_kick"), keyConfig.get("p1_special"), p1Char));
        gameView.getCharacters()
                .add(new Character(SceneController.getWidth() - 100d, 50d, keyConfig.get("p2_left"),
                        keyConfig.get("p2_right"), keyConfig.get("p2_jump"), keyConfig.get("p2_kick"),
                        keyConfig.get("p2_special"), p2Char));
        gameView.getGoalRegions().add(new Goal(0, 500, 100, GameView.GROUND, 2));
        gameView.getGoalRegions().add(new Goal(SceneController.getWidth() - 100, 500,
                SceneController.getWidth(), GameView.GROUND, 1));
        gameView.setBall(new Ball(SceneController.getWidth() / 2, 50));
        gameView.getChildren().addAll(gameView.getBall());
        gameView.getChildren().addAll(gameView.getCharacters());
        if (gameView.getForeground() != null)
            gameView.getForeground().toFront();
    }

    public static void resetGame(GameView gameView) {
        Platform.runLater(() -> {
            gameView.getCharacters().get(0).reset(100d, 50d);
            gameView.getCharacters().get(1).reset(SceneController.getWidth() - 100d, 50d);
            gameView.getBall().reset(SceneController.getWidth() / 2, 50);
        });
        goalable = true;
    }

    public static void incrementP1() {
        p1Score.set(p1Score.get() + 1);
        playGoal();
        logger.debug(p1Score);
    }

    public static void incrementP2() {
        p2Score.set(p2Score.get() + 1);
        playGoal();
        logger.debug(p2Score);
    }

    public static void incrementSP1(double d) {
        p1Super.set(p1Super.get() + d);
    }

    public static void incrementSP2(double d) {
        p2Super.set(p2Super.get() + d);
    }

    public static void playGoal() {
        SplashView splash = gameView.createSplash(SplashType.GOAL);
        Timeline timeline = new Timeline();
        timeline.getKeyFrames().add(new KeyFrame(Duration.millis(500),
                new KeyValue(splash.getFade(), 1)));
        timeline.getKeyFrames().add(new KeyFrame(Duration.seconds(1), (e) -> {
            resetGame(gameView);
            splash.getFade().set(0);
        }));
        MediaController.playSFX("goal", 1000);
        timeline.play();
    }

    public static SimpleIntegerProperty getP1Score() {
        return p1Score;
    }

    public static SimpleIntegerProperty getP2Score() {
        return p2Score;
    }

    public static SimpleDoubleProperty getP1Super() {
        return p1Super;
    }

    public static SimpleDoubleProperty getP2Super() {
        return p2Super;
    }

    public static void startCountdown(int time) {
        roundTime.set(time);
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(time), new KeyValue(roundTime, 0)));
        timeline.getKeyFrames().add(new KeyFrame(Duration.seconds(time), (e) -> {
            goalable = false;
            endRound();
        }));
        timeline.play();
    }

    public static SimpleIntegerProperty getRoundTime() {
        return roundTime;
    }

    private static void endRound() {
        gameView.getDrawingLoop().setTimeScale(0.1);
        MediaController.stop();
        MediaController.playSFX("applause", 5000);
        if (p1Score.get() > p2Score.get()) {
            SplashView splash = gameView.createSplash(SplashType.P1WIN);
            gameView.getCharacters().get(0).setWon(true);
            Timeline timeline2 = new Timeline();
            timeline2.getKeyFrames().add(new KeyFrame(Duration.millis(500),
                    new KeyValue(splash.getFade(), 1)));
            timeline2.getKeyFrames().add(new KeyFrame(Duration.seconds(5), (e2) -> {
                cleanUp();
            }));
            timeline2.play();
        } else if (p1Score.get() < p2Score.get()) {
            SplashView splash = gameView.createSplash(SplashType.P2WIN);
            gameView.getCharacters().get(1).setWon(true);
            Timeline timeline2 = new Timeline();
            timeline2.getKeyFrames().add(new KeyFrame(Duration.millis(500),
                    new KeyValue(splash.getFade(), 1)));
            timeline2.getKeyFrames().add(new KeyFrame(Duration.seconds(5), (e2) -> {
                cleanUp();
            }));
            timeline2.play();
        } else {
            SplashView splash = gameView.createSplash(SplashType.DRAW);
            Timeline timeline2 = new Timeline();
            timeline2.getKeyFrames().add(new KeyFrame(Duration.millis(500),
                    new KeyValue(splash.getFade(), 1)));
            timeline2.getKeyFrames().add(new KeyFrame(Duration.seconds(5), (e2) -> {
                cleanUp();
            }));
            timeline2.play();
        }
    }

    private static void cleanUp() {
        gameView.getDrawingLoop().setRunning(false);
        gameView.getGameLoop().setRunning(false);
        Launcher.getSceneController().activate("Menu");
        Launcher.getSceneController().removeScene("Game");
        Launcher.getSceneController().addScene("Game", new GameView());
    }

    public static boolean isGoalable() {
        return goalable;
    }

    public static void setGoalable(boolean goalable) {
        MainGameController.goalable = goalable;
    }

    public static void executeSpecial(Character c) {
        int player = gameView.getCharacters().indexOf(c);
        if (player == 0) {
            if (p1Super.get() >= 100) {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        gameView.getP1Super().getStyleClass().add("special-queued");
                    }
                });
                c.queueSpecial();
            }
        } else if (player == 1) {
            if (p2Super.get() >= 100) {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        gameView.getP2Super().getStyleClass().add("special-queued");
                    }
                });
                c.queueSpecial();
            }
        }
    }

    public static void exhaustSpecial(Character c) {
        int player = gameView.getCharacters().indexOf(c);
        if (player == 0) {
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    Timeline tl = new Timeline(new KeyFrame(Duration.millis(250), new KeyValue(p1Super, 0)));
                    tl.getKeyFrames().add(new KeyFrame(Duration.millis(250),
                            e -> gameView.getP1Super().getStyleClass().remove("special-queued")));
                    tl.play();
                }
            });
        } else if (player == 1) {
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    Timeline tl = new Timeline(new KeyFrame(Duration.millis(250), new KeyValue(p2Super, 0)));
                    tl.getKeyFrames().add(new KeyFrame(Duration.millis(250),
                            e -> gameView.getP2Super().getStyleClass().remove("special-queued")));
                    tl.play();
                }
            });
        }
    }

    public static void doSpecialAnimation(Character c) {
        gameView.getDrawingLoop().setTimeScale(0.1);
        gameView.getGameLoop().setTimeScale(0.1);
        goalable = false;
        c.setDoingSpecial(true);
    }

    public static void finishSpecial(Character c) {
        gameView.getDrawingLoop().setTimeScale(1);
        gameView.getGameLoop().setTimeScale(1);
        goalable = true;
        Launcher.getSceneController().getKeys().clear();
        c.setIsMoving(false);
        c.setDoingSpecial(false);
    }
}