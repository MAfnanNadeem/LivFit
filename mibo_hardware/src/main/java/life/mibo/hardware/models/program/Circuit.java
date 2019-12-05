package life.mibo.hardware.models.program;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Fer on 22/03/2019.
 */

public class Circuit {

    @SerializedName("name")
    public String name;

    @SerializedName("description")
    public String description;

    @SerializedName("programs")
    public Program[] programs;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Program[] getPrograms() {
        return programs;
    }

    public void setPrograms(Program[] programs) {
        this.programs = programs;
    }


}
