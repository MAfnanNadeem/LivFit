package life.mibo.hardware.events;

/**
 * Created by Fer on 20/03/2019.
 */

public class SendMainLevelEvent {

        private int level;
        private String uid;

        public SendMainLevelEvent(int level, String uid) {
            this.level = level;
            this.uid = uid;
        }

        public int getLevel() {
            return level;
        }

        public String getUid() {
            return uid;
        }


}
