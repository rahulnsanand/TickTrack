package com.theflopguyproductions.ticktrack.ui.utils.radiobutton;

import android.content.Context;
import android.graphics.Typeface;

import androidx.annotation.NonNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class TickTrackFontCacheHelper {
    private static final String FEATURE_FONT_PATH = "Fonts/";
    private static final String APP_FONT_PATH = "Fonts/App/";
    public static final String DEFAULT_FONT_NAME = "apercu_regular.otf";
    private static TickTrackFontCacheHelper fontCacheHelper;
    private Map<String, Typeface> fontMap = new HashMap<>();

    public static TickTrackFontCacheHelper getInstance() {
        if (fontCacheHelper == null) {
            fontCacheHelper = new TickTrackFontCacheHelper();
        }
        return fontCacheHelper;
    }

    public
    @NonNull
    Typeface getAppFont(@NonNull Context context, @NonNull String fontName) {
        String fontPath = getAppFontFilePath(fontName);
        return getFontByPath(context, fontPath);
    }

    public
    @NonNull
    Typeface getFeatureFont(@NonNull Context context, @NonNull String fontName) {
        String fontPath = getFeatureFontFilePath(fontName);
        return getFontByPath(context, fontPath);
    }

    public
    @NonNull
    Typeface getFontByPath(@NonNull Context context, @NonNull String fontPath) {
        if (fontMap.containsKey(fontPath)) {
            return Objects.requireNonNull(fontMap.get(fontPath));
        } else {
            Typeface typeface;
            if (fontPath.isEmpty()) {
                typeface = Typeface.DEFAULT;
            } else {
                typeface = Typeface.createFromAsset(context.getAssets(), fontPath);
                if (typeface == null) {
                    typeface = Typeface.DEFAULT;
                }
            }
            fontMap.put(fontPath, typeface);
            return typeface;
        }
    }

    public
    @NonNull
    String getDefaultFontFilePath() {
        return APP_FONT_PATH + DEFAULT_FONT_NAME;
    }

    private
    @NonNull
    String getAppFontFilePath(@NonNull String fontName) {
        return APP_FONT_PATH + fontName;
    }

    private
    @NonNull
    String getFeatureFontFilePath(@NonNull String fontName) {
        return FEATURE_FONT_PATH + fontName;
    }

    public
    @NonNull
    String getFontName(@NonNull Typeface typeface) {
        for (Map.Entry<String, Typeface> entry : fontMap.entrySet()) {
            if (entry.getValue().equals(typeface)) {
                return entry.getKey();
            }
        }
        return "";
    }
}
