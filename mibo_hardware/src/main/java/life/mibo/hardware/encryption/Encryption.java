package life.mibo.hardware.encryption;

public class Encryption {
    public static void mbp_encrypt(byte[] buffer, int length)
    {
        int temp1;
        int temp2;
        for(int i = 0; i < length; i++)
        {
            buffer[i] = (byte)(buffer[i] ^ (byte)0x35);
            temp1 = (buffer[i]& 0xFF) << 3;
            temp2 = (buffer[i]& 0xFF) >>> 5;
            buffer[i] = (byte)(temp1 + temp2);
            buffer[i] = (byte)(buffer[i] ^ (byte)0x76);
        }
    }

    public static void mbp_decrypt(byte[] buffer, int length) {
        int temp1;
        int temp2;
        for (int i = 0; i < length; i++) {
            buffer[i] = (byte) (buffer[i] ^ (byte)0x76);
            temp1 = (buffer[i]& 0xFF) >>> 3;
            temp2 = (buffer[i]& 0xFF) << 5;
            buffer[i] = (byte) (temp1 + temp2);
            buffer[i] = (byte) (buffer[i] ^ (byte)0x35);
        }
    }

    public static byte[] encrypt(byte[] buffer, int length) {
        int temp1;
        int temp2;
        for (int i = 0; i < length; i++) {
            buffer[i] = (byte) (buffer[i] ^ (byte) 0x35);
            temp1 = (buffer[i] & 0xFF) << 3;
            temp2 = (buffer[i] & 0xFF) >>> 5;
            buffer[i] = (byte) (temp1 + temp2);
            buffer[i] = (byte) (buffer[i] ^ (byte) 0x76);
        }
        return buffer;
    }

    public static byte[] decrypt(byte[] buffer, int length) {
        int temp1;
        int temp2;
        for (int i = 0; i < length; i++) {
            buffer[i] = (byte) (buffer[i] ^ (byte) 0x76);
            temp1 = (buffer[i] & 0xFF) >>> 3;
            temp2 = (buffer[i] & 0xFF) << 5;
            buffer[i] = (byte) (temp1 + temp2);
            buffer[i] = (byte) (buffer[i] ^ (byte) 0x35);
        }
        return buffer;
    }

}
