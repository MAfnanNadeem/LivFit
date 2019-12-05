package life.mibo.hardware.events;

/**
 * Created by Fer on 25/03/2019.
 */

public class NewDeviceDiscoveredEvent {
        private String uid;

        public NewDeviceDiscoveredEvent(String uid) {
            this.uid = uid;
        }

        public String getUid() {
            return uid;
        }


}
