package com.theflopguyproductions.ticktrack.ui.utils.numberpicker;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ContextThemeWrapper;
import android.widget.NumberPicker;

import com.theflopguyproductions.ticktrack.R;

public class LightModeNumberPicker extends NumberPicker {
    public LightModeNumberPicker(Context context) {
        this(context, null);
    }

    public LightModeNumberPicker(Context context, AttributeSet attrs) {
        super(new ContextThemeWrapper(context, R.style.LightNumberPicker), attrs);

    }
}
