package life.mibo.hardware.models.program.parameters;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Fer on 22/03/2019.
 */

public class CustomParameter {

    @SerializedName("name")
    private String name = "";

    @SerializedName("desc")
    private String desc = "";

    @SerializedName("value")
    private String value = "0";

    @SerializedName("unit")
    private String unit = "";

    @SerializedName("max")
    private String max = "";

    @SerializedName("min")
    private String min = "";

    @SerializedName("default")
    private String mDefault = "";

    public CustomParameter() {

    }

    public CustomParameter(CustomParameter parameterToClone) {
        this.name = parameterToClone.name;
        this.desc = parameterToClone.desc;
        this.value = parameterToClone.value;
        this.unit = parameterToClone.unit;
        this.max = parameterToClone.max;
        this.min = parameterToClone.min;
        this.mDefault = parameterToClone.mDefault;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getValue() {
        return value;
    }

    public int getValueInteger() {
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

}
