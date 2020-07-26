package com.theflopguyproductions.ticktrack.utils;

import android.app.Activity;
import android.content.res.ColorStateList;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.theflopguyproductions.ticktrack.R;

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

}
