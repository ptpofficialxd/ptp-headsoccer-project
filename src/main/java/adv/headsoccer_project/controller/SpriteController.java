package adv.headsoccer_project.controller;

import java.util.HashMap;
import adv.headsoccer_project.Launcher;
import adv.headsoccer_project.model.SpriteData;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.IOException;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

public class SpriteController {
    private static HashMap<String, SpriteData> sprites = new HashMap<>();
    private static Logger logger = LogManager.getLogger(SpriteController.class);
    static {
        load();
    }

    public static void load() {
        try {
            String text = new String(Launcher.class.getResourceAsStream("/assets/data/sprite_data.json").readAllBytes())
                    .replaceAll("[\\n\\t]", "");
            JSONArray jsonarray = new JSONArray(text);
            for (int i = 0; i < jsonarray.length(); i++) {
                JSONObject temp_obj = jsonarray.getJSONObject(i);
                sprites.put(temp_obj.getString("name"),
                        new SpriteData(temp_obj.getString("sheetURL"), temp_obj.getJSONArray("animation")));
            }
            logger.info("Sprites loaded");
        } catch (IOException e) {
            logger.error(e.getCause(), e);
        }
    }

    public static SpriteData getSpriteData(String key) {
        return sprites.get(key);
    }
}