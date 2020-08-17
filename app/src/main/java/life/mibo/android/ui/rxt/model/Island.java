package life.mibo.android.ui.rxt.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import life.mibo.android.ui.rxt.parser.RxtProgram;
import life.mibo.android.ui.rxt.parser.RxtTile;

public class Island implements Serializable {
    //Device device;
    private int id = 0;
    private String name = "";
    //int controllerId, count;
    private List<Tile> tiles;
    private String key;
    private Boolean isSelected = false;
    private RxtProgram program;
    private int color = 0;

    public Island(String name, List<Tile> tiles) {
        //this.device = device;
        this.name = name;
        this.tiles = tiles;
        //isHeader = true;
    }

    public Island(String name, List<Tile> tiles, String key) {
        //this.device = device;
        this.name = name;
        this.tiles = tiles;
        this.key = key;
        //isHeader = true;
    }

    public String getKey() {
        return key;
    }

    public String getName() {
        return name;
    }

    public int getSize() {
        if (tiles == null)
            return 0;
        return tiles.size();
    }

    public int getTileCount() {
        if (tiles == null)
            return 0;
        return tiles.size();
    }

    public RxtProgram getProgram() {
        return program;
    }

    public void setProgram(RxtProgram program) {
        this.program = program;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public void updateCheck() {
        this.isSelected = !this.isSelected;
    }

    public Boolean isSelected() {
        return isSelected;
    }

    public void setSelected(Boolean selected) {
        isSelected = selected;
    }

    public List<Tile> getTiles() {
        return tiles;
    }

    public List<RxtTile> getRxtTiles() {
        List<RxtTile> list = new ArrayList<>();
        for (Tile t : tiles)
            list.add(new RxtTile(t.uid, t.tileId));
        return list;
    }

    @Override
    public String toString() {
        return "Island{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", tiles=" + tiles +
                ", key='" + key + '\'' +
                ", isSelected=" + isSelected +
                ", program=" + program +
                ", color=" + color +
                '}';
    }
}