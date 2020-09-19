package com.theflopguyproductions.ticktrack.utils.helpers;

import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.chip.Chip;
import com.theflopguyproductions.ticktrack.R;
import com.theflopguyproductions.ticktrack.ui.utils.TickTrackProgressBar;
import com.theflopguyproductions.ticktrack.ui.utils.swipebutton.SwipeButton;
import com.theflopguyproductions.ticktrack.utils.database.TickTrackDatabase;

public class TickTrackThemeSetter {

    public static void mainActivityTheme(BottomNavigationView bottomNavigationView, Activity activity, TickTrackDatabase tickTrackDatabase, Toolbar mainToolbar,
                                         TextView ticktrackAppName, ConstraintLayout container){
        int checkTheme = tickTrackDatabase.getThemeMode();
        if(checkTheme==1){
            container.setBackgroundResource(R.color.LightGray);
            bottomNavigationView.setBackgroundColor(activity.getResources().getColor(R.color.LightGray));
            mainToolbar.setBackgroundColor(activity.getResources().getColor(R.color.LightGray));
        } else {
            container.setBackgroundResource(R.color.Black);
            bottomNavigationView.setBackgroundColor(activity.getResources().getColor(R.color.Gray));
            mainToolbar.setBackgroundColor(activity.getResources().getColor(R.color.Black));
            mainToolbar.setPopupTheme(R.style.LightOverflow);
        }
        ticktrackAppName.setTextColor(ColorStateList.valueOf(activity.getResources().getColor(R.color.Accent)));
    }

