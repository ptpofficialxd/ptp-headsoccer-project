package adv.headsoccer_project.model;

import adv.headsoccer_project.controller.Game.MainGameController;
import adv.headsoccer_project.controller.MediaController;
import adv.headsoccer_project.controller.SceneController;
import adv.headsoccer_project.view.GameView;
import adv.headsoccer_project.controller.ImageHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;

public class Ball extends StackPane {
    public static final double RADIUS = 32;
    private double x;
    private double y;
    private PolarVector vel;
    private double angle;
    private double va;
    private boolean isMidAir;
    private ImageView imageView;
    private static Logger logger = LogManager.getLogger(Ball.class);

    public Ball(double x, double y) {
        this.x = x;
        this.y = y;
        this.setTranslateX(x);
        this.setTranslateY(y);
        this.imageView = new ImageView(ImageHandler.getImage("/assets/img/ball.png"));
        this.imageView.setFitHeight(RADIUS * 2);
        this.imageView.setPreserveRatio(true);
        this.vel = new PolarVector();
        this.getChildren().add(imageView);
    }

    public void move() {
        logger.debug(String.format("pos: [%.2f, %.2f] %s - %s", x, y, isMidAir ? "air" : "ground", vel));
        double[] velArray = vel.toCartesian();
        x += velArray[0] * MainGameController.PHYSICS_FACTOR;
        y += velArray[1] * MainGameController.PHYSICS_FACTOR;
        if (isMidAir) {
            vel = vel.add(0, MainGameController.GRAVITY);
        } else {
            va = velArray[0];
        }
        angle += va * MainGameController.PHYSICS_FACTOR;
        vel = vel.mult(isMidAir ? MainGameController.AIR_RESISTANCE : MainGameController.FRICTION);
        if (vel.getMagnitude() < 0.2d) {
            vel.setMagnitude(0);
        }
    }

    public void checkReachGameWall() {
        if (x <= RADIUS) {
            PolarVector toAdd = new PolarVector().fromPolar(
                    vel.project(0).mult(1 + MainGameController.ELASTICITY).getMagnitude(), 0);
            vel = vel.add(toAdd);
            move();
            x = RADIUS;
            MediaController.playSFX("bounce");
        } else if (x >= SceneController.getWidth() - RADIUS) {
            PolarVector toAdd = new PolarVector().fromPolar(
                    vel.project(Math.PI).mult(1 + MainGameController.ELASTICITY).getMagnitude(), Math.PI);
            vel = vel.add(toAdd);
            move();
            x = SceneController.getWidth() - RADIUS;
            MediaController.playSFX("bounce");
        }
    }

    public void checkReachFloor() {
        if (y >= GameView.GROUND - RADIUS) {
            isMidAir = false;
            PolarVector toAdd = new PolarVector().fromPolar(
                    vel.project(-Math.PI / 2).mult(1 + MainGameController.ELASTICITY).getMagnitude() - 4, -Math.PI / 2);
            vel = vel.add(toAdd);
            y = GameView.GROUND - RADIUS;
            if (toAdd.getMagnitude() > 5)
                MediaController.playSFX("bounce");
        } else
            isMidAir = true;
    }

    public void checkIntersectCharacter(Character c) {
        if (intersects_character(c)) {
            if (c.isSpecialQueued()) {
                y = c.getBoundsInParent().getCenterY();
                MainGameController.doSpecialAnimation(c);
                vel = new PolarVector().fromPolar(75, c.getDirectionR() ? 0 : Math.PI);
                c.exhaustSpecial();
                return;
            }
            PolarVector normal = vel.add(c.getVel().negate());
            double impulse = vel.add(c.getVel().negate()).mult(0.75 * (2 + MainGameController.ELASTICITY))
                    .project(normal.getAngle())
                    .getMagnitude();
            PolarVector toAdd = new PolarVector().fromPolar(impulse, normal.getAngle()).negate();
            vel = vel
                    .add(c.isKicking()
                            ? toAdd.add(new PolarVector().fromPolar(toAdd.mult(0.125).getMagnitude(), -Math.PI / 2))
                            : toAdd)
                    .mult(c.isKicking() ? 1.5 : 1);
            toAdd = new PolarVector().fromPolar(impulse, normal.getAngle());
            c.setVel(c.getVel().add(toAdd));
        }
    }

    public void checkIntersectGoalRegion(Goal gr) {
        if (intersects_goal_region(gr)) {
            if (y < gr.getMinY()) {
                PolarVector toAdd = new PolarVector().fromPolar(
                        vel.project(-Math.PI / 2).mult(1 + MainGameController.ELASTICITY).getMagnitude() - 4, -Math.PI / 2);
                vel = vel.add(toAdd);
                y = gr.getMinY() - RADIUS;
            } else {
                gr.goal();
            }
        }
    }

    public boolean intersects_goal_region(Goal goalRegion) {
        return goalRegion.intersects(x, y, 0.1, 0.1);
    }

    public boolean intersects_character(Character c) {
        double distx = Math.abs(this.x - c.getX());
        double disty = Math.abs(this.y - c.getY());
        if (distx > (c.getImageView().getViewport().getWidth() / 2 + RADIUS)) {
            return false;
        }
        if (disty > (c.getImageView().getViewport().getHeight() / 2 + RADIUS)) {
            return false;
        }

        if (distx <= (c.getImageView().getViewport().getWidth() / 2)) {
            x = c.getX() + (x < c.getX() ? (c.getImageView().getViewport().getWidth() / 2)
                    : -(c.getImageView().getViewport().getWidth() / 2));
            return true;
        }
        if (disty <= (c.getImageView().getViewport().getHeight() / 2)) {
            y = c.getY() - (y < c.getY() ? (c.getImageView().getViewport().getHeight() / 2)
                    : -(c.getImageView().getViewport().getHeight() / 2));
            return true;
        }

        double cornerDistance_sq = Math.pow(distx - c.getImageView().getViewport().getWidth() / 2, 2) +
                Math.pow(disty - c.getImageView().getViewport().getHeight() / 2, 2);
        boolean out = cornerDistance_sq <= Math.pow(RADIUS, 2);
        if (out) {
            x = c.getX() - (x < c.getX() ? (RADIUS + c.getImageView().getViewport().getWidth() / 2)
                    : -(RADIUS + c.getImageView().getViewport().getWidth() / 2));
            y = c.getY() - (y < c.getY() ? (RADIUS + c.getImageView().getViewport().getHeight() / 2)
                    : -(RADIUS + c.getImageView().getViewport().getHeight() / 2));
        }
        return out;
    }

    public void updatePos() {
        setTranslateX(x);
        setTranslateY(y);
        setRotate(angle);
    }

    public void reset(double x, double y) {
        this.x = x;
        this.y = y;
        this.vel = new PolarVector();
        this.setTranslateX(x);
        this.setTranslateY(y);
    }

    public PolarVector getVel() {
        return vel;
    }

    public void setVel(PolarVector vel) {
        this.vel = vel;
    }
}