package life.mibo.hardware.events;


import life.mibo.hardware.models.program.Circuit;

/**
 * Created by Fer on 23/04/2019.
 */

public class SendCircuitEvent {

    private Circuit circuit;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    private String uid;

    public SendCircuitEvent(Circuit circuit, String uid) {
        this.circuit = circuit;
        this.uid = uid;
    }

    public Circuit getCircuit() {
        return circuit;
    }
}
