package life.mibo.fitbitsdk.service.models;

import com.google.gson.annotations.Expose;

@SuppressWarnings("unused")
public class ActivityLevel {

    @Expose
    private Long id;
    @Expose
    private Long maxSpeedMPH;
    @Expose
    private Double mets;
    @Expose
    private Long minSpeedMPH;
    @Expose
    private String name;
    @Expose
    private Long minutes;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getMaxSpeedMPH() {
        return maxSpeedMPH;
    }

    public void setMaxSpeedMPH(Long maxSpeedMPH) {
        this.maxSpeedMPH = maxSpeedMPH;
    }

    public Double getMets() {
        return mets;
    }

    public void setMets(Double mets) {
        this.mets = mets;
    }

    public Long getMinSpeedMPH() {
        return minSpeedMPH;
    }

    public void setMinSpeedMPH(Long minSpeedMPH) {
        this.minSpeedMPH = minSpeedMPH;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getMinutes() {
        return minutes;
    }

    public void setMinutes(Long minutes) {
        this.minutes = minutes;
    }
}
