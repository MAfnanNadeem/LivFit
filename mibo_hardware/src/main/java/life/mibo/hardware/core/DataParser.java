package life.mibo.hardware.core;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;

import life.mibo.hardware.models.DeviceColors;
import life.mibo.hardware.models.program.Program;

import static life.mibo.hardware.constants.CommunicationConstants.COMMAND_GET_DEVICE_STATUS;
import static life.mibo.hardware.constants.CommunicationConstants.COMMAND_GET_FIRMWARE_REVISION;
import static life.mibo.hardware.constants.CommunicationConstants.COMMAND_PING_STIMULATOR;
import static life.mibo.hardware.constants.CommunicationConstants.COMMAND_RESET_CURRENT_CYCLE;
import static life.mibo.hardware.constants.CommunicationConstants.COMMAND_SEARCH_STIMULATOR;
import static life.mibo.hardware.constants.CommunicationConstants.COMMAND_SET_CHANNELS_LEVELS;
import static life.mibo.hardware.constants.CommunicationConstants.COMMAND_SET_COMMON_STIMULATION_PARAMETERS;
import static life.mibo.hardware.constants.CommunicationConstants.COMMAND_SET_COMMON_STIMULATION_PARAMETERS_ON_HOT;
import static life.mibo.hardware.constants.CommunicationConstants.COMMAND_SET_DEVICE_COLOR;
import static life.mibo.hardware.constants.CommunicationConstants.COMMAND_SET_MAIN_LEVEL;
import static life.mibo.hardware.constants.CommunicationConstants.COMMAND_START_CURRENT_CYCLE;
import static life.mibo.hardware.constants.CommunicationConstants.COMMAND_STOP_CURRENT_CYCLE;


public class DataParser {
    public final static int BOOSTER = 0;
    public final static int RXL = 1;

    public static void transformSignedToUnsignedBytes(byte[] message) {
        for (int i = 0; i < message.length; i++) {
            message[i] = (byte) (message[i] & 0xFF);
        }
    }

    public static int getCommand(byte[] command) {
        int aux;
        aux = command[0] & 0xFF;
        return aux;
    }

    public static int getMainLevel(byte[] command) {
        int aux;
        aux = command[3] & 0xFF;
        return aux;
    }

    public static int getMainLevelAsync(byte[] command) {
        int aux;
        aux = command[2] & 0xFF;
        return aux;
    }

    public static int getProgramStatusTime(byte[] command) {
        int aux;
        aux = ((command[3] & 0xff) << 8) | (command[2] & 0xff);
        return aux;
    }

    public static int getProgramStatusPause(byte[] command) {
        int aux;
        aux = ((command[7] & 0xff) << 8) | (command[6] & 0xff);
        return aux;
    }

    public static int getProgramStatusAction(byte[] command) {
        int aux;
        aux = ((command[5] & 0xff) << 8) | (command[4] & 0xff);
        return aux;
    }

    public static int getProgramStatusCurrentBlock(byte[] command) {
        int aux;
        aux = command[8] & 0xFF;
        return aux;
    }

    public static int getProgramStatusCurrentProgram(byte[] command) {
        int aux;
        aux = command[9] & 0xFF;
        return aux;
    }

    public static byte[] getUID(byte[] command) {
        Logger.e("DataParser", "getUID " + Arrays.toString(command));
        if (command.length > 6)
            return new byte[]{command[2], command[3], command[4], command[5], command[6], command[7]};
        return new byte[]{0,1,2,3,4,5};
    }

    public static byte[] getUIDRxl(byte[] command) {
        Logger.e("DataParser", "getUID " + Arrays.toString(command));
        if (command.length > 2)
            return new byte[]{1, 2, command[0], command[1], command[2], command[3]};
        return new byte[]{0, 1, 2, 3, 4, 5};
    }

    public static boolean[] getChannelAlarms(byte[] command) {
        boolean[] aux = new boolean[]{false, false, false, false, false, false, false, false, false, false};
        aux[0] = ((command[4] & 1) == 1);
        aux[1] = ((command[4] & 2) == 2);
        aux[2] = ((command[4] & 4) == 4);
        aux[3] = ((command[4] & 8) == 8);
        aux[4] = ((command[4] & 16) == 16);
        aux[5] = ((command[4] & 32) == 32);
        aux[6] = ((command[4] & 64) == 64);
        aux[7] = ((command[4] & 128) == 128);
        aux[8] = ((command[5] & 256) == 256);
        aux[9] = ((command[5] & 512) == 512);
        return aux;
    }

