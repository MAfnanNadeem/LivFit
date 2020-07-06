/*
 *  Created by Sumeet Kumar on 7/5/20 11:04 AM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 7/5/20 11:04 AM
 *  Mibo Hexa - app
 */

package life.mibo.android.ui.fit;

import android.content.Context;
import android.widget.TextView;

import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.MPPointF;

import java.text.DecimalFormat;

import life.mibo.android.R;

public class MyMarkerView extends MarkerView {

    private final TextView tvContent;
    //private final ValueFormatter xAxisValueFormatter;

   // private final DecimalFormat format;
    private String hint = "";

    public MyMarkerView(Context context, ValueFormatter xAxisValueFormatter, String text) {
        super(context, R.layout.item_bar_chart_marker);

        //this.xAxisValueFormatter = xAxisValueFormatter;
        tvContent = findViewById(R.id.tvContent);
       // format = new DecimalFormat("###.0");
        hint = text;
    }

    // runs every time the MarkerView is redrawn, can be used to update the
    // content (user-interface)
    @Override
    public void refreshContent(Entry e, Highlight highlight) {
        //tvContent.setText(String.format("x: %s, y: %s", xAxisValueFormatter.getFormattedValue(e.getX()), format.format(e.getY())));
        tvContent.setText(String.format("%s %s", (int) e.getY(), hint));
        super.refreshContent(e, highlight);
    }

    @Override
    public MPPointF getOffset() {
        return new MPPointF(-(getWidth() / 2), -getHeight());
    }
}