package life.mibo.hardware.encryption;


public class MCrypt2 {


    public MCrypt2() {
    }


    public String encrypt(String text) {
        return text;
    }

    public byte[] decrypt(String code) throws Exception {
        if (code != null)
            return code.getBytes();
        return null;
    }
}