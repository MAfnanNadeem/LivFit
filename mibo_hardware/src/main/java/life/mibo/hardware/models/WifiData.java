package life.mibo.hardware.models;

public class WifiData {
        CharSequence name;
        CharSequence pass;


    int state = 0;
        int signalLevel = 0;

        public String getCapabilities() {
            return capabilities;
        }

        public void setCapabilities(String capabilities) {
            this.capabilities = capabilities;
        }

        String capabilities;

        public void setName(CharSequence name) {
            this.name = name;
        }

        public CharSequence getName (){
            return name;
        }

    public CharSequence getPass() {
        return pass;
    }

    public void setPass(CharSequence pass) {
        this.pass = pass;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public int getSignalLevel() {
        return signalLevel;
    }

    public void setSignalLevel(int signalLevel) {
        this.signalLevel = signalLevel;
    }
}

