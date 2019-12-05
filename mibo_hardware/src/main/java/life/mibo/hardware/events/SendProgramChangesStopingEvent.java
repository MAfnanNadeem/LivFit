package life.mibo.hardware.events;

import life.mibo.hardware.models.program.Program;

public class SendProgramChangesStopingEvent {

    private Program program;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    private String uid;

    public SendProgramChangesStopingEvent(Program program, String uid) {
        this.program = program;
        this.uid = uid;
    }

    public Program getProgram() {
        return program;
    }

}