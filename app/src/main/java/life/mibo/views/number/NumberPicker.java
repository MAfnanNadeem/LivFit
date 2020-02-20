/*
 *  Created by Sumeet Kumar on 2/19/20 9:36 AM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 2/19/20 9:36 AM
 *  Mibo Hexa - app
 */

package life.mibo.views.number;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import life.mibo.hardware.core.Logger;
import life.mibo.hexa.R;

public class NumberPicker extends LinearLayout {
    private TextView number;
    private int min = 0, max = 100, diff = 1;

    public NumberPicker(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        inflate(context, R.layout.number_picker, this);

        number = findViewById(R.id.et_number);

        final View btn_less = findViewById(R.id.btn_less);
        btn_less.setOnClickListener(new Listener(-diff));

        final View btn_more = findViewById(R.id.btn_more);
        btn_more.setOnClickListener(new Listener(diff));
    }

    private class Listener implements OnClickListener {
        final int diff;

        public Listener(int diff) {
            this.diff = diff;
        }

        @Override
        public void onClick(View v) {
            int newValue = getValue() + diff;
            if (newValue < min) {
                newValue = min;
            } else if (newValue > max) {
                newValue = max;
            }
            number.setText(String.valueOf(newValue));
        }
    }


    public String getText() {
        return "" + getValue();
    }

    public int getValue() {
        if (number != null) {
            try {
                final String value = number.getText().toString();
                return Integer.parseInt(value);
            } catch (Exception ex) {
                Logger.e("HorizontalNumberPicker", ex.toString());
            }
        }
        return 0;
    }

    public void setValue(final int value) {
        if (number != null) {
            number.setText(String.valueOf(value));
        }
    }

    public int getMin() {
        return min;
    }

    public void addDiff(int diff) {
        this.diff = diff;
    }

    public void setMin(int min) {
        this.min = min;
    }

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
    }
}