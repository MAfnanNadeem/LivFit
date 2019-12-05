package life.mibo.hardware;

public class MIBO {

    private static MIBO instance;

    private MIBO() {

    }

    public static MIBO getInstance() {
        if (instance == null)
            instance = new MIBO();
        return instance;
    }

    public void init(){

    }
}
