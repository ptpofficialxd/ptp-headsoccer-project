package adv.headsoccer_project.model;

import javafx.scene.input.KeyCode;
import java.util.HashSet;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Keys {
    private static Logger logger = LogManager.getLogger(Keys.class);
    private HashSet<KeyCode> keys;

    public Keys() {
        keys = new HashSet<>();
    }

    public void add(KeyCode key) {
        keys.add(key);
        logger.debug(keys);
    }

    public void remove(KeyCode key) {
        keys.remove(key);
        logger.debug(keys);
    }

    public boolean isPressed(KeyCode key) {
        return keys.contains(key);
    }

    public void clear() {
        keys.clear();
    }
}