package life.mibo.android.ui.rxt.model;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import life.mibo.android.R;
import life.mibo.android.ui.base.ItemClickListener;


public class InstallAdapter extends RecyclerView.Adapter<InstallHolder> {

        public static final int TILE = 1001;

        List<Controller> devices = new ArrayList<>();
        ItemClickListener<Object> listener;
        RecyclerView.RecycledViewPool pool = new RecyclerView.RecycledViewPool();
        int spanCount = 5;

        public InstallAdapter(List<Controller> list, ItemClickListener<Object> listener) {
            devices.addAll(list);
            this.listener = listener;
        }

        public void setSpanCount(int spanCount) {
            this.spanCount = spanCount;
        }

        @NonNull
        @Override
        public InstallHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new InstallHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_rxt_install, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull InstallHolder holder, int position) {
            holder.bind(devices.get(position), listener, pool, spanCount);
        }

        @Override
        public int getItemCount() {
            return devices.size();
        }

        public void update(Tile tile) {
            if (tile != null) {
                int pos = -1;
                for (Controller controller : devices) {
                    if (controller.controllerId == tile.controllerId) {
                        for (Tile t : controller.tiles) {
                            if (t.tileId == tile.tileId) {
                                pos = t.controllerId - 1;
                                break;
                            }
                        }
                    }
                }
                if (pos >= 0) {
                    notifyItemChanged(pos);
                }
            }
        }

    public void clearSelected() {
        try {
            for (Controller c : devices) {
                List<Tile> list = c.tiles;
                for (Tile t : list) {
                    t.setSelected(false);
                }
            }
            notifyDataSetChanged();
        } catch (Exception ee) {

        }
    }
}