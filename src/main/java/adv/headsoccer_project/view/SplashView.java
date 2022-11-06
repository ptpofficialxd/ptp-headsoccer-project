package adv.headsoccer_project.view;

import adv.headsoccer_project.controller.SceneController;
import javafx.beans.property.DoubleProperty;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import adv.headsoccer_project.controller.ImageHandler;

public class SplashView extends BorderPane {
    public static enum SplashType {
        GOAL, P1WIN, P2WIN, DRAW
    }

    private ImageView splashImage;

    public SplashView(SplashType type) {
        this.setOpacity(0);
        this.getStyleClass().add("splash");
        this.setPrefSize(SceneController.getWidth(), SceneController.getHeight());
        switch (type) {
            case GOAL:
                splashImage = new ImageView(ImageHandler.getImage("/assets/img/goal.gif"));
                break;
            case P1WIN:
                splashImage = new ImageView(ImageHandler.getImage("/assets/img/p1win.gif"));
                break;
            case P2WIN:
                splashImage = new ImageView(ImageHandler.getImage("/assets/img/p2win.gif"));
                break;
            case DRAW:
                splashImage = new ImageView(ImageHandler.getImage("/assets/img/draw.gif"));
                break;
        }
        this.setCenter(splashImage);
    }

    public DoubleProperty getFade() {
        return this.opacityProperty();
    }
}