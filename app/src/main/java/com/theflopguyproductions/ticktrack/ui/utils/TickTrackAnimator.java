package com.theflopguyproductions.ticktrack.ui.utils;

import android.graphics.drawable.Drawable;
import android.view.View;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class TickTrackAnimator {


    public static void fabDissolve(FloatingActionButton fab){
        fab.animate()
                .setDuration(50)
                .scaleX(1.2f)
                .scaleY(1.2f)
                .withEndAction(() -> fab.animate()
                        .setDuration(50)
                        .scaleX(0)
                        .scaleY(0)
                        .alpha(0)
                        .withEndAction(()->fab.setVisibility(View.GONE))
                        .start())
                .start();
    }

    public static void fabUnDissolve(FloatingActionButton fab){
        fab.setVisibility(View.VISIBLE);
        fab.animate()
                .setDuration(50)
                .scaleX(1.2f)
                .scaleY(1.2f)
                .alpha(1f)
                .withEndAction(() -> fab.animate()
                        .setDuration(50)
                        .scaleX(1)
                        .scaleY(1)
                        .start())
                .start();
    }

    public static void fabBounce(FloatingActionButton fab, Drawable drawable){
        fab.animate()
                .setDuration(50)
                .scaleX(.8f)
                .scaleY(.8f)
                .withEndAction(() -> {
                    fab.setImageDrawable(drawable);
                    fab.animate()
                            .setDuration(50)
                            .scaleX(1f)
                            .scaleY(1f)
                            .start();
                }).start();
    }
}
