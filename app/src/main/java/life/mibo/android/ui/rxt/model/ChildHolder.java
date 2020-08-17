package life.mibo.android.ui.rxt.model;

import android.graphics.Color;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import life.mibo.android.R;
import life.mibo.android.ui.base.ItemClickListener;


public class ChildHolder extends RecyclerView.ViewHolder {

    TextView number;
    View selected, emptyImage;
    ImageView tileBg, checked;

    public ChildHolder(View view) {
        super(view);
        number = view.findViewById(R.id.tileId);
        checked = view.findViewById(R.id.image_check);
        selected = view.findViewById(R.id.image_selected);
        tileBg = view.findViewById(R.id.image_tile);
        emptyImage = view.findViewById(R.id.image_tile_empty);
    }

    void bind(Tile tile, ItemClickListener<Object> listener) {
        if (tile == null)
            return;


        if (tile.isNumber) {
            number.setVisibility(View.VISIBLE);
            tileBg.setVisibility(View.GONE);
            number.setText("" + tile.tileId);
        } else {
            if (tile.showNumber) {
                number.setVisibility(View.VISIBLE);
                number.setText("" + tile.tileId);
            } else number.setVisibility(View.GONE);
            //tileBg.setVisibility(View.VISIBLE);
            if (tile.isEmpty) {
                emptyImage.setVisibility(View.VISIBLE);
                tileBg.setVisibility(View.GONE);
            } else {
                tileBg.setVisibility(View.VISIBLE);
                emptyImage.setVisibility(View.GONE);
            }
            if (tile.imgRes != 0)
                tileBg.setImageResource(tile.imgRes);
        }

        if (tile.isSelected) {
            checked.setVisibility(View.VISIBLE);
            if (tile.isGeneric)
                checked.setColorFilter(Color.DKGRAY);
        } else {
            checked.setVisibility(View.GONE);
        }

        if (tile.isAdded) {
            selected.setVisibility(View.VISIBLE);
        } else {
            selected.setVisibility(View.GONE);
        }

        itemView.setOnClickListener(v -> {
            if (listener != null)
                listener.onItemClicked(tile, getAdapterPosition());
        });

    }
}