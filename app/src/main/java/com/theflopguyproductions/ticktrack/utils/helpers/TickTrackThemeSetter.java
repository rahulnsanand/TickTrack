package com.theflopguyproductions.ticktrack.utils.helpers;

import android.app.Activity;
import android.content.res.ColorStateList;
import android.view.View;
import android.widget.NumberPicker;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.theflopguyproductions.ticktrack.R;
import com.theflopguyproductions.ticktrack.ui.utils.TickTrackProgressBar;
import com.theflopguyproductions.ticktrack.ui.utils.swipebutton.SwipeButton;
import com.theflopguyproductions.ticktrack.utils.database.TickTrackDatabase;

public class TickTrackThemeSetter {

    public static void mainActivityTheme(BottomNavigationView bottomNavigationView, Activity activity, TickTrackDatabase tickTrackDatabase, Toolbar mainToolbar,
                                         TextView ticktrackAppName){
        int checkTheme = tickTrackDatabase.getThemeMode();
        if(checkTheme==1){
            bottomNavigationView.setBackgroundColor(activity.getResources().getColor(R.color.LightGray));
            mainToolbar.setBackgroundColor(activity.getResources().getColor(R.color.LightGray));
            bottomNavigationView.setItemTextColor(ColorStateList.valueOf(activity.getResources().getColor(R.color.DarkText)));
            ticktrackAppName.setTextColor(ColorStateList.valueOf(activity.getResources().getColor(R.color.Accent)));
        } else {
            bottomNavigationView.setBackgroundColor(activity.getResources().getColor(R.color.Gray));
            mainToolbar.setBackgroundColor(activity.getResources().getColor(R.color.Black));
            mainToolbar.setPopupTheme(R.style.LightOverflow);
            bottomNavigationView.setItemTextColor(ColorStateList.valueOf(activity.getResources().getColor(R.color.LightText)));
            ticktrackAppName.setTextColor(ColorStateList.valueOf(activity.getResources().getColor(R.color.Accent)));
        }
    }

