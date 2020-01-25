package life.mibo.hardware.events;

import life.mibo.hardware.core.DataParser;

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
    private byte[] command;
    private boolean isCommand;

    public ProgramStatusEvent(int programtime, int actiontime, int pausetime, int currentprogram, int currrentblock , String uid) {
        this.remainingProgramTime = programtime;
        this.remainingProgramAction = actiontime;
        this.remainingProgramPause = pausetime;
        this.currentBlock = currentprogram;
        this.currentProgram = currrentblock;
        this.uid = uid;
    }

    public ProgramStatusEvent(byte[] command, String uid) {
        this.command = command;
        this.uid = uid;
        this.isCommand = true;
        // listener.onStatus(DataParser.getProgramStatusTime(command), DataParser.getProgramStatusAction(command), DataParser.getProgramStatusPause(command), DataParser.getProgramStatusCurrentBlock(command), DataParser.getProgramStatusCurrentProgram(command), uid);

    }
    public String getUid() {
        return uid;
    }

    public int getActionTime() {
        try {
            return DataParser.getProgramStatusAction(command);
        } catch (Exception e) {
            return remainingProgramAction;
        }
    }

    public int getPauseTime() {
        try {
            return DataParser.getProgramStatusPause(command);
        } catch (Exception e) {
            return remainingProgramPause;
        }
    }

    public int getRemainingTime() {
        try {
            return DataParser.getProgramStatusTime(command);
        } catch (Exception e) {
            return remainingProgramPause;
        }
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
