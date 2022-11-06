package adv.headsoccer_project.model;

import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import javafx.geometry.Rectangle2D;
import adv.headsoccer_project.controller.ImageHandler;

public class AnimatedSprite extends ImageView {
    SpriteData spriteData;
    int gridW, gridH;
    int currX = 0;
    int currY = 0;
    int counter = 0;
    String currKey = "";
    AnimationData animationData;
    int[][] frames;
    boolean isReversed;
    boolean finishedLoop;
    Logger logger = LogManager.getLogger(AnimatedSprite.class);

    public AnimatedSprite(SpriteData spriteData) {
        this.spriteData = spriteData;
        this.setImage(new Image(ImageHandler.getImage(this.spriteData.getSheetURL())));
        setPlaying("idle", true);
        tick();
    }

    public void tick() {
        int[] offset = isReversed ? animationData.getOffsetReversed() : animationData.getOffset();
        this.setScaleX(1);
        if (animationData.getOffsetReversed()[0] == -1 && animationData.getOffsetReversed()[1] == -1 && isReversed) {
            offset = animationData.getOffset();
            this.setScaleX(-1);
        }
        if (animationData.getOffset()[0] == -1 && animationData.getOffset()[1] == -1 && !isReversed) {
            offset = animationData.getOffsetReversed();
            this.setScaleX(-1);
        }
        int x = offset[0];
        int y = offset[1];
        x += frames[counter][0];
        y += frames[counter][1];
        // logger.debug(String.format("[%d + %d, %d + %d]", offset[0],
        // frames[counter][0], offset[1], frames[counter][1]));
        this.setViewport(new Rectangle2D(x, y, frames[counter][2], frames[counter][3]));
        counter = (counter + 1) % frames.length;
        if (counter == 0)
            finishedLoop = true;
        else
            finishedLoop = false;
    }

    public void setPlaying(String key, boolean isReversed) {
        if (key.equals(currKey)) {
            this.isReversed = isReversed;
            return;
        }
        currKey = key;
        this.animationData = this.spriteData.getAnimationData(key);
        this.frames = this.animationData.getFrames();
        this.isReversed = isReversed;
        counter = 0;
    }

    public boolean isFinishedLoop() {
        return finishedLoop;
    }
}