    public static void settingsActivityTheme(Activity activity, TextView themeTitle, TextView themeLabel, ScrollView settingsScrollView, ConstraintLayout themeLayout
            , TickTrackDatabase tickTrackDatabase){
        int checkTheme = tickTrackDatabase.getThemeMode();

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

    public static void counterFragmentTheme(Activity activity, RecyclerView recyclerView, ConstraintLayout counterFragmentRootLayout, TextView noCounterText
            , TickTrackDatabase tickTrackDatabase){
        int checkTheme = tickTrackDatabase.getThemeMode();
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
                                            ConstraintLayout switchLowerDivider, ConstraintLayout switchUpperDivider, TickTrackDatabase tickTrackDatabase){

        int checkTheme = tickTrackDatabase.getThemeMode();
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
                                                ConstraintLayout buttonDivider, ConstraintLayout notificationDivider, TickTrackDatabase tickTrackDatabase) {

        int checkTheme = tickTrackDatabase.getThemeMode();
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

    public static void timerRecycleTheme(Activity activity, RecyclerView recyclerView, TickTrackDatabase tickTrackDatabase){
        int checkTheme = tickTrackDatabase.getThemeMode();
        if(checkTheme==1){
            recyclerView.setBackgroundColor(activity.getResources().getColor(R.color.LightGray) );

        } else {
            recyclerView.setBackgroundColor(activity.getResources().getColor(R.color.Black) );

        }
    }

    public static void timerCreateTheme(Activity activity, NumberPicker hourPicker, NumberPicker minutePicker, NumberPicker secondPicker, NumberPicker hourLightPicker, NumberPicker minuteLightPicker,
                                        NumberPicker secondLightPicker, TextView hourLabel, TextView minuteLabel, TextView secondLabel, TextView timerLabelText,
                                        TextView timerFlagText, ConstraintLayout timerCreateRootLayout, TickTrackDatabase tickTrackDatabase ){
        int checkTheme = tickTrackDatabase.getThemeMode();
        if(checkTheme==1){
            timerCreateRootLayout.setBackgroundColor(activity.getResources().getColor(R.color.LightGray) );

            hourLabel.setTextColor(activity.getResources().getColor(R.color.DarkText));
            minuteLabel.setTextColor(activity.getResources().getColor(R.color.DarkText));
            secondLabel.setTextColor(activity.getResources().getColor(R.color.DarkText));
            timerLabelText.setTextColor(activity.getResources().getColor(R.color.DarkText));
            timerFlagText.setTextColor(activity.getResources().getColor(R.color.DarkText));

            hourLightPicker.setVisibility(View.INVISIBLE);
            minuteLightPicker.setVisibility(View.INVISIBLE);
            secondLightPicker.setVisibility(View.INVISIBLE);


        } else {
            timerCreateRootLayout.setBackgroundColor(activity.getResources().getColor(R.color.Black) );

            hourLabel.setTextColor(activity.getResources().getColor(R.color.LightText));
            minuteLabel.setTextColor(activity.getResources().getColor(R.color.LightText));
            secondLabel.setTextColor(activity.getResources().getColor(R.color.LightText));
            timerLabelText.setTextColor(activity.getResources().getColor(R.color.LightText));
            timerFlagText.setTextColor(activity.getResources().getColor(R.color.LightText));

            hourPicker.setVisibility(View.INVISIBLE);
            minutePicker.setVisibility(View.INVISIBLE);
            secondPicker.setVisibility(View.INVISIBLE);

        }
    }

    public static void timerActivityTheme(Activity activity, ConstraintLayout toolBar, int flagColor, ConstraintLayout timerRootLayout,
                                          TextView chronometer, TextView timerMillisText, TickTrackProgressBar backgroundProgressBar, TickTrackDatabase tickTrackDatabase){
        toolBar.setBackgroundResource(timerActivityToolbarColor(flagColor));
        int checkTheme = tickTrackDatabase.getThemeMode();
        if(checkTheme==1){
            timerRootLayout.setBackgroundColor(activity.getResources().getColor(R.color.LightGray) );
            chronometer.setTextColor(activity.getResources().getColor(R.color.DarkText) );
            timerMillisText.setTextColor(activity.getResources().getColor(R.color.DarkText) );
            backgroundProgressBar.setBarColor(R.color.GrayOnLight);
        } else {
            timerRootLayout.setBackgroundColor(activity.getResources().getColor(R.color.Black) );
            chronometer.setTextColor(activity.getResources().getColor(R.color.LightText) );
            timerMillisText.setTextColor(activity.getResources().getColor(R.color.LightText) );
            backgroundProgressBar.setBarColor(R.color.Black);
        }
    }

    private static int timerActivityToolbarColor(int flagColor){
        if(flagColor == 1){
            return R.color.red_matte;
        } else if(flagColor == 2){
            return R.color.green_matte;
        } else if(flagColor == 3){
            return R.color.orange_matte;
        } else if(flagColor == 4){
            return R.color.purple_matte;
        } else if(flagColor == 5){
            return R.color.blue_matte;
        }
        return R.color.Accent;
    }

    public static void stopwatchFragmentTheme(Activity activity, ConstraintLayout rootLayout, TextView lapTitleText, TextView stopwatchValueText, TickTrackDatabase tickTrackDatabase,
                                              TickTrackProgressBar backgroundProgressBar, TextView millisText){

        int checkTheme = tickTrackDatabase.getThemeMode();
        if(checkTheme==1){
            rootLayout.setBackgroundColor(activity.getResources().getColor(R.color.LightGray) );
            lapTitleText.setTextColor(activity.getResources().getColor(R.color.DarkText) );
            stopwatchValueText.setTextColor(activity.getResources().getColor(R.color.DarkText) );
            millisText.setTextColor(activity.getResources().getColor(R.color.Accent) );
            backgroundProgressBar.setBarColor(R.color.GrayOnLight);
        } else {
            rootLayout.setBackgroundColor(activity.getResources().getColor(R.color.Black) );
            lapTitleText.setTextColor(activity.getResources().getColor(R.color.LightText) );
            stopwatchValueText.setTextColor(activity.getResources().getColor(R.color.LightText) );
            millisText.setTextColor(activity.getResources().getColor(R.color.Accent) );
            backgroundProgressBar.setBarColor(R.color.Accent);
        }
        backgroundProgressBar.setProgress(1f);
    }
}