    public static boolean[] getStatusFlags(byte[] command) {
        boolean[] aux = new boolean[]{false, false, false, false, false};
        aux[0] = ((command[6] & 1) == 1);
        aux[1] = ((command[6] & 2) == 2);
        aux[2] = ((command[6] & 4) == 4);
        aux[3] = ((command[6] & 8) == 8);
        aux[4] = ((command[6] & 16) == 16);
        return aux;
    }

    public static byte[] sendStart() {
        byte[] aux = new byte[0];
        return fullMessage(new byte[]{COMMAND_START_CURRENT_CYCLE}, new byte[]{0}, aux, BOOSTER);
    }

    public static byte[] sendReStart() {
        byte[] aux = new byte[0];
        return fullMessage(new byte[]{COMMAND_RESET_CURRENT_CYCLE}, new byte[]{0}, aux, BOOSTER);
    }

    public static byte[] sendStop() {
        byte[] aux = new byte[0];
        return fullMessage(new byte[]{COMMAND_STOP_CURRENT_CYCLE}, new byte[]{0}, aux, BOOSTER);
    }

    public static byte[] sendMain(int main) {
        byte[] aux = new byte[1];
        aux[0] = (byte) main;
        return fullMessage(new byte[]{COMMAND_SET_MAIN_LEVEL}, new byte[]{1}, aux, BOOSTER);
    }

    public static byte[] sendPing() {
        byte[] aux = new byte[0];
        return fullMessage(new byte[]{COMMAND_PING_STIMULATOR}, new byte[]{0}, aux, BOOSTER);
    }

    public static byte[] sendGetFirm() {
        byte[] aux = new byte[0];
        return fullMessage(new byte[]{COMMAND_GET_FIRMWARE_REVISION}, new byte[]{0}, aux, BOOSTER);
    }

    public static byte[] sendGetStatus(int type) {
        return fullMessage(new byte[]{COMMAND_GET_DEVICE_STATUS}, new byte[]{0}, new byte[0], type);
    }

    public static int getStatusBattery(byte[] command) {
        int aux;
        aux = command[2] & 0xFF;
        return aux;
    }

    public static int getStatusSignal(byte[] command) {
        int aux;
        aux = command[3];
        return aux;
    }

    public static byte[] sendSearchCommand() {
        byte[] aux = new byte[0];
        return fullMessage(new byte[]{COMMAND_SEARCH_STIMULATOR}, new byte[]{0}, aux, BOOSTER);
    }

    public static byte[] sendColor(byte[] color, int type) {
        byte[] aux = new byte[3];
        aux[0] = color[0];
        aux[1] = color[1];
        aux[2] = color[2];
        return fullMessage(new byte[]{COMMAND_SET_DEVICE_COLOR}, new byte[]{3}, aux, type);
    }

    public static byte[] sendRxlColor(int color, int time, int type) {
        try {
            int r = (color >> 16) & 0xFF;
            int g = (color >> 8) & 0xFF;
            int b = (color) & 0xFF;
            int t1 = (time >> 8) & 0xFF;
            int t2 = (time) & 0xFF;
            Logger.e("sendRxlColor " + color + " : r"+r+" g"+g+" b "+b + time+" : "+t1 +"  "+t2);
            return fullMessage(new byte[]{COMMAND_SET_DEVICE_COLOR}, new byte[]{5}, new byte[]{(byte) r, (byte) g, (byte) b, (byte) t2, (byte) t1}, type);
        } catch (Exception e) {
            // Color c = Color.valueOf(color);
            Logger.e("sendRxlColor " + color + " : " + time, e);
            e.printStackTrace();
        }
        return new byte[0];

    }

    public static byte[] sendRxlColor(byte[] color, int type) {
        return fullMessage(new byte[]{COMMAND_SET_DEVICE_COLOR}, new byte[]{3}, color, type);
    }

    public static byte[] sendMainLevel(int main) {
        byte[] aux = new byte[1];
        aux[0] = (byte) main;
        return fullMessage(new byte[]{COMMAND_SET_MAIN_LEVEL}, new byte[]{1}, aux, BOOSTER);
    }

    public static byte[] sendLevels(int[] levels) {
        //stage = stage,freq,pulse,duration,rampup,stim,rdown,pause,freqrel,tpulserel;
        byte[] aux = new byte[10];
        aux[0] = (byte) levels[0];//
        aux[1] = (byte) levels[1];//
        aux[2] = (byte) levels[2];//
        aux[3] = (byte) levels[3];//
        aux[4] = (byte) levels[4];//
        aux[5] = (byte) levels[5];//
        aux[6] = (byte) levels[6];//
        aux[7] = (byte) levels[7];//
        aux[8] = (byte) levels[8];//
        aux[9] = (byte) levels[9];//

        return fullMessage(new byte[]{COMMAND_SET_CHANNELS_LEVELS}, new byte[]{10}, aux, BOOSTER);
    }

