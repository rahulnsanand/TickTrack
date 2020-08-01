package com.theflopguyproductions.ticktrack.ui.utils.numberpicker;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ContextThemeWrapper;
import android.widget.NumberPicker;

import com.theflopguyproductions.ticktrack.R;

public class DarkModeNumberPicker extends NumberPicker {
    public DarkModeNumberPicker(Context context) {
        this(context, null);
    }

    public DarkModeNumberPicker(Context context, AttributeSet attrs) {
        super(new ContextThemeWrapper(context, R.style.DarkNumberPicker), attrs);

    }
}