    public static void settingsActivityTheme(Activity activity, TextView themeTitle, TextView themeLabel, NestedScrollView settingsScrollView, ConstraintLayout themeLayout
            , TickTrackDatabase tickTrackDatabase, TextView backupTitle, TextView backupEmail, ConstraintLayout backupLayout,
                                             ConstraintLayout switchAccountLayout, ConstraintLayout disconnectAccountLayout, TextView switchText, TextView disconnectText, CheckBox counterCheck,
                                             CheckBox timerCheck, RadioButton monthly, RadioButton weekly, RadioButton daily, ConstraintLayout freqOptionsLayout,
                                             RadioButton darkButton, RadioButton lightButton, ConstraintLayout themeOptionsLayout, ConstraintLayout hapticLayout, TextView hapticTitle,
                                             ConstraintLayout deleteBackupLayout, ConstraintLayout factoryResetLayout,
                                             ConstraintLayout rateUsLayout, ConstraintLayout counterSumLayout, ConstraintLayout timerSoundLayout, ConstraintLayout clockStyleLayout,
                                             ConstraintLayout clockOptionsLayout, ConstraintLayout dateTimeLayout, TextView rateUsTitle, TextView rateUsValue, TextView counterSumTitle,
                                             TextView timerSoundTitle, TextView timerSoundValue, TextView clockStyleTitle, TextView clockStyleValue,
                                             TextView dateTimeTitle, TextView dateTimeValue, ConstraintLayout toolbar, ConstraintLayout vibrateLayout, ConstraintLayout soundLayout,
                                             TextView milestoneVibrateTitle, TextView milestoneSoundTitle, TextView milestoneSoundValue){

        int checkTheme = tickTrackDatabase.getThemeMode();

        if(checkTheme==1){
            settingsScrollView.setBackgroundResource(R.color.LightGray);
            toolbar.setBackgroundResource(R.color.LightGray);
            themeLayout.setBackgroundResource(R.drawable.clickable_layout_light_background);
            backupLayout.setBackgroundResource(R.drawable.clickable_layout_light_background);
            switchAccountLayout.setBackgroundResource(R.drawable.clickable_layout_light_background);
            disconnectAccountLayout.setBackgroundResource(R.drawable.clickable_layout_light_background);
            freqOptionsLayout.setBackgroundResource(R.drawable.clickable_layout_light_background);
            themeOptionsLayout.setBackgroundResource(R.drawable.clickable_layout_light_background);
            hapticLayout.setBackgroundResource(R.drawable.clickable_layout_light_background);
            deleteBackupLayout.setBackgroundResource(R.drawable.clickable_layout_white_background);
            factoryResetLayout.setBackgroundResource(R.drawable.clickable_layout_white_background);
            rateUsLayout.setBackgroundResource(R.drawable.clickable_layout_light_background);
            counterSumLayout.setBackgroundResource(R.drawable.clickable_layout_light_background);
            timerSoundLayout.setBackgroundResource(R.drawable.clickable_layout_light_background);
            clockStyleLayout.setBackgroundResource(R.drawable.clickable_layout_light_background);
            clockOptionsLayout.setBackgroundResource(R.drawable.clickable_layout_light_background);
            dateTimeLayout.setBackgroundResource(R.drawable.clickable_layout_light_background);

            soundLayout.setBackgroundResource(R.drawable.clickable_layout_light_background);
            vibrateLayout.setBackgroundResource(R.drawable.clickable_layout_light_background);
            milestoneVibrateTitle.setTextColor(activity.getResources().getColor(R.color.DarkText));
            milestoneSoundTitle.setTextColor(activity.getResources().getColor(R.color.DarkText));
            milestoneSoundValue.setTextColor(activity.getResources().getColor(R.color.LightDarkText));

            timerSoundTitle.setTextColor(activity.getResources().getColor(R.color.DarkText));
            timerSoundValue.setTextColor(activity.getResources().getColor(R.color.LightDarkText));
            clockStyleValue.setTextColor(activity.getResources().getColor(R.color.LightDarkText));
            clockStyleTitle.setTextColor(activity.getResources().getColor(R.color.DarkText));
            dateTimeTitle.setTextColor(activity.getResources().getColor(R.color.DarkText));
            dateTimeValue.setTextColor(activity.getResources().getColor(R.color.LightDarkText));
            rateUsTitle.setTextColor(activity.getResources().getColor(R.color.DarkText));
            rateUsValue.setTextColor(activity.getResources().getColor(R.color.LightDarkText));
            counterSumTitle.setTextColor(activity.getResources().getColor(R.color.DarkText));

            hapticTitle.setTextColor(activity.getResources().getColor(R.color.DarkText));
            themeTitle.setTextColor(activity.getResources().getColor(R.color.DarkText));
            themeLabel.setTextColor(activity.getResources().getColor(R.color.LightDarkText));
            backupTitle.setTextColor(activity.getResources().getColor(R.color.DarkText));
            backupEmail.setTextColor(activity.getResources().getColor(R.color.LightDarkText));
            switchText.setTextColor(activity.getResources().getColor(R.color.DarkText));
            counterCheck.setTextColor(activity.getResources().getColor(R.color.LightDarkText));
            timerCheck.setTextColor(activity.getResources().getColor(R.color.LightDarkText));
            disconnectText.setTextColor(activity.getResources().getColor(R.color.DarkText));
            monthly.setTextColor(activity.getResources().getColor(R.color.LightDarkText));
            weekly.setTextColor(activity.getResources().getColor(R.color.LightDarkText));
            daily.setTextColor(activity.getResources().getColor(R.color.LightDarkText));
            darkButton.setTextColor(activity.getResources().getColor(R.color.LightDarkText));
            lightButton.setTextColor(activity.getResources().getColor(R.color.LightDarkText));

            themeLabel.setText("Light Mode");
        } else {
            settingsScrollView.setBackgroundResource(R.color.Black);
            toolbar.setBackgroundResource(R.color.Black);
            themeLayout.setBackgroundResource(R.drawable.clickable_layout_dark_background);
            backupLayout.setBackgroundResource(R.drawable.clickable_layout_dark_background);
            switchAccountLayout.setBackgroundResource(R.drawable.clickable_layout_dark_background);
            disconnectAccountLayout.setBackgroundResource(R.drawable.clickable_layout_dark_background);
            freqOptionsLayout.setBackgroundResource(R.drawable.clickable_layout_dark_background);
            themeOptionsLayout.setBackgroundResource(R.drawable.clickable_layout_dark_background);
            hapticLayout.setBackgroundResource(R.drawable.clickable_layout_dark_background);
            deleteBackupLayout.setBackgroundResource(R.drawable.clickable_layout_gray_background);
            factoryResetLayout.setBackgroundResource(R.drawable.clickable_layout_gray_background);
            rateUsLayout.setBackgroundResource(R.drawable.clickable_layout_dark_background);
            counterSumLayout.setBackgroundResource(R.drawable.clickable_layout_dark_background);
            timerSoundLayout.setBackgroundResource(R.drawable.clickable_layout_dark_background);
            clockStyleLayout.setBackgroundResource(R.drawable.clickable_layout_dark_background);
            clockOptionsLayout.setBackgroundResource(R.drawable.clickable_layout_dark_background);
            dateTimeLayout.setBackgroundResource(R.drawable.clickable_layout_dark_background);

            soundLayout.setBackgroundResource(R.drawable.clickable_layout_dark_background);
            vibrateLayout.setBackgroundResource(R.drawable.clickable_layout_dark_background);
            milestoneVibrateTitle.setTextColor(activity.getResources().getColor(R.color.LightText));
            milestoneSoundTitle.setTextColor(activity.getResources().getColor(R.color.LightText));
            milestoneSoundValue.setTextColor(activity.getResources().getColor(R.color.DarkLightText));

            timerSoundTitle.setTextColor(activity.getResources().getColor(R.color.LightText));
            timerSoundValue.setTextColor(activity.getResources().getColor(R.color.DarkLightText));
            clockStyleValue.setTextColor(activity.getResources().getColor(R.color.DarkLightText));
            clockStyleTitle.setTextColor(activity.getResources().getColor(R.color.LightText));
            dateTimeTitle.setTextColor(activity.getResources().getColor(R.color.LightText));
            dateTimeValue.setTextColor(activity.getResources().getColor(R.color.DarkLightText));
            rateUsTitle.setTextColor(activity.getResources().getColor(R.color.LightText));
            rateUsValue.setTextColor(activity.getResources().getColor(R.color.DarkLightText));
            counterSumTitle.setTextColor(activity.getResources().getColor(R.color.LightText));

            hapticTitle.setTextColor(activity.getResources().getColor(R.color.LightText));
            themeTitle.setTextColor(activity.getResources().getColor(R.color.LightText));
            themeLabel.setTextColor(activity.getResources().getColor(R.color.DarkLightText));
            backupTitle.setTextColor(activity.getResources().getColor(R.color.LightText));
            backupEmail.setTextColor(activity.getResources().getColor(R.color.DarkLightText));
            switchText.setTextColor(activity.getResources().getColor(R.color.LightText));
            counterCheck.setTextColor(activity.getResources().getColor(R.color.DarkLightText));
            timerCheck.setTextColor(activity.getResources().getColor(R.color.DarkLightText));
            disconnectText.setTextColor(activity.getResources().getColor(R.color.LightText));
            monthly.setTextColor(activity.getResources().getColor(R.color.DarkLightText));
            weekly.setTextColor(activity.getResources().getColor(R.color.DarkLightText));
            daily.setTextColor(activity.getResources().getColor(R.color.DarkLightText));
            lightButton.setTextColor(activity.getResources().getColor(R.color.DarkLightText));
            darkButton.setTextColor(activity.getResources().getColor(R.color.DarkLightText));

            themeLabel.setText("Dark Mode");
        }
    }

