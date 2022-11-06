package adv.headsoccer_project.controller;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import adv.headsoccer_project.view.GameView;
import adv.headsoccer_project.view.MenuView;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import adv.headsoccer_project.model.Keys;

public class SceneController {
    private static int width = 1366;
    private static int height = 768;
    private HashMap<String, Pane> sceneMap = new HashMap<>();
    private static Logger logger = LogManager.getLogger(SceneController.class);
    private Scene main;
    private String active = "";
    private Keys keys;

    private void init() {
        keys = new Keys();
        this.addScene("Menu", new MenuView());
        this.addScene("Game", new GameView());
        this.main.setOnKeyPressed(e -> this.getKeys().add(e.getCode()));
        this.main.setOnKeyReleased(e -> this.getKeys().remove(e.getCode()));
    }

    public SceneController() {
        this.main = new Scene(new Pane(), width, height);
        init();
    }

    public SceneController(Scene main) {
        this.main = main;
        init();
    }

    public void addScene(String name, Pane pane) {
        sceneMap.put(name, pane);
        logger.info(String.format("Added \"%s\":%s", name, pane.getClass()));
    }

    public void removeScene(String name) {
        sceneMap.remove(name);
    }

    public void activate(String name) {
        active = name;
        Method onStart = null;
        try {
            onStart = getScene(name).getClass().getMethod("onStart", (Class<?>[]) null);
        } catch (NoSuchMethodException e) {
            logger.warn(String.format("Scene \"%s\" does not have a onStart method", name));
        }
        if (onStart != null)
            try {
                onStart.invoke(getScene(name), (Object[]) null);
            } catch (IllegalAccessException e) {
                logger.error(String.format("Error invoking onStart on \"%s\"", name));
            } catch (InvocationTargetException e) {
                logger.error(e.getCause(), e);
            }
        main.setRoot(sceneMap.get(name));
        logger.info(String.format("Changed Scene to %s", name));
    }

    public Pane getScene(String name) {
        return sceneMap.get(name);
    }

    public Pane getActiveScene() {
        return sceneMap.get(active);
    }

    public Scene getScene() {
        return main;
    }

    public static int getWidth() {
        return width;
    }

    public static int getHeight() {
        return height;
    }

    public Keys getKeys() {
        return keys;
    }
}