package life.mibo.hardware.models.program.parameters;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Fer on 22/03/2019.
 */

public class Duration {


    @SerializedName("value")
    private String value = "0";

    @SerializedName("unit")
    private String unit = "s";

    @SerializedName("max")
    private String max = "9000";

    @SerializedName("min")
    private String min = "0";

    @SerializedName("default")
    private String mDefault = "0";

    @SerializedName("format")
    private String format = "";

    public Duration(){

    }

    public Duration(Duration durationToClone){
        this.value = durationToClone.value;
        this.unit = durationToClone.unit;
        this.max = durationToClone.max;
        this.min = durationToClone.min;
        this.mDefault = durationToClone.mDefault;
        this.format = durationToClone.format;
    }

    public String getValue() {
        return value;
    }

    public int getValueInt() {
        return Integer.parseInt(value);
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getMax() {
        return max;
    }

    public void setMax(String max) {
        this.max = max;
    }

    public String getMin() {
        return min;
    }

    public void setMin(String min) {
        this.min = min;
    }

    public String getmDefault() {
        return mDefault;
    }

    public void setmDefault(String mDefault) {
        this.mDefault = mDefault;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

}
