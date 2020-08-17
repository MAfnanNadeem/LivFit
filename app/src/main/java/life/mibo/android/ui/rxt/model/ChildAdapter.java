package life.mibo.android.ui.rxt.model;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import life.mibo.android.R;
import life.mibo.android.ui.base.ItemClickListener;
import life.mibo.hardware.core.Logger;


public class ChildAdapter extends RecyclerView.Adapter<ChildHolder> {

    List<Tile> tiles = new ArrayList<>();
    ItemClickListener<Object> listener;

    public ChildAdapter(List<Tile> list, ItemClickListener<Object> listener) {
        tiles.addAll(list);
        this.listener = listener;
    }

    @NonNull
    @Override
    public ChildHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ChildHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_rxt_install_child, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ChildHolder holder, int position) {
        holder.bind(tiles.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return tiles.size();
    }

    public void add(Tile tile) {
        tiles.add(tile);
        notifyItemInserted(tiles.size());
    }

    public boolean update(Tile tile, int position) {
        Logger.e("update " + position + " -- " + tile);
        int count = 0;
        boolean added = false;
        if (tile.isSelected) {
            for (Tile t : tiles) {
                if (t.isEmpty) {
                    Logger.e("update add controllerId matched " + t + " -- " + tile);
                    t.uid = tile.uid;
                    t.tileId = tile.tileId;
                    t.isEmpty = false;
                    t.showNumber = false;
                    t.imgRes = R.drawable.rxt_tile_1;
                    t.isSelected = tile.isSelected;
                    added = true;
                    break;
                }
                count++;
            }
        } else {
            for (Tile t : tiles) {
                if (t.tileId == tile.tileId) {
                    Logger.e("update remove controllerId matched " + t + " -- " + tile);
                    t.tileId = 0;
                    t.isEmpty = true;
                    t.isSelected = tile.isSelected;
                    break;
                }
                count++;
            }
        }

        notifyItemChanged(count);
        return added;
    }

    public void remove(Tile tile) {
        if (tiles.isEmpty())
            return;
        int pos = -1;
        pos = tiles.indexOf(tile);
        if (pos >= 0) {
            tiles.remove(pos);
            notifyItemRemoved(pos);
        }
    }

    public List<Tile> getSelected() {
        return tiles;
    }

    public List<Tile> getAddedTiles() {
        List<Tile> list = new ArrayList<>(tiles);
        List<Tile> empty = new ArrayList<>();
        for (Tile t : list) {
            if (!t.isEmpty && t.isSelected)
                empty.add(t);
        }
        return empty;
    }

    public void clearSelected() {
        try {
            for (Tile t : tiles) {
                t.setSelected(false);
            }
            notifyDataSetChanged();
        } catch (Exception ee) {

        }
    }

    // avoid ConcurrentModificationException
    public List<Tile> getSelectedCopy() {
        return new ArrayList<>(tiles);
    }
}