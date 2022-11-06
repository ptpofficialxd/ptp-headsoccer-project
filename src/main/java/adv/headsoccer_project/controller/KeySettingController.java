package adv.headsoccer_project.controller;

import java.io.IOException;
import java.util.HashMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import javafx.scene.input.KeyCode;
import adv.headsoccer_project.Launcher;

public class KeySettingController {
    private static Logger logger = LogManager.getLogger(KeySettingController.class);
    private static HashMap<String, KeyCode> keyConfig = new HashMap<>();
    static {
        load();
    }

    private static void load() {
        try {
            String text = new String(Launcher.class.getResourceAsStream("/keysetting.json").readAllBytes())
                .replaceAll("[\\n\\t]", "");
            JSONObject json = new JSONObject(text);
            for (String keySet : json.keySet()) {
                keyConfig.put(keySet, KeyCode.valueOf(json.getString(keySet)));
            }
            logger.info("Successfully Registered Keys");
        } catch (IOException e){
            logger.fatal(e.getCause(), e);
        }

    }

    public static HashMap<String, KeyCode> getKeyConfig() {
        return keyConfig;
    }
}