package life.mibo.hardware.events;

import life.mibo.hardware.models.program.Program;

/**
 * Created by Fer on 25/03/2019.
 */

public class SendProgramEvent {

    private Program program;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    private String uid;

    public SendProgramEvent(Program program, String uid) {
        this.program = program;
        this.uid = uid;
    }

    public Program getProgram() {
        return program;
    }

}
