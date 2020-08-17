package life.mibo.android.ui.rxt.model;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import life.mibo.android.R;
import life.mibo.android.ui.base.ItemClickListener;


public class InstallHolder extends RecyclerView.ViewHolder {
    TextView title, mac, info;
    View image;
    RecyclerView childRecycler;

    public InstallHolder(View view) {
        super(view);
        image = view.findViewById(R.id.image_tile);
        title = view.findViewById(R.id.controllerId);
        mac = view.findViewById(R.id.controllerMac);
        info = view.findViewById(R.id.controllerCount);
        childRecycler = view.findViewById(R.id.childRecyclerView);
    }

    void bind(Controller controller, ItemClickListener<Object> listener, RecyclerView.RecycledViewPool pool, int span) {
        if (controller == null)
            return;
        title.setText("" + controller.controllerId);
        mac.setText("MAC #: " + controller.uid);
        info.setText("Connected Tiles: " + controller.count);
        //GridLayoutManager manager = new GridLayoutManager(itemView.getContext(), span);
        childRecycler.setLayoutManager(new GridLayoutManager(itemView.getContext(), span));
        childRecycler.setAdapter(new ChildAdapter(controller.tiles, listener));
        childRecycler.setRecycledViewPool(pool);

        if (image != null)
            image.setOnClickListener(v -> {
                if (listener != null)
                    listener.onItemClicked(controller, getAdapterPosition());
            });

//            childRecycler.getViewTreeObserver().addOnGlobalLayoutListener(
//                    new ViewTreeObserver.OnGlobalLayoutListener() {
//                        @Override
//                        public void onGlobalLayout() {
//                            childRecycler.getViewTreeObserver().removeOnGlobalLayoutListener(this);
//                            int viewWidth = childRecycler.getMeasuredWidth();
//                            float cardViewWidth = childRecycler.getResources().getDimension(R.dimen.rxt_child_item);
//                            int newSpanCount = (int) Math.floor(viewWidth / cardViewWidth);
//                            manager.setSpanCount(newSpanCount);
//                            manager.requestLayout();
//                        }
//                    });
    }
}