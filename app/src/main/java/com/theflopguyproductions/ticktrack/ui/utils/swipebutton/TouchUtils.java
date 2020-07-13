package com.theflopguyproductions.ticktrack.ui.utils.swipebutton;

import android.view.MotionEvent;
import android.view.View;

final class TouchUtils {
    private TouchUtils() {
    }

    static boolean isTouchOutsideInitialPosition(MotionEvent event, View view) {
        return event.getX() > view.getX() + view.getWidth();
    }
}
