package life.mibo.hardware.encryption;


import android.util.Log;

import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import life.mibo.hardware.core.Logger;

public class MCrypt {

    static char[] HEX_CHARS = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};


    private IvParameterSpec ivspec;
    private SecretKeySpec keyspec;
    private Cipher cipher;


    public MCrypt() {
        String iv = "fdsfds85435nfdfs";
        //String SecretKey = "89432hjfsd891787";
        String SecretKey = "UkXn2r5u8x/A?D(G+KbPeShVmYq3s6v9";

        ivspec = new IvParameterSpec(iv.getBytes());

        keyspec = new SecretKeySpec(SecretKey.getBytes(), "AES_256");

        try {
            cipher = Cipher.getInstance("AES_256/CBC/PKCS5Padding");
            Logger.e("cipher init "+cipher);
            Logger.e("cipher init "+cipher.getAlgorithm());
            Logger.e("cipher init "+cipher.getBlockSize());
            Logger.e("cipher getProvider "+cipher.getProvider());
            Logger.e("cipher init "+cipher.getProvider().getInfo());
            Logger.e("cipher keyspec "+keyspec.getAlgorithm());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public byte[] encrypt2(String text) throws Exception {
        if (text == null || text.length() == 0)
            throw new Exception("Empty string");

        byte[] encrypted = null;

        try {
            cipher.init(Cipher.ENCRYPT_MODE, keyspec, ivspec);

            encrypted = cipher.doFinal(padString(text).getBytes());
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("[encrypt] " + e.getMessage());
        }

        return encrypted;
    }

    public String encrypt(String text) throws Exception {
        if (text == null || text.length() == 0)
            throw new Exception("Empty string");

        String encrypted = null;

        try {
            cipher.init(Cipher.ENCRYPT_MODE, keyspec, ivspec);
            //byte[] enc = cipher.doFinal(padString(text).getBytes());
            byte[] enc = cipher.doFinal(text.getBytes());
            encrypted = bytesToHex(enc);
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("[encrypt] " + e.getMessage());
        }

        return encrypted;
    }

    public byte[] decrypt(String code) throws Exception {
        if (code == null || code.length() == 0)
            return new byte[]{};

        byte[] decrypted = null;

        try {
            //cipher.init(Cipher.DECRYPT_MODE, keyspec, ivspec);
            cipher.init(Cipher.DECRYPT_MODE, keyspec, ivspec);
            //log("decrypt "+code);

            //decrypted = cipher.doFinal(hexToBytes(code));
            decrypted = cipher.doFinal(parseHexBinary(code));
            // decrypted = cipher.doFinal(code.getBytes(StandardCharsets.UTF_8));

            //Remove trailing zeroes
            if (decrypted.length > 0) {
                int trim = 0;
                for (int i = decrypted.length - 1; i >= 0; i--) if (decrypted[i] == 0) trim++;

                if (trim > 0) {
                    byte[] newArray = new byte[decrypted.length - trim];
                    System.arraycopy(decrypted, 0, newArray, 0, decrypted.length - trim);
                    decrypted = newArray;
                }
            }

            //log("decrypted "+new String(decrypted));
        } catch (Exception e) {
            e.printStackTrace();
            return new byte[]{};
            //throw new Exception("[decrypt] " + e.getMessage());
        }
        return decrypted;
    }


    public byte[] decrypt2(String code) throws Exception {
        if (code == null || code.length() == 0)
            throw new Exception("Empty string");

        byte[] decrypted = null;

        try {
            cipher.init(Cipher.DECRYPT_MODE, keyspec, ivspec);

            decrypted = cipher.doFinal(hexToBytes(code));
            //Remove trailing zeroes
            if (decrypted.length > 0) {
                int trim = 0;
                for (int i = decrypted.length - 1; i >= 0; i--) if (decrypted[i] == 0) trim++;

                if (trim > 0) {
                    byte[] newArray = new byte[decrypted.length - trim];
                    System.arraycopy(decrypted, 0, newArray, 0, decrypted.length - trim);
                    decrypted = newArray;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("[decrypt] " + e.getMessage());
        }
        return decrypted;
    }


    public static String bytesToHex(byte[] buf) {
        char[] chars = new char[2 * buf.length];
        for (int i = 0; i < buf.length; ++i) {
            chars[2 * i] = HEX_CHARS[(buf[i] & 0xF0) >>> 4];
            chars[2 * i + 1] = HEX_CHARS[buf[i] & 0x0F];
        }
        return new String(chars);
    }


    public static byte[] hexToBytes(String str) {
        if (str == null) {
            return null;
        } else if (str.length() < 2) {
            return null;
        } else {
            int len = str.length() / 2;
            byte[] buffer = new byte[len];
            for (int i = 0; i < len; i++) {
                buffer[i] = (byte) Integer.parseInt(str.substring(i * 2, i * 2 + 2), 16);
            }
            return buffer;
        }
    }


    private static String padString(String source) {
        char paddingChar = 0;
        int size = 16;
        int x = source.length() % size;
        int padLength = size - x;

        StringBuilder sourceBuilder = new StringBuilder(source);
        for (int i = 0; i < padLength; i++) {
            sourceBuilder.append(paddingChar);
        }
        source = sourceBuilder.toString();

        return source;
    }

    private static int hexToBin(char ch) {
        if ('0' <= ch && ch <= '9') {
            return ch - '0';
        }
        if ('A' <= ch && ch <= 'F') {
            return ch - 'A' + 10;
        }
        if ('a' <= ch && ch <= 'f') {
            return ch - 'a' + 10;
        }
        return -1;
    }

    private static final char[] hexCode = "0123456789ABCDEF".toCharArray();

    public static byte[] parseHexBinary(String s) {
        final int len = s.length();

        // "111" is not a valid hex encoding.
        if (len % 2 != 0) {
            throw new IllegalArgumentException("hexBinary needs to be even-length: " + s);
        }

        byte[] out = new byte[len / 2];

        for (int i = 0; i < len; i += 2) {
            int h = hexToBin(s.charAt(i));
            int l = hexToBin(s.charAt(i + 1));
            if (h == -1 || l == -1) {
                throw new IllegalArgumentException("contains illegal character for hexBinary: " + s);
            }

            out[i / 2] = (byte) (h * 16 + l);
        }

        return out;
    }
}