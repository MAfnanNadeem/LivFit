package life.mibo.fitbitsdk.service.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@SuppressWarnings("unused")
public class Steps {

    @SerializedName("dateTime")
    @Expose
    private String date;
    @Expose
    private Long value;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Long getValue() {
        return value;
    }

    public void setValue(Long value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "Steps{" +
                "date='" + date + '\'' +
                ", value=" + value +
                '}';
    }
}