    public static void counterFragmentTheme(Activity activity, RecyclerView recyclerView, ConstraintLayout counterFragmentRootLayout, TextView noCounterText
            , TickTrackDatabase tickTrackDatabase, ConstraintLayout sumLayout, ConstraintLayout counterFab){
        int checkTheme = tickTrackDatabase.getThemeMode();
        if(checkTheme==1){
            counterFragmentRootLayout.setBackgroundResource(R.color.LightGray);
            sumLayout.setBackgroundResource(R.drawable.round_rect_white);
            recyclerView.setBackgroundResource(R.color.LightGray);
            noCounterText.setTextColor(activity.getResources().getColor(R.color.DarkText) );
            counterFab.setBackgroundResource(R.drawable.fab_light_background);

        } else {
            counterFragmentRootLayout.setBackgroundResource(R.color.Black);
            sumLayout.setBackgroundResource(R.drawable.round_rect_dark);
            recyclerView.setBackgroundResource(R.color.Black);
            noCounterText.setTextColor(activity.getResources().getColor(R.color.LightText) );
            counterFab.setBackgroundResource(R.drawable.fab_dark_background);
        }
    }

    public static void counterWidgetActivityTheme(Activity activity, RecyclerView recyclerView, ConstraintLayout counterFragmentRootLayout, TextView noCounterText
            , TickTrackDatabase tickTrackDatabase, TextView black, TextView gray, TextView light, Button cancel){
        int checkTheme = tickTrackDatabase.getThemeMode();
        if(checkTheme==1){
            counterFragmentRootLayout.setBackgroundResource(R.color.LightGray);
            recyclerView.setBackgroundResource(R.color.LightGray);
            noCounterText.setTextColor(activity.getResources().getColor(R.color.DarkText) );
            black.setTextColor(activity.getResources().getColor(R.color.DarkText) );
            gray.setTextColor(activity.getResources().getColor(R.color.DarkText) );
            light.setTextColor(activity.getResources().getColor(R.color.DarkText) );
            cancel.setTextColor(activity.getResources().getColor(R.color.DarkText) );
            cancel.setBackgroundResource(R.drawable.button_selector_white);

        } else {
            counterFragmentRootLayout.setBackgroundResource(R.color.Black);
            recyclerView.setBackgroundResource(R.color.Black);
            noCounterText.setTextColor(activity.getResources().getColor(R.color.LightText) );
            black.setTextColor(activity.getResources().getColor(R.color.LightText) );
            gray.setTextColor(activity.getResources().getColor(R.color.LightText) );
            light.setTextColor(activity.getResources().getColor(R.color.LightText) );
            cancel.setTextColor(activity.getResources().getColor(R.color.LightText) );
            cancel.setBackgroundResource(R.drawable.button_selector_dark);
        }
    }

