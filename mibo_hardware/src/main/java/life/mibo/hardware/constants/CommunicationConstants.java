package life.mibo.hardware.constants;

/**
 * Created by Fer on 20/03/2019.
 */

public class CommunicationConstants {
    public static final int MIN_COMMAND_LENGHT = 9;

    public static final int COMMAND_ADVERTISING = 0xFF;
    public static final int COMMAND_PING_STIMULATOR = 0x00;
    public static final int COMMAND_PING_RESPONSE = 0x80;
    public static final int COMMAND_SEARCH_STIMULATOR = 0x04;
    public static final int COMMAND_SEARCH_RESPONSE = 0x84;
    public static final int COMMAND_GET_FIRMWARE_REVISION = 0x01;
    public static final int COMMAND_FIRMWARE_REVISION_RESPONSE = 0x81;
    public static final int COMMAND_GET_DEVICE_STATUS = 0x02;
    public static final int COMMAND_DEVICE_STATUS_RESPONSE = 0x82;
    public static final int COMMAND_SET_DEVICE_COLOR = 0x03;
    public static final int COMMAND_SET_DEVICE_COLOR_RESPONSE = 0x83;
    public static final int COMMAND_SET_COMMON_STIMULATION_PARAMETERS = 0x10;
    public static final int COMMAND_SET_COMMON_STIMULATION_PARAMETERS_RESPONSE = 0x90;
    public static final int COMMAND_SET_COMMON_STIMULATION_PARAMETERS_ON_HOT = 0x14;
    public static final int COMMAND_SET_COMMON_STIMULATION_PARAMETERS_RESPONSE_ON_HOT  = 0x94;
    public static final int COMMAND_SET_MAIN_LEVEL = 0x11;
    public static final int COMMAND_SET_MAIN_LEVEL_RESPONSE = 0x91;
    public static final int COMMAND_SET_CHANNELS_LEVELS = 0x12;
    public static final int COMMAND_SET_CHANNELS_LEVELS_RESPONSE = 0x92;
    public static final int COMMAND_START_CURRENT_CYCLE = 0x20;
    public static final int COMMAND_START_CURRENT_CYCLE_RESPONSE = 0xA0;
    public static final int COMMAND_STOP_CURRENT_CYCLE = 0x21;
    public static final int COMMAND_STOP_CURRENT_CYCLE_RESPONSE = 0xA1;
    public static final int COMMAND_PAUSE_CURRENT_CYCLE = 0x21;
    public static final int COMMAND_PAUSE_CURRENT_CYCLE_RESPONSE = 0xA1;
    public static final int COMMAND_RESET_CURRENT_CYCLE = 0x22;
    public static final int COMMAND_RESET_CURRENT_CYCLE_RESPONSE = 0xA2;
    public static final int ASYNC_PROGRAM_STATUS = 0xC0;
    public static final int COMMAND_ASYNC_SET_MAIN_LEVEL = 0xC1;
    public static final int COMMAND_ASYNC_PAUSE = 0xC2;
    public static final int COMMAND_ASYNC_START = 0xC4;



}
