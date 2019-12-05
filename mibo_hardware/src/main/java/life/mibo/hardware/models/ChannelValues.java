package life.mibo.hardware.models;

import org.json.JSONException;
import org.json.JSONObject;

public class ChannelValues {
    private int[] channelValues;

    public ChannelValues(int[] channelValues) {
        this.channelValues = channelValues;
    }

    public int[] getChannelValues() {
        return channelValues;
    }

    public void setChannelValues(int[] channelValues) {
        this.channelValues = channelValues;
    }

    public JSONObject toJSON() {
        JSONObject channelValues = new JSONObject();

        try {
            channelValues.accumulate("channelValues", this.channelValues);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return channelValues;
    }
}
