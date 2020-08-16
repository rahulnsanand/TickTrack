package com.theflopguyproductions.ticktrack.utils.radiobutton;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;

import com.theflopguyproductions.ticktrack.R;
import com.theflopguyproductions.ticktrack.utils.font.TickTrackFontHelper;

public class TickTrackRadioButton extends androidx.appcompat.widget.AppCompatRadioButton {
    public TickTrackRadioButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        TickTrackFontHelper.setCustomFont(this, context, attrs);
    }

    public TickTrackRadioButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        TickTrackFontHelper.setCustomFont(this, context, attrs);
    }



    private void init(AttributeSet attrs) {
        if (attrs != null) {
            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.CustomFont);
            String fontName = a.getString(R.styleable.CustomFont_fontName);

            if (fontName == null || fontName.isEmpty()) {
                fontName = TickTrackFontCacheHelper.DEFAULT_FONT_NAME;
            }
            Typeface myTypeface = TickTrackFontCacheHelper.getInstance().getAppFont(getContext(), fontName);
            setTypeface(myTypeface);
            a.recycle();
        }
    }
}
