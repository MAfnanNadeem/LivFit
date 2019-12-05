package life.mibo.hardware.events;

/**
 * Created by Fer on 18/04/2019.
 */

public class HrEvent {
        private int hr;
        private String uid;

        public HrEvent(int hr, String uid) {
            this.hr = hr;
            this.uid = uid;
        }

        public int getHr() {
            return hr;
        }

        public String getUid() {
            return uid;
        }


}
