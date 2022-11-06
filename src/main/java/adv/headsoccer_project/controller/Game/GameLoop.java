package adv.headsoccer_project.controller.Game;

import java.util.ArrayList;
import org.apache.logging.log4j.Logger;
import javafx.application.Platform;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.SimpleDoubleProperty;
import org.apache.logging.log4j.LogManager;
import adv.headsoccer_project.model.Ball;
import adv.headsoccer_project.model.Character;
import adv.headsoccer_project.model.Goal;
import adv.headsoccer_project.view.GameView;

public class GameLoop implements Runnable {
    private GameView gameView;
    private DoubleBinding frameRate;
    private SimpleDoubleProperty timeScale;
    private DoubleBinding interval;
    private boolean running;
    private static Logger logger = LogManager.getLogger(GameLoop.class);

    public GameLoop(GameView gameView) {
        this.gameView = gameView;
        timeScale = new SimpleDoubleProperty(1);
        frameRate = new SimpleDoubleProperty(120).multiply(timeScale);
        interval = new SimpleDoubleProperty(1000d).divide(frameRate);
        running = true;
    }

    private void checkDrawCollisions(ArrayList<Character> characters, Ball ball, ArrayList<Goal> goalRegions) {
        for (int i = 0; i < characters.size(); i++) {
            Character c = characters.get(i);
            c.checkReachGameWall();
            c.checkReachFloor();
            ball.checkIntersectCharacter(c);
        }
        characters.get(0).checkIntersectCharacter(characters.get(1));

        for (int i = 0; i < goalRegions.size(); i++) {
            Goal g = goalRegions.get(i);
            ball.checkIntersectGoalRegion(g);
        }
        ball.checkReachGameWall();
        ball.checkReachFloor();
    }

    private void paint(ArrayList<Character> characters, Ball ball) {
        for (int i = 0; i < characters.size(); i++) {
            Character c = characters.get(i);
            c.move();
            c.updatePos();
        }
        ball.move();
        ball.updatePos();
    }

    @Override
    public void run() {
        while (running) {
            long time = System.currentTimeMillis();
            if (MainGameController.isGoalable()) {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        MainGameController.incrementSP1(10.0 / frameRate.get());
                        MainGameController.incrementSP2(10.0 / frameRate.get());
                        checkDrawCollisions(gameView.getCharacters(), gameView.getBall(), gameView.getGoalRegions());
                        paint(gameView.getCharacters(), gameView.getBall());
                    }
                });
            }
            time = System.currentTimeMillis() - time;
            if (interval.get() > 1000)
                continue;
            try {
                if (time < interval.get()) {
                    Thread.sleep((long) (interval.get() - time));
                } else {
                    Thread.sleep((long) (interval.get() - (interval.get() % time)));
                }
            } catch (InterruptedException e) {
                logger.error(e.getCause(), e);
            }
        }
    }

    public DoubleBinding getFrameRate() {
        return frameRate;
    }

    public void setTimeScale(double timeScale) {
        this.timeScale.set(timeScale);
        frameRate.getValue();
        interval.getValue();
        logger.info(String.format("%s %s", frameRate, timeScale));
    }

    public void setRunning(boolean running) {
        this.running = running;
    }
}