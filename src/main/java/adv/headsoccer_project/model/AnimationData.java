package adv.headsoccer_project.model;

import org.json.JSONArray;
import org.json.JSONObject;

public class AnimationData {
    private int ox;
    private int oy;
    private int orx;
    private int ory;
    private int rolltype;
    private int gap;
    private int[][] frames;

    public AnimationData(int ox, int oy, int orx, int ory, int rolltype, int gap, JSONArray rawFrames) {
        this.ox = ox;
        this.oy = oy;
        this.orx = orx;
        this.ory = ory;
        int x = 0;
        int y = 0;
        this.frames = new int[rawFrames.length()][4];
        for (int i = 0; i < rawFrames.length(); i++) {
            JSONObject frame = rawFrames.getJSONObject(i);
            this.frames[i][0] = x;
            this.frames[i][1] = y;
            this.frames[i][2] = frame.getInt("w");
            this.frames[i][3] = frame.getInt("h");
            if (rolltype == 1)
                x += this.frames[i][2] + gap;
            else if (rolltype == 2)
                y += this.frames[i][3] + gap;

        }
    }

    public int[] getOffset() {
        return new int[] { ox, oy };
    }

    public int[] getOffsetReversed() {
        return new int[] { orx, ory };
    }

    public int getRolltype() {
        return rolltype;
    }

    public int getGap() {
        return gap;
    }

    public int[][] getFrames() {
        return frames;
    }
}