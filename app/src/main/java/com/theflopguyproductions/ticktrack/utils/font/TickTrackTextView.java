package com.theflopguyproductions.ticktrack.utils.font;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.SparseArray;

import com.theflopguyproductions.ticktrack.R;


public class TickTrackTextView extends androidx.appcompat.widget.AppCompatTextView {

    private final static int SOURCECODE_SEMI_BOLD_ITALIC = 0;
    private final static int SOURCECODE_LIGHT_ITALIC = 1;
    private final static int APERCU_REGULAR = 2;
    private final static int APERCU_ITALIC = 3;
    private final static int APERCU_LIGHT_ITALIC = 4;
    private final static int APERCU_MEDIUM_ITALIC = 5;
    private final static int APERCU_BOLD = 6;
    private final static int ODUDO_REGULAR = 7;

    private final static SparseArray<Typeface> mTypefaces = new SparseArray<Typeface>(16);

    public TickTrackTextView(Context context) {
        super(context);
    }

    public TickTrackTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        parseAttributes(context, attrs);
    }


    public TickTrackTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        parseAttributes(context, attrs);
    }

    private void parseAttributes(Context context, AttributeSet attrs) {
        TypedArray values = context.obtainStyledAttributes(attrs, R.styleable.TickTrackTextView);

        int typefaceValue = values.getInt(R.styleable.TickTrackTextView_typeface, 0);
        values.recycle();

        setTypeface(obtaintTypeface(context, typefaceValue));
    }

    private Typeface obtaintTypeface(Context context, int typefaceValue) throws IllegalArgumentException {
        Typeface typeface = mTypefaces.get(typefaceValue);
        if (typeface == null) {
            typeface = createTypeface(context, typefaceValue);
            mTypefaces.put(typefaceValue, typeface);
        }
        return typeface;
    }

    private Typeface createTypeface(Context context, int typefaceValue) throws IllegalArgumentException {
        Typeface typeface;
        switch (typefaceValue) {


            case SOURCECODE_SEMI_BOLD_ITALIC:
                typeface = Typeface.createFromAsset(context.getAssets(), "fonts/source_code_pro_semi_bold_italic.ttf");
                break;
            case SOURCECODE_LIGHT_ITALIC:
                typeface = Typeface.createFromAsset(context.getAssets(), "fonts/source_code_pro_light_italic.ttf");
                break;
            case APERCU_REGULAR:
                typeface = Typeface.createFromAsset(context.getAssets(), "fonts/apercu_regular.otf");
                break;
            case APERCU_ITALIC:
                typeface = Typeface.createFromAsset(context.getAssets(), "fonts/apercu_italic.otf");
                break;
            case APERCU_LIGHT_ITALIC:
                typeface = Typeface.createFromAsset(context.getAssets(), "fonts/apercu_light_italic.otf");
                break;
            case APERCU_MEDIUM_ITALIC:
                typeface = Typeface.createFromAsset(context.getAssets(), "fonts/apercu_medium_italic.otf");
                break;
            case APERCU_BOLD:
                typeface = Typeface.createFromAsset(context.getAssets(), "fonts/apercu_bold.otf");
                break;
            case ODUDO_REGULAR:
                typeface = Typeface.createFromAsset(context.getAssets(), "fonts/odudo_regular.otf");
                break;
            default:
                throw new IllegalArgumentException("Unknown `typeface` attribute value " + typefaceValue);
        }
        return typeface;
    }

}