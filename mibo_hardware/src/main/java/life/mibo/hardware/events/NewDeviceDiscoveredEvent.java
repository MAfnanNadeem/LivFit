package life.mibo.hardware.events;

/**
 * Created by Fer on 25/03/2019.
 */

public class NewDeviceDiscoveredEvent {
        private String uid;
        private Object data;

        public NewDeviceDiscoveredEvent(String uid) {
            this.uid = uid;
        }

        public NewDeviceDiscoveredEvent(Object object) {
            this.data = object;
        }

        public String getUid() {
            return uid;
        }

    public Object getData() {
        return data;
    }
}
