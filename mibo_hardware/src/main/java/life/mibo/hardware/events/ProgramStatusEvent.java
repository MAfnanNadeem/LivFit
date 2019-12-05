package life.mibo.hardware.events;

/**
 * Created by Fer on 12/04/2019.
 */

public class ProgramStatusEvent {

    private int remainingProgramTime;
    private int remainingProgramAction;
    private int remainingProgramPause;

    private int currentBlock;
    private int currentProgram;

    private String uid;

    public ProgramStatusEvent(int programtime, int actiontime, int pausetime, int currentprogram, int currrentblock , String uid) {
        this.remainingProgramTime = programtime;
        this.remainingProgramAction = actiontime;
        this.remainingProgramPause = pausetime;
        this.currentBlock = currentprogram;
        this.currentProgram = currrentblock;
        this.uid = uid;
    }

    public String getUid() {
        return uid;
    }

    public int getRemainingProgramTime() {
        return remainingProgramTime;
    }

    public int getRemainingProgramAction() {
        return remainingProgramAction;
    }

    public int getRemainingProgramPause() {
        return remainingProgramPause;
    }

    public int getCurrentBlock() {
        return currentBlock;
    }

    public void setCurrentBlock(int currentBlock) {
        this.currentBlock = currentBlock;
    }

    public int getCurrentProgram() {
        return currentProgram;
    }

    public void setCurrentProgram(int currentProgram) {
        this.currentProgram = currentProgram;
    }
}
