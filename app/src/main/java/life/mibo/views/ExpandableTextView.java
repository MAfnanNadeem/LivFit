/*
 *  Created by Sumeet Kumar on 1/26/20 5:01 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 1/26/20 5:01 PM
 *  Mibo Hexa - app
 */

package life.mibo.views;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.view.ViewTreeObserver;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatTextView;

import life.mibo.hexa.utils.Constants;

public class ExpandableTextView extends AppCompatTextView {

    private final String readMoreText = "...read more";
    private final int readMoreColor = Constants.PRIMARY;

    private int _maxLines = 4;
    private CharSequence originalText;

    public ExpandableTextView(Context context) {
        super(context);
        init(context);
    }

    public ExpandableTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ExpandableTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {

        ViewTreeObserver vto = getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @SuppressWarnings("deprecation")
            @Override
            public void onGlobalLayout() {

                ViewTreeObserver obs = getViewTreeObserver();
                obs.removeGlobalOnLayoutListener(this);

                truncateText();
            }
        });
    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        super.setText(text, type);

        if (originalText == null) {
            originalText = text;
        }
    }

    @Override
    public int getMaxLines() {
        return _maxLines;
    }

    @Override
    public void setMaxLines(int maxLines) {
        _maxLines = maxLines;
    }

    public void truncateText() {

        int maxLines = _maxLines;
        //String text = getText().toString();

        if (getLineCount() >= maxLines) {

            int lineEndIndex = getLayout().getLineEnd(maxLines - 1);

            String truncatedText = getText().subSequence(0, lineEndIndex - readMoreText.length() + 1) + readMoreText;

            Spannable spannable = new SpannableString(truncatedText);
            spannable.setSpan(new ForegroundColorSpan(readMoreColor), truncatedText.length() - readMoreText.length(), truncatedText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            setText(spannable, TextView.BufferType.SPANNABLE);

            super.setMaxLines(_maxLines);
        }
    }

    public void expandText() {
        setText(originalText);
        super.setMaxLines(1000);
    }

    public void reset() {
        originalText = null;
    }
}