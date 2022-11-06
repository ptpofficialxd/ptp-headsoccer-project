package adv.headsoccer_project.model;

import adv.headsoccer_project.controller.Game.MainGameController;
import adv.headsoccer_project.controller.SceneController;
import adv.headsoccer_project.controller.SpriteController;
import org.apache.logging.log4j.Logger;
import java.util.Timer;
import java.util.TimerTask;
import org.apache.logging.log4j.LogManager;
import javafx.scene.layout.StackPane;
import javafx.scene.input.KeyCode;
import adv.headsoccer_project.controller.MediaController;
import adv.headsoccer_project.view.GameView;

public class Character extends StackPane {
    private static Logger logger = LogManager.getLogger(Character.class);
    public static final int CHARACTER_HEIGHT = 96;
    private AnimatedSprite imageView;
    private double x;
    private double y;
    private PolarVector vel;
    private KeyCode leftKey;
    private KeyCode rightKey;
    private KeyCode upKey;
    private KeyCode kickKey;
    private KeyCode specialKey;
    private boolean isMidAir = true;
    private boolean canJump = false;
    private boolean directionR = true;
    private boolean isMoving;
    private boolean Jumping = false;
    private boolean kicking = true;
    private boolean won = false;
    private boolean specialQueued = false;
    private boolean doingSpecial = false;

    public Character(double x, double y, KeyCode leftKey, KeyCode rightKey, KeyCode upKey, KeyCode kickKey,
            KeyCode specialKey, String characterKey) {
        SpriteData spriteData = SpriteController.getSpriteData(characterKey);
        this.imageView = new AnimatedSprite(spriteData);
        this.imageView.setFitHeight(CHARACTER_HEIGHT);
        this.imageView.setPreserveRatio(true);
        this.x = x;
        this.y = y;
        this.vel = new PolarVector();
        this.setTranslateX(x);
        this.setTranslateY(y);
        this.leftKey = leftKey;
        this.rightKey = rightKey;
        this.upKey = upKey;
        this.kickKey = kickKey;
        this.specialKey = specialKey;
        this.kicking = false;
        this.getChildren().addAll(this.imageView);
    }

    public void move() {
        logger.debug(String.format("pos: [%.2f, %.2f] %s - %s", x, y, isMidAir ? "air" : "ground", vel));
        if (isMoving) {
            if (directionR) {
                vel = vel.add(MainGameController.ACCELERATION, 0);
            } else if (!directionR) {
                vel = vel.add(-MainGameController.ACCELERATION, 0);
            }
        }
        if (Jumping) {
            canJump = false;
            Jumping = false;
            isMidAir = true;
            vel = vel.add(0, MainGameController.JUMP_VELOCITY);
            MediaController.playSFX("jump");
        }
        double[] velArray = vel.toCartesian();
        x += velArray[0] * MainGameController.PHYSICS_FACTOR;
        y += velArray[1] * MainGameController.PHYSICS_FACTOR;
        if (isMidAir) {
            vel = vel.add(0, MainGameController.GRAVITY);
        }
        vel = vel.mult(isMidAir ? MainGameController.AIR_RESISTANCE - 0.05 : MainGameController.FRICTION);
        if (vel.getMagnitude() < 0.2d) {
            vel.setMagnitude(0);
        }
    }

    public void checkReachGameWall() {
        if (x <= imageView.getViewport().getWidth() / 2) {
            x = imageView.getViewport().getWidth() / 2;
        } else if (x + imageView.getViewport().getWidth() / 2 >= SceneController.getWidth()) {
            x = SceneController.getWidth() - (int) imageView.getViewport().getWidth() / 2;
        }
    }

    public void checkReachFloor() {
        if (y >= GameView.GROUND - CHARACTER_HEIGHT / 2) {
            canJump = true;
            isMidAir = false;
            vel = vel.project(0);
            y = GameView.GROUND - CHARACTER_HEIGHT / 2;
        } else if (y < GameView.GROUND - CHARACTER_HEIGHT / 2) {
            isMidAir = true;
        }
    }

    public void checkIntersectCharacter(Character c) {
        if (this.getBoundsInParent().intersects(c.getBoundsInParent())) {
            // if (this.x > c.getX())
            // this.x = c.getBoundsInParent().getWidth() / 2 + c.getX();
            // else
            // this.x = c.getBoundsInParent().getWidth() / 2 - c.getX();
            logger.info("intersect");
            PolarVector normal = vel.add(c.getVel().negate());
            double impulse = vel.add(c.getVel().negate()).mult(0.66 * (1 +
                    MainGameController.ELASTICITY))
                    .project(normal.getAngle())
                    .getMagnitude();
            PolarVector toAdd = new PolarVector().fromPolar(impulse,
                    normal.getAngle()).negate();
            vel = vel
                    .add(c.isKicking()
                            ? toAdd.add(new PolarVector().fromPolar(toAdd.mult(0.2).getMagnitude(),
                                    -Math.PI / 2))
                            : toAdd)
                    .mult(c.isKicking() ? 1.75 : 1);
            toAdd = new PolarVector().fromPolar(impulse, normal.getAngle());
            c.setVel(c.getVel().add(isKicking()
                    ? toAdd.add(new PolarVector().fromPolar(toAdd.mult(0.2).getMagnitude(),
                            -Math.PI / 2))
                    : toAdd)
                    .mult(isKicking() ? 1.75 : 1));
        }
    }

    public void kick() {
        if (!kicking) {
            kicking = true;
            MediaController.playSFX("kick");
            vel = vel.add(new PolarVector().fromPolar(15, directionR ? 0 : Math.PI));
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    kicking = false;
                }
            }, 500);
        }
    }

    public KeyCode getKickKey() {
        return kickKey;
    }

    public KeyCode getSpecialKey() {
        return specialKey;
    }

    public void trySpecial() {
        MainGameController.executeSpecial(this);
    }

    public void queueSpecial() {
        specialQueued = true;
    }

    public boolean isSpecialQueued() {
        return specialQueued;
    }

    public void exhaustSpecial() {
        specialQueued = false;
        MainGameController.exhaustSpecial(this);
    }

    public void reset(double x, double y) {
        this.x = x;
        this.y = y;
        this.vel = new PolarVector();
        this.setTranslateX(x);
        this.setTranslateY(y);
        this.kicking = false;
    }

    public void jump() {
        if (canJump) {
            Jumping = true;
        }
    }

    public void updatePos() {
        setTranslateX(x);
        setTranslateY(y);
    }

    public KeyCode getLeftKey() {
        return leftKey;
    }

    public KeyCode getRightKey() {
        return rightKey;
    }

    public KeyCode getUpKey() {
        return upKey;
    }

    public int getVxSign() {
        return (int) Math.signum(vel.toCartesian()[0]);
    }

    public AnimatedSprite getImageView() {
        return imageView;
    }

    public void setIsMoving(boolean isMoving) {
        this.isMoving = isMoving;
    }

    public void setDirectionR(boolean directionR) {
        this.directionR = directionR;
    }

    public boolean isMidAir() {
        return isMidAir;
    }

    public boolean getDirectionR() {
        return directionR;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public PolarVector getVel() {
        return vel;
    }

    public void setVel(PolarVector vel) {
        this.vel = vel;
    }

    public boolean isKicking() {
        return kicking;
    }

    public void setWon(boolean won) {
        this.won = won;
    }

    public boolean hasWon() {
        return won;
    }

    public boolean isDoingSpecial() {
        return doingSpecial;
    }

    public void setDoingSpecial(boolean doingSpecial) {
        this.doingSpecial = doingSpecial;
    }
}