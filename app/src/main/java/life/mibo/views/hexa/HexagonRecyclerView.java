/*
 *  Created by Sumeet Kumar on 3/11/20 11:28 AM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 1/6/20 5:15 PM
 *  Mibo Hexa - app
 */

package life.mibo.views.hexa;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.ViewTreeObserver;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import life.mibo.hardware.core.Logger;
import life.mibo.android.R;
import life.mibo.views.recycler.HorizontalOverlapDecorator;
import life.mibo.views.recycler.VerticalOverlapDecorator;


/**
 * Created by mindvalley on 02/06/2016.
 */

public class HexagonRecyclerView extends RecyclerView {

    public HexagonRecyclerView(Context context) {
        super(context);
        configure(null);
    }

    public HexagonRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        configure(attrs);
    }

    public HexagonRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        configure(attrs);
    }

    private void configure(@Nullable AttributeSet attrs) {
        //Deflate the custom XML attributes
        TypedArray typedArray = getContext().getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.HexagonRecyclerView,
                0, 0);

        float horizontal_spacing = typedArray.getDimension(R.styleable.HexagonRecyclerView_items_horizontal_spacing, 0.0f);
        float vertical_spacing = typedArray.getDimension(R.styleable.HexagonRecyclerView_items_vertical_spacing, 0.0f);
        final int row_size = typedArray.getInt(R.styleable.HexagonRecyclerView_items_count_in_row, 3);
        final int orientation = typedArray.getInt(R.styleable.HexagonRecyclerView_orientation, 0);
        typedArray.recycle();


        if (row_size < 2)
            throw new RuntimeException("Hexagon RecyclerView row_size can't be smaller than 2");
        //the number of items in two consequetive rows
        final int itemsInTwoRows = row_size * 2 - 1;
        //There are two types of rows: big row and small row. Small row has one item less than the big row.
        final int itemsCountInSmallRow = row_size - 1;

        GridLayoutManager mLayoutManager = new GridLayoutManager(getContext(), row_size * itemsCountInSmallRow);
        mLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                int size = itemsCountInSmallRow;
                if ((position % itemsInTwoRows >= 0) && (position % itemsInTwoRows <= itemsCountInSmallRow - 1))
                    size = row_size;
                log("GridLayoutManager getSpanSize " + position + " : " + size);
                return size;
            }
        });

        setLayoutManager(mLayoutManager);
        this.setClipToPadding(false);
        if (orientation == 0) {
            mLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
            addItemDecoration(new HorizontalOverlapDecorator(row_size, horizontal_spacing, vertical_spacing));
        } else {
            mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            addItemDecoration(new VerticalOverlapDecorator(row_size, horizontal_spacing, vertical_spacing));
        }


        //Adjust the padding at the end of the recycler view
        ViewTreeObserver vto = this.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {
                int smallRow_padding_top_bottom = getHeight() / (row_size * 2);
                //shift the items so the interpolate
                int item_row_shift = (int) (smallRow_padding_top_bottom / Math.sqrt(3));
                if (orientation == 0)
                    setPadding(0, 0, item_row_shift, 0);
                else
                    setPadding(0, 0, 0, item_row_shift);
                ViewTreeObserver obs = HexagonRecyclerView.this.getViewTreeObserver();
                obs.removeOnGlobalLayoutListener(this);
                invalidate();
            }

        });
    }

    @Override
    protected void onMeasure(int widthSpec, int heightSpec) {
        super.onMeasure(widthSpec, heightSpec);
        log("onMeasure " + widthSpec + " " + heightSpec);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        log("onLayout " + changed + " " + l);
    }

    @Override
    public void onDraw(Canvas c) {
        super.onDraw(c);
        log("ondraw " + c);
    }

    private void log(String msg) {
        Logger.e("HexagonRecyclerView : " + msg);
    }
}
