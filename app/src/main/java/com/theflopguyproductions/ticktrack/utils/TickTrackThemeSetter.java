package com.theflopguyproductions.ticktrack.utils;

import android.app.Activity;
import android.content.res.ColorStateList;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.theflopguyproductions.ticktrack.R;
import com.theflopguyproductions.ticktrack.ui.utils.swipebutton.SwipeButton;

public class TickTrackThemeSetter {

    public static void mainActivityTheme(BottomNavigationView bottomNavigationView, Activity activity){
        int checkTheme = TickTrackDatabase.getThemeMode(activity);
        if(checkTheme==1){
            bottomNavigationView.setBackgroundColor(activity.getResources().getColor(R.color.LightGray));
             bottomNavigationView.setItemTextColor(ColorStateList.valueOf(activity.getResources().getColor(R.color.DarkText)));
        } else {
            bottomNavigationView.setBackgroundColor(activity.getResources().getColor(R.color.Gray));
            bottomNavigationView.setItemTextColor(ColorStateList.valueOf(activity.getResources().getColor(R.color.LightText)));
        }
    }

    public static void settingsActivityTheme(Activity activity, TextView themeTitle, TextView themeLabel, ScrollView settingsScrollView, ConstraintLayout themeLayout){
        int checkTheme = TickTrackDatabase.getThemeMode(activity);

        if(checkTheme==1){
            settingsScrollView.setBackgroundResource(R.color.LightGray);
            themeLayout.setBackgroundResource(R.drawable.clickable_layout_light_background);
            themeTitle.setTextColor(activity.getResources().getColor(R.color.DarkText));
            themeLabel.setTextColor(activity.getResources().getColor(R.color.DarkText));
            themeLabel.setText("Light");
        } else {
            settingsScrollView.setBackgroundResource(R.color.Black);
            themeLayout.setBackgroundResource(R.drawable.clickable_layout_dark_background);
            themeTitle.setTextColor(activity.getResources().getColor(R.color.LightText));
            themeLabel.setTextColor(activity.getResources().getColor(R.color.LightText));
            themeLabel.setText("Dark");
        }
    }

    public static void counterFragmentTheme(Activity activity, RecyclerView recyclerView, ConstraintLayout counterFragmentRootLayout, TextView noCounterText){
        int checkTheme = TickTrackDatabase.getThemeMode(activity);
        if(checkTheme==1){
            counterFragmentRootLayout.setBackgroundResource(R.color.LightGray);
            recyclerView.setBackgroundResource(R.color.LightGray);
            noCounterText.setTextColor(activity.getResources().getColor(R.color.DarkText) );

        } else {
            counterFragmentRootLayout.setBackgroundResource(R.color.Black);
            recyclerView.setBackgroundResource(R.color.Black);
            noCounterText.setTextColor(activity.getResources().getColor(R.color.LightText) );
        }
    }

    public static void counterActivityTheme(Activity activity, ConstraintLayout toolbar, ConstraintLayout rootLayout, int flagColor,
                                            ConstraintLayout plusButtonBig, ConstraintLayout minusButtonBig, TextView plusText, TextView minusText, SwipeButton plusButton, SwipeButton minusButton,
                                            ScrollView counterActivityScrollView, TextView counterSwitchMode, Switch buttonSwitch, ConstraintLayout switchLayout,
                                            ConstraintLayout switchLowerDivider, ConstraintLayout switchUpperDivider){

        int checkTheme = TickTrackDatabase.getThemeMode(activity);
        toolbar.setBackgroundResource(counterActivityToolbarColor(flagColor));

        if(checkTheme==1){
            rootLayout.setBackgroundResource(R.color.LightGray);
            switchLayout.setBackgroundResource(R.color.LightGray);
            counterActivityScrollView.setBackgroundResource(R.color.LightGray);

            counterSwitchMode.setTextColor(activity.getResources().getColor(R.color.DarkText));
            plusButtonBig.setBackgroundResource(R.drawable.clickable_layout_light_background);
            plusText.setTextColor(activity.getResources().getColor(R.color.DarkText));
            minusButtonBig.setBackgroundResource(R.drawable.clickable_layout_light_background);
            minusText.setTextColor(activity.getResources().getColor(R.color.DarkText));

            switchLowerDivider.setBackgroundResource(R.color.GrayOnLight);
            switchUpperDivider.setBackgroundResource(R.color.GrayOnLight);

        } else {
            rootLayout.setBackgroundResource(R.color.Black);
            switchLayout.setBackgroundResource(R.color.Black);
            counterActivityScrollView.setBackgroundResource(R.color.Black);

            counterSwitchMode.setTextColor(activity.getResources().getColor(R.color.LightText));
            plusButtonBig.setBackgroundResource(R.drawable.clickable_layout_dark_background);
            plusText.setTextColor(activity.getResources().getColor(R.color.LightText));
            minusButtonBig.setBackgroundResource(R.drawable.clickable_layout_dark_background);
            minusText.setTextColor(activity.getResources().getColor(R.color.LightText));


            switchUpperDivider.setBackgroundResource(R.color.GrayOnDark);
            switchLowerDivider.setBackgroundResource(R.color.GrayOnDark);
        }

    }

    private static int counterActivityToolbarColor(int flagColor){
        if(flagColor == 1){
            return R.color.red_matte;
        } else if(flagColor == 2){
            return R.color.green_matte;
        } else if(flagColor == 3){
            return R.color.orange_matte;
        } else if(flagColor == 4){
            return R.color.blue_matte;
        } else if(flagColor == 5){
            return R.color.purple_matte;
        }
        return R.color.Accent;
    }

}
