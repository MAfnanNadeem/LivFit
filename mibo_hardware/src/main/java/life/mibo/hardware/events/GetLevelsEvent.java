package life.mibo.hardware.events;

/**
 * Created by Fer on 26/03/2019.
 */

public class GetLevelsEvent {

        private String uid;

        public GetLevelsEvent(String uid) {
            this.uid = uid;
        }

        public String getUid() {
            return uid;
        }


}
