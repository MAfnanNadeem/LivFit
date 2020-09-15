package life.mibo.android.ui.rxt.model;

import java.io.Serializable;
import java.util.Objects;

import life.mibo.android.R;


public class Tile implements Serializable {
    String uid = "";
    int controllerId, tileId, position, imgRes = R.drawable.rxt_tile_2;
    boolean isSelected, isAdded, isNumber, showNumber, isEmpty = false, isGeneric = false;

    public void updateSelect() {
        isSelected = !isSelected;
    }

    void updateAdded() {
        isAdded = !isAdded;
    }

    public Tile(String uid, int tileId) {
        this.uid = uid;
        this.tileId = tileId;
    }

    public Tile(String uid, int controllerId, int tileId, Boolean selected, Boolean added) {
        //this.device = device;
        this.uid = uid;
        this.controllerId = controllerId;
        this.tileId = tileId;
        this.isSelected = selected;
        this.isAdded = added;
    }

    public Tile(String uid, int controllerId, int tileId, Boolean selected, Boolean added, int imgRes) {
        //this.device = device;
        this.uid = uid;
        this.controllerId = controllerId;
        this.tileId = tileId;
        this.isSelected = selected;
        this.isAdded = added;
        this.imgRes = imgRes;
        isGeneric = true;
    }

    public Tile(String uid, int controllerId, int tileId, boolean selected, boolean added, boolean empty) {
        //this.device = device;
        this.uid = uid;
        this.controllerId = controllerId;
        this.tileId = tileId;
        this.isSelected = selected;
        this.isAdded = added;
        this.isEmpty = empty;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tile tile = (Tile) o;
        return controllerId == tile.controllerId &&
                tileId == tile.tileId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(controllerId, tileId);
    }

    public Tile copy() {
        return new Tile(uid, controllerId, tileId, isSelected, isAdded, isEmpty);
    }

    static Tile copy(Tile tile) {
        if (tile == null)
            return null;
        return new Tile(tile.uid, tile.controllerId, tile.tileId, false, false);
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public int getControllerId() {
        return controllerId;
    }

    public void setControllerId(int controllerId) {
        this.controllerId = controllerId;
    }

    public int getTileId() {
        return tileId;
    }

    public void setTileId(int tileId) {
        this.tileId = tileId;
    }

    public int getImgRes() {
        return imgRes;
    }

    public void setImgRes(int imgRes) {
        this.imgRes = imgRes;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public boolean isShowNumber() {
        return showNumber;
    }

    public void setShowNumber(boolean showNumber) {
        this.showNumber = showNumber;
    }

    @Override
    public String toString() {
        return "Tile{" +
                "uid='" + uid + '\'' +
                ", tileId=" + tileId +
                '}';
    }
}