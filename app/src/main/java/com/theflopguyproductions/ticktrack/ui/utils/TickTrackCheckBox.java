package com.theflopguyproductions.ticktrack.ui.utils;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatCheckBox;

public class TickTrackCheckBox extends AppCompatCheckBox {


    public TickTrackCheckBox(Context context) {
        super(context);
        init();
    }

    public TickTrackCheckBox(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TickTrackCheckBox(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }
    private void init(){
        Typeface font = Typeface.createFromAsset(getContext().getAssets(), "fonts/apercu_regular.otf");
        this.setTypeface(font);
    }

    @Override
    public void setTypeface(Typeface tf, int style) {
        tf = Typeface.createFromAsset(getContext().getAssets(), "fonts/apercu_regular.otf");
        super.setTypeface(tf, style);
    }

    @Override
    public void setTypeface(Typeface tf) {
        tf = Typeface.createFromAsset(getContext().getAssets(), "fonts/apercu_regular.otf");
        super.setTypeface(tf);
    }
}