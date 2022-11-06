package adv.headsoccer_project.model;

import adv.headsoccer_project.controller.Game.MainGameController;
import javafx.application.Platform;
import javafx.geometry.Rectangle2D;

public class Goal extends Rectangle2D {
    private int player;

    public Goal(double x1, double y1, double x2, double y2, int player) {
        super(x1, y1, x2 - x1, y2 - y1);
        this.player = player;
    }

    public void goal() {
        if (!MainGameController.isGoalable())
            return;
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                if (player == 1)
                    MainGameController.incrementP1();
                else if (player == 2)
                    MainGameController.incrementP2();
            }
        });
        MainGameController.setGoalable(false);
    }
}