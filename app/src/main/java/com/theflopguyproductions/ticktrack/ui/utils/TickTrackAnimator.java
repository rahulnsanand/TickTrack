package com.theflopguyproductions.ticktrack.ui.utils;

import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class TickTrackAnimator {

    public static void fabImageDissolve(ImageView imageView){
        imageView.animate()
                .setDuration(100)
                .scaleX(0f)
                .scaleY(0f)
                .start();
    }
    public static void fabImageReveal(ImageView imageView){
        imageView.animate()
                .setDuration(100)
                .scaleX(1f)
                .scaleY(1f)
                .start();
    }

    public static void layoutUnDissolve(ConstraintLayout fab){
        fab.setAlpha(0f);
        fab.setVisibility(View.VISIBLE);
        fab.animate()
                .setDuration(350)
                .alpha(1f)
                .start();
    }

    public static void fabDissolve(FloatingActionButton fab){
        fab.animate()
                .setDuration(350)
                .alpha(0f)
                .withEndAction(() -> fab.setVisibility(View.GONE)).start();
    }

    public static void fabUnDissolve(FloatingActionButton fab){
        fab.setAlpha(0f);
        fab.setVisibility(View.VISIBLE);
        fab.animate()
                .setDuration(350)
                .alpha(1f)
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

    public static void timerFabFadeIn(FloatingActionsMenu timerPlusFab) {

        timerPlusFab.setAlpha(0f);
        timerPlusFab.setVisibility(View.VISIBLE);
        timerPlusFab.animate()
                .setDuration(350)
                .alpha(1f)
                .start();

    }

    public static void timerFabFadeOut(FloatingActionsMenu timerPlusFab) {
        timerPlusFab.animate()
                .setDuration(350)
                .alpha(0f)
                .withEndAction(() -> timerPlusFab.setVisibility(View.GONE)).start();
    }
}
