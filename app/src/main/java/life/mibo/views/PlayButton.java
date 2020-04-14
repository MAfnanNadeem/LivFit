package life.mibo.views;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.SoundEffectConstants;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.core.content.ContextCompat;

import life.mibo.android.R;


public class PlayButton extends AppCompatImageButton {

    private Drawable playImage;
    private Drawable stopImage;
    private boolean checked = false;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public PlayButton(final Context context,
                      final AttributeSet attrs,
                      final int defStyleAttr,
                      final int defStyleRes) {
        super(context, attrs, defStyleAttr);
        init(context);

    }

    public PlayButton(final Context context,
                      final AttributeSet attrs,
                      final int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public PlayButton(final Context context,
                      final AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void removeText() {
        //this.setTextOn("");
        //this.setTextOff("");

        //setChecked(!this.isChecked());
        //super.setChecked(!this.isChecked());
    }

    private void init(final Context context) {
        playImage = ContextCompat.getDrawable(context, R.drawable.ic_play_hexa);
        stopImage = ContextCompat.getDrawable(context, R.drawable.ic_stop_hexa);
        setBackground(null);
        setScaleType(ImageView.ScaleType.FIT_CENTER);
        removeText();
        updateDrawables();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        updateDrawables();
    }

    private void updateDrawables() {
        if (checked) {
            //setImageDrawable();
            setImageDrawable(playImage);
            //setBackgroundResource(R.drawable.img_play_hexa);
            //setBackgroundDrawable(playImage);
        } else {
            //setBackgroundResource();
            setImageDrawable(stopImage);
            //setBackgroundResource(R.drawable.img_stop_hexa);
            //setBackgroundDrawable(stopImage);
        }
    }

    public void setChecked(boolean check) {
        //super.setChecked(checked);
        this.checked = check;
        updateDrawables();
    }

    public boolean isChecked() {
        return checked;
    }

    public boolean isPlay() {
        return isChecked();
    }

    public void setPlayImage(final Drawable playImage) {
        this.playImage = playImage;
        updateDrawables();
    }

    public void setStopImage(final Drawable stopImage) {
        this.stopImage = stopImage;
        updateDrawables();
    }

    @Override
    public Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();

        SavedState ss = new SavedState(superState);

        ss.checked = isChecked();
        return ss;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        SavedState ss = (SavedState) state;

        super.onRestoreInstanceState(ss.getSuperState());
        setChecked(ss.checked);
        requestLayout();
    }

    public void toggle() {
        setChecked(!checked);
    }

    @Override
    public boolean performClick() {
        toggle();

        final boolean handled = super.performClick();
        if (!handled) {
            // View only makes a sound effect if the onClickListener was
            // called, so we'll need to make one here instead.
            playSoundEffect(SoundEffectConstants.CLICK);
        }

        return handled;
    }
    @Override
    public void setOnClickListener(@Nullable OnClickListener l) {
        super.setOnClickListener(l);
    }
    static class SavedState extends View.BaseSavedState {
        boolean checked;

        SavedState(Parcelable superState) {
            super(superState);
        }

        private SavedState(Parcel in) {
            super(in);
            checked = (Boolean) in.readValue(null);
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeValue(checked);
        }

        @Override
        public String toString() {
            return "SavedState{"
                    + Integer.toHexString(System.identityHashCode(this))
                    + " checked=" + checked + "}";
        }

        @SuppressWarnings("hiding")
        public static final Parcelable.Creator<SavedState> CREATOR =
                new Parcelable.Creator<SavedState>() {
                    @Override
                    public SavedState createFromParcel(Parcel in) {
                        return new SavedState(in);
                    }

                    @Override
                    public SavedState[] newArray(int size) {
                        return new SavedState[size];
                    }
                };
    }
}