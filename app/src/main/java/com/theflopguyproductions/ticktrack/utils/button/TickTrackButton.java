package com.theflopguyproductions.ticktrack.utils.button;

import android.content.Context;
import android.util.AttributeSet;

import com.theflopguyproductions.ticktrack.utils.font.TickTrackFontHelper;

public class TickTrackButton extends androidx.appcompat.widget.AppCompatButton {
    public TickTrackButton(Context context) {
        super(context);
    }

    public TickTrackButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        TickTrackFontHelper.setCustomFont(this, context, attrs);
    }


    public TickTrackButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        TickTrackFontHelper.setCustomFont(this, context, attrs);
    }
}
