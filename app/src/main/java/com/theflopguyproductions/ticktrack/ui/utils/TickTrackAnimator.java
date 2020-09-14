package com.theflopguyproductions.ticktrack.ui.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.theflopguyproductions.ticktrack.R;

public class TickTrackAnimator {

    public static void expandFabMenu(Context context, ConstraintLayout plusFab, ConstraintLayout timerFab, ConstraintLayout quickTimerFab, TextView timerText, TextView quickTimerText){

        timerFab.animate().setDuration(250).alpha(1f).translationY(-
                context.getResources().getDimension(R.dimen.standard_55));
        quickTimerFab.animate().setDuration(250).alpha(1f).translationY(-
                context.getResources().getDimension(R.dimen.standard_105));
        timerText.animate().setDuration(250).alpha(1f).translationY(-
                context.getResources().getDimension(R.dimen.standard_55));
        quickTimerText.animate().setDuration(250).alpha(1f).translationY(-
                context.getResources().getDimension(R.dimen.standard_105));

        plusFab.animate()
                .setDuration(100)
                .rotation(-20f)
                .withEndAction(() -> {
                    plusFab.animate().rotation(135f).setDuration(150);
                }).start();


    }
    public static void collapseFabMenu(ConstraintLayout plusFab, ConstraintLayout timerFab, ConstraintLayout quickTimerFab, TextView timerText, TextView quickTimerText){
        if(plusFab.getRotation()!=0f){

            timerFab.animate().setDuration(250).alpha(0f).translationY(0);
            quickTimerFab.animate().setDuration(250).alpha(0f).translationY(0);
            timerText.animate().setDuration(250).alpha(0f).translationY(0);
            quickTimerText.animate().setDuration(250).alpha(0f).translationY(0);


            plusFab.animate()
                    .setDuration(100)
                    .rotation(155f)
                    .withEndAction(() -> {
                        plusFab.animate().rotation(0f).setDuration(150);
                    }).start();
        }
    }

    public static void fabImageDissolve(ImageView imageView){
        if(imageView.getVisibility()==View.VISIBLE){
            imageView.setVisibility(View.INVISIBLE);
        }
        imageView.animate()
                .setDuration(100)
                .scaleX(0f)
                .scaleY(0f)
                .start();
    }
    public static void fabImageReveal(ImageView imageView){
        if(imageView.getVisibility()==View.INVISIBLE){
            imageView.setVisibility(View.VISIBLE);
        }
        imageView.animate()
                .setDuration(100)
                .scaleX(1f)
                .scaleY(1f)
                .start();
    }

    public static void fabLayoutDissolve(ConstraintLayout fab){
        if(fab.getAlpha()==1f){
            fab.animate()
                    .setDuration(350)
                    .alpha(0f)
                    .withEndAction(() -> fab.setVisibility(View.GONE)).start();
        }
    }
    public static void fabLayoutUnDissolve(ConstraintLayout fab){
        if(fab.getAlpha()!=1f){
            fab.setAlpha(0f);
            fab.setVisibility(View.VISIBLE);
            fab.animate()
                    .setDuration(350)
                    .alpha(1f)
                    .start();
        }
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

}
