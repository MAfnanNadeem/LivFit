package life.mibo.hardware.events;

public class DeviceSearchEvent {


        public String getUid() {
            return uid;
        }

        public void setUid(String uid) {
            this.uid = uid;
        }

        private String uid;

        public DeviceSearchEvent(String uid) {
            this.uid = uid;
        }

    }
