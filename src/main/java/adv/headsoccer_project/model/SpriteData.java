package adv.headsoccer_project.model;

import java.util.HashMap;
import org.json.JSONArray;
import org.json.JSONObject;

public class SpriteData {
    private String sheetURL;
    private HashMap<String, AnimationData> animationData;

    public SpriteData(String URL, JSONArray animationRaw) {
        this.sheetURL = URL;
        this.animationData = new HashMap<>();
        for (int i = 0; i < animationRaw.length(); i++) {
            String name = animationRaw.getJSONObject(i).getString("name");
            JSONObject offset = animationRaw.getJSONObject(i).getJSONObject("offset");
            JSONObject offsetReversed = animationRaw.getJSONObject(i).getJSONObject("offset_r");
            int rolltype = animationRaw.getJSONObject(i).getInt("rolltype");
            int gap = animationRaw.getJSONObject(i).getInt("gap");
            JSONArray data = animationRaw.getJSONObject(i).getJSONArray("frames");
            this.animationData.put(name, new AnimationData(offset.getInt("x"), offset.getInt("y"),
                    offsetReversed.getInt("x"), offsetReversed.getInt("y"), rolltype, gap, data));
        }
    }

    public String getSheetURL() {
        return sheetURL;
    }

    public AnimationData getAnimationData(String key) {
        return animationData.get(key);
    }
}