    public static byte[] sendProgram(int numberOfPrograms, int programIndex, Program program) {
//if index0 and numberOfPrograms 1 is a single program, else you need to send multiple programs

        ByteArrayOutputStream outputProgram = new ByteArrayOutputStream();
        try {
            outputProgram.write(generateOutputProgram(numberOfPrograms, programIndex, program));

        } catch (IOException e) {
            e.printStackTrace();
        }
        return fullMessage(new byte[]{COMMAND_SET_COMMON_STIMULATION_PARAMETERS}, new byte[]{(byte) outputProgram.toByteArray().length}, outputProgram.toByteArray(), BOOSTER);
    }

    public static byte[] sendProgramOnHot(int numberOfPrograms, int programIndex, Program program) {
//if index0 and numberOfPrograms 1 is a single program, else you need to send multiple programs

        ByteArrayOutputStream outputProgram = new ByteArrayOutputStream();
        try {
            outputProgram.write(generateOutputProgram(numberOfPrograms, programIndex, program));

        } catch (IOException e) {
            e.printStackTrace();
        }
        return fullMessage(new byte[]{COMMAND_SET_COMMON_STIMULATION_PARAMETERS_ON_HOT}, new byte[]{(byte) outputProgram.toByteArray().length}, outputProgram.toByteArray(), BOOSTER);
    }


