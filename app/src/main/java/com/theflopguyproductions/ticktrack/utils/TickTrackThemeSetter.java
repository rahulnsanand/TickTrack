package com.theflopguyproductions.ticktrack.utils;

import android.app.Activity;
import android.content.res.ColorStateList;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextClock;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.theflopguyproductions.ticktrack.R;
import com.theflopguyproductions.ticktrack.counter.activity.CounterEditActivity;
import com.theflopguyproductions.ticktrack.ui.utils.swipebutton.SwipeButton;

import org.w3c.dom.Text;

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

    public static void counterEditActivityTheme(Activity activity, ConstraintLayout counterLabelLayout,
                                                ConstraintLayout counterValueLayout, ConstraintLayout counterMilestoneLayout,
                                                ConstraintLayout counterFlagLayout, ConstraintLayout counterButtonModeLayout,
                                                ConstraintLayout counterNotificationLayout, ConstraintLayout counterEditRootLayout,
                                                TextView counterLabel, TextView counterValue, TextView counterMilestone, TextView counterButtonMode,
                                                TextView notificationDetail, TextView milestoneDetail, int flagColor,
                                                ConstraintLayout labelDivider, ConstraintLayout valueDivider, ConstraintLayout milestoneDivider, ConstraintLayout flagDivider,
                                                ConstraintLayout buttonDivider, ConstraintLayout notificationDivider) {

        int checkTheme = TickTrackDatabase.getThemeMode(activity);
        if(checkTheme==1){
            counterLabelLayout.setBackgroundResource(R.drawable.clickable_layout_light_background);
            counterValueLayout.setBackgroundResource(R.drawable.clickable_layout_light_background);
            counterMilestoneLayout.setBackgroundResource(R.drawable.clickable_layout_light_background);
            counterFlagLayout.setBackgroundResource(R.drawable.clickable_layout_light_background);
            counterButtonModeLayout.setBackgroundResource(R.drawable.clickable_layout_light_background);
            counterNotificationLayout.setBackgroundResource(R.drawable.clickable_layout_light_background);
            counterEditRootLayout.setBackgroundResource(R.drawable.clickable_layout_light_background);

            counterLabel.setTextColor(activity.getResources().getColor(R.color.DarkText));
            counterValue.setTextColor(activity.getResources().getColor(R.color.DarkText));
            counterMilestone.setTextColor(activity.getResources().getColor(R.color.DarkText));
            counterButtonMode.setTextColor(activity.getResources().getColor(R.color.DarkText));
            notificationDetail.setTextColor(activity.getResources().getColor(R.color.DarkText));
            milestoneDetail.setTextColor(activity.getResources().getColor(R.color.DarkText));

            labelDivider.setBackgroundResource(R.color.GrayOnLight);
            valueDivider.setBackgroundResource(R.color.GrayOnLight);
            milestoneDivider.setBackgroundResource(R.color.GrayOnLight);
            buttonDivider.setBackgroundResource(R.color.GrayOnLight);
            notificationDivider.setBackgroundResource(R.color.GrayOnLight);
            flagDivider.setBackgroundResource(R.color.GrayOnLight);

        } else {
            counterLabelLayout.setBackgroundResource(R.drawable.clickable_layout_dark_background);
            counterValueLayout.setBackgroundResource(R.drawable.clickable_layout_dark_background);
            counterMilestoneLayout.setBackgroundResource(R.drawable.clickable_layout_dark_background);
            counterFlagLayout.setBackgroundResource(R.drawable.clickable_layout_dark_background);
            counterButtonModeLayout.setBackgroundResource(R.drawable.clickable_layout_dark_background);
            counterNotificationLayout.setBackgroundResource(R.drawable.clickable_layout_dark_background);
            counterEditRootLayout.setBackgroundResource(R.drawable.clickable_layout_dark_background);

            counterLabel.setTextColor(activity.getResources().getColor(R.color.LightText));
            counterValue.setTextColor(activity.getResources().getColor(R.color.LightText));
            counterMilestone.setTextColor(activity.getResources().getColor(R.color.LightText));
            counterButtonMode.setTextColor(activity.getResources().getColor(R.color.LightText));
            notificationDetail.setTextColor(activity.getResources().getColor(R.color.LightText));
            milestoneDetail.setTextColor(activity.getResources().getColor(R.color.LightText));

            labelDivider.setBackgroundResource(R.color.GrayOnDark);
            valueDivider.setBackgroundResource(R.color.GrayOnDark);
            milestoneDivider.setBackgroundResource(R.color.GrayOnDark);
            buttonDivider.setBackgroundResource(R.color.GrayOnDark);
            notificationDivider.setBackgroundResource(R.color.GrayOnDark);
            flagDivider.setBackgroundResource(R.color.GrayOnDark);


        }
    }
}
