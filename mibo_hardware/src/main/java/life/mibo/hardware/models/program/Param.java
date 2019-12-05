package life.mibo.hardware.models.program;

import org.json.JSONException;
import org.json.JSONObject;

public class Param {
    private String name;
    private String desc;
    private String value;
    private String unit;
    private String max;
    private String min;
    private String defValue;

    public Param(String name, String desc, String value, String unit, String max, String min, String defValue) {
        setName(name);
        setDesc(desc);
        setValue(value);
        setUnit(unit);
        setMax(max);
        setMin(min);
        setDefValue(defValue);
    }

    public JSONObject toJSON() {
        JSONObject param = new JSONObject();

        try {

            JSONObject paramObject = new JSONObject();

            param.accumulate("name", getName());
            param.accumulate("desc", getDesc());
            param.accumulate("value", getValue());
            param.accumulate("unit", getUnit());
            param.accumulate("max", getMax());
            param.accumulate("min", getMin());
            param.accumulate("default", getDefValue());

            String formattedParamName = getName().replace(" ", "");
            String upperChar = formattedParamName.substring(0, 1).toUpperCase();
            formattedParamName = upperChar + formattedParamName.substring(1);

            paramObject.accumulate(formattedParamName, param);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return param;
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

    public String getDefValue() {
        return defValue;
    }

    public void setDefValue(String defValue) {
        this.defValue = defValue;
    }
}