    private static byte[] generateOutputProgram(int numberOfPrograms, int programIndex, Program program) {
        byte[] blocks = new byte[14 * program.getBlocks().size()];
        for (int i = 0; i < program.getBlocks().size(); i++) {
            blocks[0 + (i * 14)] = (byte) i;// block index
            blocks[1 + (i * 14)] = (byte) ((Integer.parseInt(program.getBlocks().get(i).getBlockDuration().getValue())) & 0xFF);
            blocks[2 + (i * 14)] = (byte) (((Integer.parseInt(program.getBlocks().get(i).getBlockDuration().getValue())) >> 8) & 0xFF);
            blocks[3 + (i * 14)] = (byte) ((Integer.parseInt(program.getBlocks().get(i).getPauseDuration().getValue())) & 0xFF);
            blocks[4 + (i * 14)] = (byte) (((Integer.parseInt(program.getBlocks().get(i).getPauseDuration().getValue())) >> 8) & 0xFF);
            blocks[5 + (i * 14)] = (byte) ((Integer.parseInt(program.getBlocks().get(i).getActionDuration().getValue())) & 0xFF);
            blocks[6 + (i * 14)] = (byte) (((Integer.parseInt(program.getBlocks().get(i).getActionDuration().getValue())) >> 8) & 0xFF);
            blocks[7 + (i * 14)] = (byte) ((Integer.parseInt(program.getBlocks().get(i).getUpRampDuration().getValue())) & 0xFF);
            blocks[8 + (i * 14)] = (byte) (((Integer.parseInt(program.getBlocks().get(i).getUpRampDuration().getValue())) >> 8) & 0xFF);
            blocks[9 + (i * 14)] = (byte) ((Integer.parseInt(program.getBlocks().get(i).getDownRampDuration().getValue())) & 0xFF);
            blocks[10 + (i * 14)] = (byte) (((Integer.parseInt(program.getBlocks().get(i).getDownRampDuration().getValue())) >> 8) & 0xFF);
            blocks[11 + (i * 14)] = (byte) ((Integer.parseInt(program.getBlocks().get(i).getPulseWidth().getValue())) & 0xFF);
            blocks[12 + (i * 14)] = (byte) (((Integer.parseInt(program.getBlocks().get(i).getPulseWidth().getValue())) >> 8) & 0xFF);
            blocks[13 + (i * 14)] = (byte) Integer.parseInt(program.getBlocks().get(i).getFrequency().getValue());
        }

        ByteArrayOutputStream outputProgram = new ByteArrayOutputStream();
        try {
            outputProgram.write((byte) numberOfPrograms);//single program
            outputProgram.write((byte) programIndex);//index first(single) program
            outputProgram.write((byte) Integer.parseInt(program.getDuration().getValue()) & 0xFF);   //program duration
            outputProgram.write((byte) (Integer.parseInt(program.getDuration().getValue()) >> 8) & 0xFF);   //program duration
            outputProgram.write((byte) program.getBlocks().size());   //number of blocks
            outputProgram.write(blocks);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return outputProgram.toByteArray();
    }

    public static byte[] sendCircuit(int[] circuit) {
        byte[] aux = new byte[0];


        return aux;
    }

    //public static boolean isRxl = true;

    private static byte[] fullMessage(byte[] code, byte[] length, byte[] data, int type) {
        byte[] header;
        if (type == RXL) {
            header = getRxlHeader();
        } else {
            header = getBoosterHeader();
        }

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            outputStream.write(header);
            outputStream.write(code);
            outputStream.write(length);
            outputStream.write(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
        byte[] c = outputStream.toByteArray();

        outputStream.write((byte) (crc16(c) & 0xFF));
        outputStream.write((byte) ((crc16(c) >> 8) & 0xFF));

        Logger.e("Writing Header1 " + Arrays.toString(header));
        Logger.e("Writing Code2 " + Arrays.toString(code));
        Logger.e("Writing Length3 " + Arrays.toString(length));
        Logger.e("Writing Message4 " + Arrays.toString(data));

        return outputStream.toByteArray();
    }

    private static byte[] getBoosterHeader() {
        byte[] header = new byte[5];
        header[0] = 'M';
        header[1] = 'I';
        header[2] = 'B';
        header[3] = 'O';
        header[4] = '\0';
        return header;
    }

    private static byte[] getRxlHeader() {
        byte[] header = new byte[6];
        header[0] = 'M';
        header[1] = 'B';
        header[2] = 'R';
        header[3] = 'X';
        header[4] = 'L';
        header[5] = '\0';
        return header;
    }

    private static byte[] fullMessageRxl(byte[] code, byte[] length, byte[] data) {
        byte[] header = new byte[6];
        header[0] = 'M';
        header[1] = 'B';
        header[2] = 'R';
        header[3] = 'X';
        header[4] = 'L';
        header[5] = '\0';
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            outputStream.write(header);
            outputStream.write(code);
            outputStream.write(length);
            outputStream.write(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
        byte[] c = outputStream.toByteArray();

        outputStream.write((byte) (crc16(c) & 0xFF));
        outputStream.write((byte) ((crc16(c) >> 8) & 0xFF));

        Logger.e("Writing Message5 " + Arrays.toString(header));
        Logger.e("Writing Message6 " + Arrays.toString(code));
        Logger.e("Writing Message7 " + Arrays.toString(length));
        Logger.e("Writing Message8 " + Arrays.toString(data));

        return outputStream.toByteArray();
    }

    // [-64, 2, -45, -62]
    public static int getRxlTime(byte[] command) {
        int aux;
        aux = ((command[3] & 0xff) << 8) | (command[2] & 0xff);
        return aux;
    }

    public static String getRxlTimeTest(byte[] command) {
        StringBuilder time = new StringBuilder();
        if (command.length > 2) {
            int a = command[2] & 0xff;
            int b = command[3] & 0xff;
            time.append(a);
            time.append(" ");
            time.append(b);
            b = b * 256;
            b += +a;
            time.append(" - ");
            time.append(b);
            time.append(" ms");
            time.append(" - ");
            time.append(b / 1000);
        }
        time.append(" sec");
        return time.toString();
    }

    public static byte[] getRxlColor(int color, int time) {
        try {
            int r = (color >> 16) & 0xFF;
            int g = (color >> 8) & 0xFF;
            int b = (color) & 0xFF;
            int t1 = (time >> 8) & 0xFF;
            int t2 = (time) & 0xFF;
            return new byte[]{(byte) r, (byte) g, (byte) b, (byte) t1, (byte) t2};
        } catch (Exception e) {
            // Color c = Color.valueOf(color);
        }
        return DeviceColors.VIOLET_BYTES;
    }

    public static byte[] parseRxlTime(int time) {
        int a = (time >> 8) & 0xFF;
        int b = (time) & 0xFF;
        return new byte[]{(byte) a, (byte) b};
    }

    static int crc16(final byte[] buffer) {
        int crc = 0xFFFF;

        for (int j = 0; j < buffer.length; j++) {
            crc = ((crc >>> 8) | (crc << 8)) & 0xffff;
            crc ^= (buffer[j] & 0xff);//byte to int, trunc sign
            crc ^= ((crc & 0xff) >> 4);
            crc ^= (crc << 12) & 0xffff;
            crc ^= ((crc & 0xFF) << 5) & 0xffff;
        }
        crc &= 0xffff;
        return crc;
    }

}