    public static void counterActivityTheme(Activity activity, ConstraintLayout toolbar, ConstraintLayout rootLayout, int flagColor,
                                            ConstraintLayout plusButtonBig, ConstraintLayout minusButtonBig, TextView plusText, TextView minusText, SwipeButton plusLightButton, SwipeButton minusLightButton,
                                            SwipeButton plusDarkButton, SwipeButton minusDarkButton,
                                            TickTrackDatabase tickTrackDatabase, TextView counterValue){

        int checkTheme = tickTrackDatabase.getThemeMode();
        counterValue.setTextColor(activity.getResources().getColor(counterActivityToolbarColor(flagColor)));

        if(checkTheme==1){
            rootLayout.setBackgroundResource(R.color.LightGray);
            toolbar.setBackgroundResource(R.color.LightGray);


            plusButtonBig.setBackgroundResource(R.drawable.clickable_layout_light_background);
            plusText.setTextColor(activity.getResources().getColor(R.color.DarkText));
            minusButtonBig.setBackgroundResource(R.drawable.clickable_layout_light_background);
            minusText.setTextColor(activity.getResources().getColor(R.color.DarkText));

            plusDarkButton.setVisibility(View.GONE);
            minusDarkButton.setVisibility(View.GONE);
            plusLightButton.setVisibility(View.VISIBLE);
            minusLightButton.setVisibility(View.VISIBLE);


        } else {
            rootLayout.setBackgroundResource(R.color.Black);
            toolbar.setBackgroundResource(R.color.Black);

            plusButtonBig.setBackgroundResource(R.drawable.clickable_layout_dark_background);
            plusText.setTextColor(activity.getResources().getColor(R.color.LightText));
            minusButtonBig.setBackgroundResource(R.drawable.clickable_layout_dark_background);
            minusText.setTextColor(activity.getResources().getColor(R.color.LightText));

            plusDarkButton.setVisibility(View.VISIBLE);
            minusDarkButton.setVisibility(View.VISIBLE);
            plusLightButton.setVisibility(View.GONE);
            minusLightButton.setVisibility(View.GONE);

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
            return R.color.purple_matte;
        } else if(flagColor == 5){
            return R.color.blue_matte;
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
                                                ConstraintLayout buttonDivider, TickTrackDatabase tickTrackDatabase,
                                                Chip redChip,Chip greenCip,Chip orangeChip,Chip purpleChip,Chip blueChip, ConstraintLayout toolbar, ConstraintLayout negativeValueLayout,
                                                ConstraintLayout negativeDivider) {

        int checkTheme = tickTrackDatabase.getThemeMode();

        if(checkTheme==1){
            counterLabelLayout.setBackgroundResource(R.drawable.clickable_layout_light_background);
            counterValueLayout.setBackgroundResource(R.drawable.clickable_layout_light_background);
            counterMilestoneLayout.setBackgroundResource(R.drawable.clickable_layout_light_background);
            counterFlagLayout.setBackgroundResource(R.drawable.clickable_layout_light_background);
            counterButtonModeLayout.setBackgroundResource(R.drawable.clickable_layout_light_background);
            counterNotificationLayout.setBackgroundResource(R.drawable.clickable_layout_light_background);
            counterEditRootLayout.setBackgroundResource(R.drawable.clickable_layout_light_background);
            negativeValueLayout.setBackgroundResource(R.drawable.clickable_layout_light_background);
            toolbar.setBackgroundResource(R.color.LightGray);

            counterLabel.setTextColor(activity.getResources().getColor(R.color.LightDarkText));
            counterValue.setTextColor(activity.getResources().getColor(R.color.DarkText));
            counterMilestone.setTextColor(activity.getResources().getColor(R.color.DarkText));
            counterButtonMode.setTextColor(activity.getResources().getColor(R.color.LightDarkText));
            notificationDetail.setTextColor(activity.getResources().getColor(R.color.LightDarkText));
            milestoneDetail.setTextColor(activity.getResources().getColor(R.color.LightDarkText));

            labelDivider.setBackgroundResource(R.color.GrayOnLight);
            valueDivider.setBackgroundResource(R.color.GrayOnLight);
            milestoneDivider.setBackgroundResource(R.color.GrayOnLight);
            buttonDivider.setBackgroundResource(R.color.GrayOnLight);
            flagDivider.setBackgroundResource(R.color.GrayOnLight);
            negativeDivider.setBackgroundResource(R.color.GrayOnLight);

            redChip.setChipBackgroundColorResource(R.color.Clickable);
            greenCip.setChipBackgroundColorResource(R.color.Clickable);
            orangeChip.setChipBackgroundColorResource(R.color.Clickable);
            purpleChip.setChipBackgroundColorResource(R.color.Clickable);
            blueChip.setChipBackgroundColorResource(R.color.Clickable);
            redChip.setTextColor(activity.getResources().getColor(R.color.DarkText));
            greenCip.setTextColor(activity.getResources().getColor(R.color.DarkText));
            orangeChip.setTextColor(activity.getResources().getColor(R.color.DarkText));
            purpleChip.setTextColor(activity.getResources().getColor(R.color.DarkText));
            blueChip.setTextColor(activity.getResources().getColor(R.color.DarkText));
        } else {
            counterLabelLayout.setBackgroundResource(R.drawable.clickable_layout_dark_background);
            counterValueLayout.setBackgroundResource(R.drawable.clickable_layout_dark_background);
            counterMilestoneLayout.setBackgroundResource(R.drawable.clickable_layout_dark_background);
            counterFlagLayout.setBackgroundResource(R.drawable.clickable_layout_dark_background);
            counterButtonModeLayout.setBackgroundResource(R.drawable.clickable_layout_dark_background);
            counterNotificationLayout.setBackgroundResource(R.drawable.clickable_layout_dark_background);
            counterEditRootLayout.setBackgroundResource(R.drawable.clickable_layout_dark_background);
            negativeValueLayout.setBackgroundResource(R.drawable.clickable_layout_dark_background);
            toolbar.setBackgroundResource(R.color.Black);

            counterLabel.setTextColor(activity.getResources().getColor(R.color.DarkLightText));
            counterValue.setTextColor(activity.getResources().getColor(R.color.LightText));
            counterMilestone.setTextColor(activity.getResources().getColor(R.color.LightText));
            counterButtonMode.setTextColor(activity.getResources().getColor(R.color.DarkLightText));
            notificationDetail.setTextColor(activity.getResources().getColor(R.color.DarkLightText));
            milestoneDetail.setTextColor(activity.getResources().getColor(R.color.DarkLightText));

            labelDivider.setBackgroundResource(R.color.GrayOnDark);
            valueDivider.setBackgroundResource(R.color.GrayOnDark);
            milestoneDivider.setBackgroundResource(R.color.GrayOnDark);
            buttonDivider.setBackgroundResource(R.color.GrayOnDark);
            flagDivider.setBackgroundResource(R.color.GrayOnDark);
            negativeDivider.setBackgroundResource(R.color.GrayOnDark);

            redChip.setChipBackgroundColorResource(R.color.GrayOnDark);
            greenCip.setChipBackgroundColorResource(R.color.GrayOnDark);
            orangeChip.setChipBackgroundColorResource(R.color.GrayOnDark);
            purpleChip.setChipBackgroundColorResource(R.color.GrayOnDark);
            blueChip.setChipBackgroundColorResource(R.color.GrayOnDark);
            redChip.setTextColor(activity.getResources().getColor(R.color.LightText));
            greenCip.setTextColor(activity.getResources().getColor(R.color.LightText));
            orangeChip.setTextColor(activity.getResources().getColor(R.color.LightText));
            purpleChip.setTextColor(activity.getResources().getColor(R.color.LightText));
            blueChip.setTextColor(activity.getResources().getColor(R.color.LightText));
        }
    }

    public static void timerRecycleTheme(Activity activity, RecyclerView recyclerView, TickTrackDatabase tickTrackDatabase){
        int checkTheme = tickTrackDatabase.getThemeMode();
        if(checkTheme==1){
//            recyclerView.setBackgroundColor(activity.getResources().getColor(R.color.LightGray) );
        } else {
//            recyclerView.setBackgroundColor(activity.getResources().getColor(R.color.Black) );
        }
    }
    public static void timerRecycleTheme(Activity activity, RecyclerView recyclerView, TickTrackDatabase tickTrackDatabase, ConstraintLayout rootLayout, TextView noTimerText){
        int checkTheme = tickTrackDatabase.getThemeMode();
        if(checkTheme==1){
            recyclerView.setBackgroundColor(activity.getResources().getColor(R.color.LightGray) );
            rootLayout.setBackgroundColor(activity.getResources().getColor(R.color.LightGray) );
            noTimerText.setTextColor(activity.getResources().getColor(R.color.DarkText));
        } else {
            recyclerView.setBackgroundColor(activity.getResources().getColor(R.color.Black) );
            rootLayout.setBackgroundColor(activity.getResources().getColor(R.color.Black) );
            noTimerText.setTextColor(activity.getResources().getColor(R.color.LightText));
        }
    }

    public static void timerCreateTheme(Activity activity, NumberPicker hourPicker, NumberPicker minutePicker, NumberPicker secondPicker, NumberPicker hourLightPicker, NumberPicker minuteLightPicker,
                                        NumberPicker secondLightPicker, TextView hourLabel, TextView minuteLabel, TextView secondLabel, TextView timerLabelText,
                                        TextView timerFlagText, ConstraintLayout timerCreateRootLayout, TickTrackDatabase tickTrackDatabase ,
                                        Chip redChip,Chip  greenCip,Chip  orangeChip,Chip  purpleChip,Chip  blueChip, ConstraintLayout createFab){
        int checkTheme = tickTrackDatabase.getThemeMode();
        if(checkTheme==1){
            timerCreateRootLayout.setBackgroundColor(activity.getResources().getColor(R.color.LightGray) );

            hourLabel.setTextColor(activity.getResources().getColor(R.color.DarkText));
            minuteLabel.setTextColor(activity.getResources().getColor(R.color.DarkText));
            secondLabel.setTextColor(activity.getResources().getColor(R.color.DarkText));
            timerLabelText.setTextColor(activity.getResources().getColor(R.color.DarkText));
            timerFlagText.setTextColor(activity.getResources().getColor(R.color.DarkText));

            createFab.setBackgroundResource(R.drawable.fab_light_background);

            hourLightPicker.setVisibility(View.INVISIBLE);
            minuteLightPicker.setVisibility(View.INVISIBLE);
            secondLightPicker.setVisibility(View.INVISIBLE);

            redChip.setChipBackgroundColorResource(R.color.Clickable);
            greenCip.setChipBackgroundColorResource(R.color.Clickable);
            orangeChip.setChipBackgroundColorResource(R.color.Clickable);
            purpleChip.setChipBackgroundColorResource(R.color.Clickable);
            blueChip.setChipBackgroundColorResource(R.color.Clickable);
            redChip.setTextColor(activity.getResources().getColor(R.color.DarkText));
            greenCip.setTextColor(activity.getResources().getColor(R.color.DarkText));
            orangeChip.setTextColor(activity.getResources().getColor(R.color.DarkText));
            purpleChip.setTextColor(activity.getResources().getColor(R.color.DarkText));
            blueChip.setTextColor(activity.getResources().getColor(R.color.DarkText));
        } else {
            timerCreateRootLayout.setBackgroundColor(activity.getResources().getColor(R.color.Black) );

            hourLabel.setTextColor(activity.getResources().getColor(R.color.LightText));
            minuteLabel.setTextColor(activity.getResources().getColor(R.color.LightText));
            secondLabel.setTextColor(activity.getResources().getColor(R.color.LightText));
            timerLabelText.setTextColor(activity.getResources().getColor(R.color.LightText));
            timerFlagText.setTextColor(activity.getResources().getColor(R.color.LightText));

            createFab.setBackgroundResource(R.drawable.fab_dark_background);

            hourPicker.setVisibility(View.INVISIBLE);
            minutePicker.setVisibility(View.INVISIBLE);
            secondPicker.setVisibility(View.INVISIBLE);

            redChip.setChipBackgroundColorResource(R.color.GrayOnDark);
            greenCip.setChipBackgroundColorResource(R.color.GrayOnDark);
            orangeChip.setChipBackgroundColorResource(R.color.GrayOnDark);
            purpleChip.setChipBackgroundColorResource(R.color.GrayOnDark);
            blueChip.setChipBackgroundColorResource(R.color.GrayOnDark);
            redChip.setTextColor(activity.getResources().getColor(R.color.LightText));
            greenCip.setTextColor(activity.getResources().getColor(R.color.LightText));
            orangeChip.setTextColor(activity.getResources().getColor(R.color.LightText));
            purpleChip.setTextColor(activity.getResources().getColor(R.color.LightText));
            blueChip.setTextColor(activity.getResources().getColor(R.color.LightText));
        }
    }

    public static void timerActivityTheme(Activity activity, ConstraintLayout toolBar, int flagColor, ConstraintLayout timerRootLayout,
                                          TextView chronometer, TextView timerMillisText, TickTrackProgressBar backgroundProgressBar, TickTrackDatabase tickTrackDatabase,
                                          TickTrackProgressBar tickTrackProgressBar, TextView startData, TextView startTitle, TextView endData, TextView endTitle, ConstraintLayout playFab,
                                          ConstraintLayout resetFab, ConstraintLayout oneFab){

        tickTrackProgressBar.setBarColor(activity.getResources().getColor(timerActivityToolbarColor(flagColor)));
        int checkTheme = tickTrackDatabase.getThemeMode();
        if(checkTheme==1){
            timerRootLayout.setBackgroundColor(activity.getResources().getColor(R.color.LightGray) );
            toolBar.setBackgroundColor(activity.getResources().getColor(R.color.LightGray));
            chronometer.setTextColor(activity.getResources().getColor(R.color.DarkText) );
            timerMillisText.setTextColor(activity.getResources().getColor(R.color.DarkText) );
            backgroundProgressBar.setBarColor(R.color.GrayOnLight);
            startData.setTextColor(activity.getResources().getColor(R.color.LightDarkText) );
            endData.setTextColor(activity.getResources().getColor(R.color.LightDarkText) );
            startTitle.setTextColor(activity.getResources().getColor(R.color.DarkText) );
            endTitle.setTextColor(activity.getResources().getColor(R.color.DarkText) );

            playFab.setBackgroundResource(R.drawable.fab_light_background);
            resetFab.setBackgroundResource(R.drawable.fab_light_background);
            oneFab.setBackgroundResource(R.drawable.fab_light_background);
        } else {
            timerRootLayout.setBackgroundColor(activity.getResources().getColor(R.color.Black) );
            toolBar.setBackgroundColor(activity.getResources().getColor(R.color.Black));
            chronometer.setTextColor(activity.getResources().getColor(R.color.LightText) );
            timerMillisText.setTextColor(activity.getResources().getColor(R.color.LightText) );
            backgroundProgressBar.setBarColor(R.color.Black);
            startData.setTextColor(activity.getResources().getColor(R.color.DarkLightText) );
            endData.setTextColor(activity.getResources().getColor(R.color.DarkLightText) );
            startTitle.setTextColor(activity.getResources().getColor(R.color.LightText) );
            endTitle.setTextColor(activity.getResources().getColor(R.color.LightText) );

            playFab.setBackgroundResource(R.drawable.fab_dark_background);
            resetFab.setBackgroundResource(R.drawable.fab_dark_background);
            oneFab.setBackgroundResource(R.drawable.fab_dark_background);
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
                                              TickTrackProgressBar backgroundProgressBar, TextView millisText, ConstraintLayout playPauseFab, ConstraintLayout resetFab, ConstraintLayout lapFab){

        int checkTheme = tickTrackDatabase.getThemeMode();
        if(checkTheme==1){
            rootLayout.setBackgroundColor(activity.getResources().getColor(R.color.LightGray) );
            lapTitleText.setTextColor(activity.getResources().getColor(R.color.DarkText) );
            stopwatchValueText.setTextColor(activity.getResources().getColor(R.color.DarkText) );
            millisText.setTextColor(activity.getResources().getColor(R.color.Accent) );
            backgroundProgressBar.setBarColor(R.color.GrayOnLight);
            playPauseFab.setBackgroundResource(R.drawable.fab_light_background);
            resetFab.setBackgroundResource(R.drawable.fab_light_background);
            lapFab.setBackgroundResource(R.drawable.fab_light_background);
        } else {
            rootLayout.setBackgroundColor(activity.getResources().getColor(R.color.Black) );
            lapTitleText.setTextColor(activity.getResources().getColor(R.color.LightText) );
            stopwatchValueText.setTextColor(activity.getResources().getColor(R.color.LightText) );
            millisText.setTextColor(activity.getResources().getColor(R.color.Accent) );
            backgroundProgressBar.setBarColor(R.color.Accent);
            playPauseFab.setBackgroundResource(R.drawable.fab_dark_background);
            resetFab.setBackgroundResource(R.drawable.fab_dark_background);
            lapFab.setBackgroundResource(R.drawable.fab_dark_background);
        }
        backgroundProgressBar.setProgress(1f);
    }

    public static void quickTimerActivityTheme(Activity activity,TickTrackDatabase tickTrackDatabase, NumberPicker hourPickerLight, NumberPicker minutePickerLight, NumberPicker secondPickerLight,
                                               NumberPicker hourPickerDark, NumberPicker minutePickerDark, NumberPicker secondPickerDark, TextView hourText, TextView minuteText,
                                               TextView secondText, ConstraintLayout rootLayout, Button customButton, ConstraintLayout oneMinute, ConstraintLayout twoMinute, ConstraintLayout fiveMinute,
                                               ConstraintLayout tenMinute, ConstraintLayout playFab) {
        int checkTheme = tickTrackDatabase.getThemeMode();
        if(checkTheme==1){
            rootLayout.setBackgroundColor(activity.getResources().getColor(R.color.LightGray) );
            hourText.setTextColor(activity.getResources().getColor(R.color.DarkText) );
            minuteText.setTextColor(activity.getResources().getColor(R.color.DarkText) );
            secondText.setTextColor(activity.getResources().getColor(R.color.DarkText) );
            customButton.setBackgroundResource(R.drawable.button_selector_white);

            oneMinute.setBackgroundResource(R.drawable.fab_light_background);
            twoMinute.setBackgroundResource(R.drawable.fab_light_background);
            fiveMinute.setBackgroundResource(R.drawable.fab_light_background);
            tenMinute.setBackgroundResource(R.drawable.fab_light_background);
            playFab.setBackgroundResource(R.drawable.fab_light_background);

            hourPickerDark.setVisibility(View.VISIBLE);
            minutePickerDark.setVisibility(View.VISIBLE);
            secondPickerDark.setVisibility(View.VISIBLE);
            hourPickerLight.setVisibility(View.INVISIBLE);
            minutePickerLight.setVisibility(View.INVISIBLE);
            secondPickerLight.setVisibility(View.INVISIBLE);
        } else {
            rootLayout.setBackgroundColor(activity.getResources().getColor(R.color.Black) );
            hourText.setTextColor(activity.getResources().getColor(R.color.LightText) );
            minuteText.setTextColor(activity.getResources().getColor(R.color.LightText) );
            secondText.setTextColor(activity.getResources().getColor(R.color.LightText) );
            customButton.setBackgroundResource(R.drawable.button_selector_dark);

            oneMinute.setBackgroundResource(R.drawable.fab_dark_background);
            twoMinute.setBackgroundResource(R.drawable.fab_dark_background);
            fiveMinute.setBackgroundResource(R.drawable.fab_dark_background);
            tenMinute.setBackgroundResource(R.drawable.fab_dark_background);
            playFab.setBackgroundResource(R.drawable.fab_dark_background);

            hourPickerDark.setVisibility(View.INVISIBLE);
            minutePickerDark.setVisibility(View.INVISIBLE);
            secondPickerDark.setVisibility(View.INVISIBLE);
            hourPickerLight.setVisibility(View.VISIBLE);
            minutePickerLight.setVisibility(View.VISIBLE);
            secondPickerLight.setVisibility(View.VISIBLE);
        }
    }

    public static void clockWidgetSetupTheme(ConstraintLayout rootLayout, TextView optionText, TickTrackDatabase tickTrackDatabase, Activity activity) {
        int checkTheme = tickTrackDatabase.getThemeMode();
        if(checkTheme==1){
            rootLayout.setBackgroundColor(activity.getResources().getColor(R.color.LightGray) );
            optionText.setTextColor(activity.getResources().getColor(R.color.DarkText) );
        } else {
            rootLayout.setBackgroundColor(activity.getResources().getColor(R.color.Black) );
            optionText.setTextColor(activity.getResources().getColor(R.color.LightText) );
        }
    }

    public static void timerFragmentTheme(Context context, TickTrackDatabase tickTrackDatabase, ConstraintLayout timerPlusFab, ConstraintLayout quickTimerFab,
                                          ConstraintLayout normalTimerFab, ConstraintLayout timerDiscardFAB, TextView timerText, TextView quickTimerText,
                                          ConstraintLayout rootLayout) {
        int checkTheme = tickTrackDatabase.getThemeMode();
        if(checkTheme==1){
            rootLayout.setBackgroundResource(R.color.LightGray);
            timerPlusFab.setBackgroundResource(R.drawable.fab_light_background);
            normalTimerFab.setBackgroundResource(R.drawable.fab_light_background);
            timerDiscardFAB.setBackgroundResource(R.drawable.fab_light_background);
            quickTimerFab.setBackgroundResource(R.drawable.fab_light_background);
            timerText.setTextColor(context.getResources().getColor(R.color.DarkText) );
            quickTimerText.setTextColor(context.getResources().getColor(R.color.DarkText) );
        } else {
            rootLayout.setBackgroundResource(R.color.Black);
            timerPlusFab.setBackgroundResource(R.drawable.fab_dark_background);
            normalTimerFab.setBackgroundResource(R.drawable.fab_dark_background);
            timerDiscardFAB.setBackgroundResource(R.drawable.fab_dark_background);
            quickTimerFab.setBackgroundResource(R.drawable.fab_dark_background);
            timerText.setTextColor(context.getResources().getColor(R.color.LightText) );
            quickTimerText.setTextColor(context.getResources().getColor(R.color.LightText) );
        }
    }

    public static void aboutActivityTheme(TickTrackDatabase tickTrackDatabase, Context context, ConstraintLayout rootLayout, ConstraintLayout toolbarLayout,
                                          TextView storyText, TextView versionText) {

        int checkTheme = tickTrackDatabase.getThemeMode();
        if(checkTheme==1){
            rootLayout.setBackgroundResource(R.color.LightGray);
            toolbarLayout.setBackgroundResource(R.color.LightGray);
            storyText.setTextColor(context.getResources().getColor(R.color.DarkText) );
            versionText.setTextColor(context.getResources().getColor(R.color.DarkText) );
        } else {
            rootLayout.setBackgroundResource(R.color.Black);
            toolbarLayout.setBackgroundResource(R.color.Black);
            storyText.setTextColor(context.getResources().getColor(R.color.LightText) );
            versionText.setTextColor(context.getResources().getColor(R.color.LightText) );
        }
    }
}
