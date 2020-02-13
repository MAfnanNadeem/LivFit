package life.mibo.hardware.bluetooth.operations;

import java.util.ArrayList;


public class GattOperationBundle {

    final ArrayList<GattOperation> operations;

    public GattOperationBundle() {
        operations = new ArrayList<>();
    }

    public void addOperation(GattOperation operation) {
        operations.add(operation);
        operation.setBundle(this);
    }

    public ArrayList<GattOperation> getOperations() {
        return operations;
    }

    @Override
    public String toString() {
        return "GattOperationBundle{" +
                "operations=" + operations +
                '}';
    }
}
