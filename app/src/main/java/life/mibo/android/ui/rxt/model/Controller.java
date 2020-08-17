package life.mibo.android.ui.rxt.model;

import java.io.Serializable;
import java.util.List;

public class Controller implements Serializable {
    //Device device;
    String uid = "";
    int controllerId, count;
    //boolean isSelected, isAdded, isHeader, isNumber;
    List<Tile> tiles;

    public Controller(String uid, int controllerId, int tileCount, List<Tile> tiles) {
        //this.device = device;
        this.uid = uid;
        this.controllerId = controllerId;
        this.count = tileCount;
        this.tiles = tiles;
        //isHeader = true;
    }

    public String getUid() {
        return uid;
    }

    public int getCount() {
        return count;
    }

    public int getControllerId() {
        return controllerId;
    }

    public List<Tile> getTiles() {
        return tiles;
    }

    @Override
    public String toString() {
        return "Controller{" +
                "uid='" + uid + '\'' +
                ", controllerId=" + controllerId +
                ", count=" + count +
                '}';
    }
}