package com.theflopguyproductions.ticktrack.utils;

import android.app.Activity;
import android.content.res.ColorStateList;
import android.widget.Button;
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

    public static void settingsActivityTheme(Activity activity, ConstraintLayout backgroundLayout, TextView themeTitle, TextView themeLabel){
        int checkTheme = TickTrackDatabase.getThemeMode(activity);
        if(checkTheme==1){
            backgroundLayout.setBackgroundResource(R.color.LightGray);
            themeTitle.setTextColor(activity.getResources().getColor(R.color.DarkText));
            themeLabel.setTextColor(activity.getResources().getColor(R.color.DarkText));
            themeLabel.setText("Light");
        } else {
            backgroundLayout.setBackgroundResource(R.color.Black);
            themeTitle.setTextColor(activity.getResources().getColor(R.color.LightText));
            themeLabel.setTextColor(activity.getResources().getColor(R.color.LightText));
            themeLabel.setText("Dark");
        }
    }

    public static void counterFragmentTheme(Activity activity, RecyclerView recyclerView){
        int checkTheme = TickTrackDatabase.getThemeMode(activity);
        if(checkTheme==1){
            recyclerView.setBackgroundResource(R.color.LightGray);

        } else {
            recyclerView.setBackgroundResource(R.color.Black);
        }
    }

    public static void counterActivityTheme(Activity activity, ConstraintLayout toolbar, ConstraintLayout rootLayout, int flagColor,
                                            Button plusButtonBig, Button minusButtonBig, SwipeButton plusButton, SwipeButton minusButton,
                                            ScrollView counterActivityScrollView, TextView counterSwitchMode, Switch buttonSwitch, ConstraintLayout switchLayout,
                                            ConstraintLayout switchLowerDivider, ConstraintLayout switchUpperDivider){

        int checkTheme = TickTrackDatabase.getThemeMode(activity);
        toolbar.setBackgroundResource(counterActivityToolbarColor(flagColor));

        if(checkTheme==1){
            rootLayout.setBackgroundResource(R.color.LightGray);
            switchLayout.setBackgroundResource(R.color.LightGray);
            counterActivityScrollView.setBackgroundResource(R.color.LightGray);

            counterSwitchMode.setTextColor(activity.getResources().getColor(R.color.DarkText));
            plusButtonBig.setTextColor(activity.getResources().getColor(R.color.DarkText));
            minusButtonBig.setTextColor(activity.getResources().getColor(R.color.DarkText));

            switchLowerDivider.setBackgroundResource(R.color.Gray);
            switchUpperDivider.setBackgroundResource(R.color.Gray);

        } else {
            rootLayout.setBackgroundResource(R.color.Black);
            switchLayout.setBackgroundResource(R.color.Black);
            counterActivityScrollView.setBackgroundResource(R.color.Black);

            counterSwitchMode.setTextColor(activity.getResources().getColor(R.color.LightText));
            plusButtonBig.setTextColor(activity.getResources().getColor(R.color.LightText));
            minusButtonBig.setTextColor(activity.getResources().getColor(R.color.LightText));

            switchUpperDivider.setBackgroundResource(R.color.LightGray);
            switchLowerDivider.setBackgroundResource(R.color.LightGray);
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
