package com.theflopguyproductions.ticktrack.ui.utils.numberpicker;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.EditText;
import android.widget.NumberPicker;

import com.theflopguyproductions.ticktrack.R;

public class DarkModeNumberPicker extends NumberPicker {

    Typeface type;

    public DarkModeNumberPicker(Context context) {
        this(context, null);
    }

    public DarkModeNumberPicker(Context context, AttributeSet attrs) {
        super(new ContextThemeWrapper(context, R.style.DarkNumberPicker), attrs);

    }

    @Override
    public void addView(View child) {
        super.addView(child);
        updateView(child);
    }

    @Override
    public void addView(View child, int index,
                        android.view.ViewGroup.LayoutParams params) {
        super.addView(child, index, params);
        type = Typeface.createFromAsset(getContext().getAssets(),
                "fonts/odudo_regular.otf");
        updateView(child);
    }

    @Override
    public void addView(View child, android.view.ViewGroup.LayoutParams params) {
        super.addView(child, params);

        type = Typeface.createFromAsset(getContext().getAssets(),
                "fonts/odudo_regular.otf");
        updateView(child);
    }

    private void updateView(View view) {

        if (view instanceof EditText) {
            ((EditText) view).setTypeface(type);
            ((EditText) view).setTextSize(25);
            ((EditText) view).setTextColor(getResources().getColor(
                    R.color.DarkText));
        }

    }
}
