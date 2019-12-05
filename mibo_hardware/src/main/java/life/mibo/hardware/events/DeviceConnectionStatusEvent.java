package life.mibo.hardware.events;

/**
 * Created by Fer on 25/04/2019.
 */


    public class DeviceConnectionStatusEvent {
        private String uid;

        private int status;
        public DeviceConnectionStatusEvent(String uid, int status) {
            this.uid = uid;
            this.status = status;
        }

        public String getUid() {
            return uid;
        }

        public int getStatus() {
        return status;
    }


    }


