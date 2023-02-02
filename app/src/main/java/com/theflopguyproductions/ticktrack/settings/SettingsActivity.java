package com.theflopguyproductions.ticktrack.settings;

import android.Manifest;
import android.animation.Animator;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.preference.PreferenceManager;

import com.google.android.material.snackbar.Snackbar;
import com.theflopguyproductions.ticktrack.R;
import com.theflopguyproductions.ticktrack.SoYouADeveloperHuh;
import com.theflopguyproductions.ticktrack.dialogs.DeleteTimer;
import com.theflopguyproductions.ticktrack.dialogs.ProgressBarDialog;
import com.theflopguyproductions.ticktrack.dialogs.SwipeDialog;
import com.theflopguyproductions.ticktrack.receivers.BackupScheduleReceiver;
import com.theflopguyproductions.ticktrack.screensaver.ScreensaverActivity;
import com.theflopguyproductions.ticktrack.service.BackupRestoreService;
import com.theflopguyproductions.ticktrack.startup.StartUpActivity;
import com.theflopguyproductions.ticktrack.ui.lottie.LottieAnimationView;
import com.theflopguyproductions.ticktrack.utils.PermissionUtils;
import com.theflopguyproductions.ticktrack.utils.RateUsUtil;
import com.theflopguyproductions.ticktrack.utils.database.TickTrackDatabase;
import com.theflopguyproductions.ticktrack.utils.database.TickTrackFirebaseDatabase;
import com.theflopguyproductions.ticktrack.utils.firebase.FirebaseHelper;
import com.theflopguyproductions.ticktrack.utils.helpers.AutoStartPermissionHelper;
import com.theflopguyproductions.ticktrack.utils.helpers.TickTrackThemeSetter;
import com.theflopguyproductions.ticktrack.utils.helpers.TimeAgo;
import com.theflopguyproductions.ticktrack.widgets.shortcuts.CreateTimerWidget;
import com.theflopguyproductions.ticktrack.widgets.shortcuts.QuickTimerWidget;
import com.theflopguyproductions.ticktrack.widgets.shortcuts.ScreensaverWidget;
import com.theflopguyproductions.ticktrack.widgets.shortcuts.StopwatchWidget;

import java.util.ArrayList;
import java.util.Locale;

public class SettingsActivity extends AppCompatActivity {

    private Activity activity;
    private ImageButton backButton;
    private NestedScrollView settingsScrollView;
    private int prevFragment = -1;

    private FirebaseHelper firebaseHelper;
    private TickTrackDatabase tickTrackDatabase;
    private TickTrackFirebaseDatabase tickTrackFirebaseDatabase;
    private SharedPreferences sharedPreferences;

    private ConstraintLayout themeLayout, toolbar;
    private TextView themeName, themeTitle;

    private ConstraintLayout googleAccountLayout, accountOptionsLayout, switchAccountOptionLayout, disconnectAccountOptionLayout, syncDataLayout, syncFreqOptionsLayout,
            includeDataLayout, backupDataOptionsLayout, themeOptionsLayout, notificationsLayout;
    private TextView backupGoogleTitle, backupEmail, switchAccountTitle, disconnectAccountTitle, syncDataTitle, syncDataFrequency, syncDataLastSync, includeDataTitle, includeDataValue;
    private CheckBox counterCheckBox, timerCheckBox;
    private RadioButton monthlyButton, weeklyButton, dailyButton, darkButton, lightButton;

    private ConstraintLayout hapticLayout, generalRootLayout;
    private TextView hapticTextTitle;
    private Switch hapticSwitch;

    private ConstraintLayout deleteBackupLayout, factoryResetLayout;

    private ConstraintLayout rateUsLayout, displaySumLayout, timerSoundLayout, clockStyleLayout, clockOptionsLayout, dateTimeLayout;
    private TextView rateUsTitle, rateUsValue, displaySumTitle, timerSoundTitle, timerSoundValue, clockStyleTitle, clockStyleValue, dateTimeTitle, dateTimeValue;
    private ImageView unordinaryImage, oxygenyImage, minimalImage, simplisticImage, romanImage, funkyImage;
    private ImageView unordinaryImageCheck, oxygenyImageCheck, minimalImageCheck, simplisticImageCheck, romanImageCheck, funkyImageCheck;
    private Switch displaySumSwitch;

    private LottieAnimationView lottieAnimationView;

    private TextView vibrateMilestoneTitle, milestoneSoundTitle, milestoneSoundValue, notificationsTitle, notificationValue;
    private Switch milestoneSwitch;
    private ConstraintLayout milestoneVibrateLayout, milestoneSoundLayout;

    private ConstraintLayout autostartLayout, retrySyncLayoutAccount;
    private TextView autostartTitle, autostartValue;


    @Override
    protected void onResume() {
        super.onResume();
        sharedPreferences.registerOnSharedPreferenceChangeListener(sharedPreferenceChangeListener);
        if(firebaseHelper.isUserSignedIn()){
            setupEmailText();
        }
        refreshTheme();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_NOTIFICATION_POLICY) == PackageManager.PERMISSION_GRANTED){
            notificationsLayout.setVisibility(View.GONE);
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                notificationsLayout.setVisibility(View.VISIBLE);
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                notificationsLayout.setVisibility(View.VISIBLE);
            }
        }
    }

    SharedPreferences.OnSharedPreferenceChangeListener sharedPreferenceChangeListener = (sharedPreferences, s) ->  {
        databaseChangeListener();
    };
    private void databaseChangeListener() {
        setupEmailText();
        checkAccountAvailable();
        setupLastBackupText();
        setupDataOptionsText();
        setupSyncFreqOptionText();
        setupThemeText();
        setupAccountBusy();
        setupHapticData();
        setupTimerSound();
        setupMilestoneSound();
        setupMilestoneVibrate();
        setupCounterSum();
        setupRetrySync();
    }

    private void setupSettingsChangeTime() {
        tickTrackDatabase.storeSettingsChangeTime(System.currentTimeMillis());
    }

    private void setupAccountBusy() {
        if(isMyServiceRunning(getApplicationContext())){
            if(tickTrackDatabase.getThemeMode()==1){
                switchAccountOptionLayout.setBackgroundResource(R.drawable.clickable_layout_light_background);
                disconnectAccountOptionLayout.setBackgroundResource(R.drawable.clickable_layout_light_background);
                switchAccountTitle.setTextColor(getResources().getColor(R.color.roboto_calendar_current_day_ring));
                disconnectAccountTitle.setTextColor(getResources().getColor(R.color.roboto_calendar_current_day_ring));
            } else {
                switchAccountOptionLayout.setBackgroundResource(R.drawable.clickable_layout_dark_background);
                disconnectAccountOptionLayout.setBackgroundResource(R.drawable.clickable_layout_dark_background);
                switchAccountTitle.setTextColor(getResources().getColor(R.color.roboto_calendar_day_of_the_week_font));
                disconnectAccountTitle.setTextColor(getResources().getColor(R.color.roboto_calendar_day_of_the_week_font));
            }
        } else {
            if(tickTrackDatabase.getThemeMode()==1){
                switchAccountOptionLayout.setBackgroundResource(R.drawable.clickable_layout_light_background);
                disconnectAccountOptionLayout.setBackgroundResource(R.drawable.clickable_layout_light_background);
                switchAccountTitle.setTextColor(getResources().getColor(R.color.DarkText));
                disconnectAccountTitle.setTextColor(getResources().getColor(R.color.DarkText));
            } else {
                switchAccountOptionLayout.setBackgroundResource(R.drawable.clickable_layout_dark_background);
                disconnectAccountOptionLayout.setBackgroundResource(R.drawable.clickable_layout_dark_background);
                switchAccountTitle.setTextColor(getResources().getColor(R.color.LightText));
                disconnectAccountTitle.setTextColor(getResources().getColor(R.color.LightText));
            }
        }
    }

    private void checkAccountAvailable() {
        if(firebaseHelper.isUserSignedIn()){
            setupAccountEnabled();
        } else {
            setupAccountDisabled();
        }
    }
    private void setupAccountDisabled(){
        if(tickTrackDatabase.getThemeMode()==1){
            syncDataLayout.setBackgroundResource(R.drawable.clickable_layout_light_background);
            includeDataLayout.setBackgroundResource(R.drawable.clickable_layout_light_background);

            syncDataTitle.setTextColor(getResources().getColor(R.color.roboto_calendar_current_day_ring));
            syncDataFrequency.setTextColor(getResources().getColor(R.color.roboto_calendar_current_day_ring));
            syncDataLastSync.setTextColor(getResources().getColor(R.color.roboto_calendar_current_day_ring));
            includeDataTitle.setTextColor(getResources().getColor(R.color.roboto_calendar_current_day_ring));
            includeDataValue.setTextColor(getResources().getColor(R.color.roboto_calendar_current_day_ring));
        } else {
            syncDataLayout.setBackgroundResource(R.drawable.clickable_layout_dark_background);
            includeDataLayout.setBackgroundResource(R.drawable.clickable_layout_dark_background);

            syncDataTitle.setTextColor(getResources().getColor(R.color.roboto_calendar_day_of_the_week_font));
            syncDataFrequency.setTextColor(getResources().getColor(R.color.roboto_calendar_day_of_the_week_font));
            syncDataLastSync.setTextColor(getResources().getColor(R.color.roboto_calendar_day_of_the_week_font));
            includeDataTitle.setTextColor(getResources().getColor(R.color.roboto_calendar_day_of_the_week_font));
            includeDataValue.setTextColor(getResources().getColor(R.color.roboto_calendar_day_of_the_week_font));
        }
        syncDataLayout.setOnClickListener(view -> {
            Toast.makeText(activity, "Sign in required", Toast.LENGTH_SHORT).show();
        });
        includeDataLayout.setOnClickListener(view -> {
            Toast.makeText(activity, "Sign in required", Toast.LENGTH_SHORT).show();
        });
    }
    private void setupAccountEnabled(){
        if(tickTrackDatabase.getThemeMode()==1){
            syncDataLayout.setBackgroundResource(R.drawable.clickable_layout_light_background);
            includeDataLayout.setBackgroundResource(R.drawable.clickable_layout_light_background);

            syncDataTitle.setTextColor(getResources().getColor(R.color.DarkText));
            syncDataFrequency.setTextColor(getResources().getColor(R.color.LightDarkText));
            syncDataLastSync.setTextColor(getResources().getColor(R.color.LightDarkText));
            includeDataTitle.setTextColor(getResources().getColor(R.color.DarkText));
            includeDataValue.setTextColor(getResources().getColor(R.color.LightDarkText));
        } else {
            syncDataLayout.setBackgroundResource(R.drawable.clickable_layout_dark_background);
            includeDataLayout.setBackgroundResource(R.drawable.clickable_layout_dark_background);

            syncDataTitle.setTextColor(getResources().getColor(R.color.LightText));
            syncDataFrequency.setTextColor(getResources().getColor(R.color.DarkLightText));
            syncDataLastSync.setTextColor(getResources().getColor(R.color.DarkLightText));
            includeDataTitle.setTextColor(getResources().getColor(R.color.LightText));
            includeDataValue.setTextColor(getResources().getColor(R.color.DarkLightText));
        }
        syncDataLayout.setOnClickListener(view -> toggleSyncOptionsLayout());
        includeDataLayout.setOnClickListener(view -> toggleDataOptionsLayout());
    }

    private void setupLastBackupText() {
        if(tickTrackDatabase.getLastBackupSystemTime()!=-1){
            syncDataLastSync.setText("Last sync "+ TimeAgo.getTimeAgo(tickTrackDatabase.getLastBackupSystemTime()));
        } else {
            syncDataLastSync.setText("No sync yet");
        }
    }
    private void setupSyncFreqOptionText(){
        String frequencyOption = "";
        int opt = tickTrackDatabase.getSyncFrequency();
        if(opt==1){
            frequencyOption = "Monthly";
            monthlyButton.setChecked(true);
        } else if (opt ==2){
            frequencyOption = "Weekly";
            weeklyButton.setChecked(true);
        } else if (opt==3){
            frequencyOption = "Daily";
            dailyButton.setChecked(true);
        } else {
            frequencyOption = "Monthly";
            monthlyButton.setChecked(true);
        }
        int backupHour = tickTrackDatabase.getBackupHour();
        int backupMinute = tickTrackDatabase.getBackupMinute();
        String timeOfBackup = " at around ";
        if(backupHour<=12){
            if(backupHour==0){
                timeOfBackup += String.format(Locale.getDefault(),"%02d:%02d am", backupHour,backupMinute);
            } else if(backupHour==12){
                timeOfBackup += String.format(Locale.getDefault(),"%02d:%02d pm", backupHour,backupMinute);
            } else{
                timeOfBackup += String.format(Locale.getDefault(),"%02d:%02d am", backupHour,backupMinute);
            }
        } else {
            backupHour = backupHour-12;
            timeOfBackup += String.format(Locale.getDefault(),"%02d:%02d pm",backupHour,backupMinute);
        }

        syncDataFrequency.setText(frequencyOption+timeOfBackup);
    }
    private void setupDataOptionsText(){
        ArrayList<Integer> optionsBackup = tickTrackDatabase.getBackupDataOptions();
        String options = "";
        if(optionsBackup.contains(1)){
            options += "Preferences";
        }
        if(optionsBackup.contains(2)){
            timerCheckBox.setChecked(true);
            options += ", Timers";
        }
        if(optionsBackup.contains(3)){
            counterCheckBox.setChecked(true);
            options += ", Counters";
        }
        includeDataValue.setText(options);
    }
    private void setupThemeText() {
        int themeMode = tickTrackDatabase.getThemeMode();
        if(themeMode==1){
            themeName.setText("Light Mode");
            lightButton.setChecked(true);
        } else {
            themeName.setText("Dark Mode");
            darkButton.setChecked(true);
        }
    }
    private void refreshTheme() {
        TickTrackThemeSetter.settingsActivityTheme(activity, themeTitle, themeName, settingsScrollView, themeLayout,
                tickTrackDatabase, backupGoogleTitle, googleAccountLayout, switchAccountOptionLayout, disconnectAccountOptionLayout, switchAccountTitle, disconnectAccountTitle,
                counterCheckBox, timerCheckBox, monthlyButton, weeklyButton, dailyButton, syncFreqOptionsLayout, darkButton, lightButton, themeOptionsLayout,
                hapticLayout, hapticTextTitle, deleteBackupLayout, factoryResetLayout, rateUsLayout, displaySumLayout, timerSoundLayout, clockStyleLayout, clockOptionsLayout, dateTimeLayout,
                rateUsTitle, rateUsValue, displaySumTitle, timerSoundTitle, timerSoundValue, clockStyleTitle, clockStyleValue, dateTimeTitle, dateTimeValue, toolbar, milestoneVibrateLayout, milestoneSoundLayout,
                vibrateMilestoneTitle, milestoneSoundTitle, milestoneSoundValue, autostartLayout, autostartTitle, autostartValue, retrySyncLayoutAccount, notificationsLayout, notificationsTitle, notificationValue);
        updateWidgets();
        checkAccountAvailable();
        setupEmailText();

    }

    private void updateWidgets() {
        Intent intent1 = new Intent(activity, CreateTimerWidget.class);
        intent1.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        int[] ids1 = AppWidgetManager.getInstance(activity.getApplication()).getAppWidgetIds(new ComponentName(activity.getApplication(), CreateTimerWidget.class));
        intent1.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids1);
        activity.sendBroadcast(intent1);
        Intent intent2 = new Intent(activity, QuickTimerWidget.class);
        intent2.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        int[] ids2 = AppWidgetManager.getInstance(activity.getApplication()).getAppWidgetIds(new ComponentName(activity.getApplication(), QuickTimerWidget.class));
        intent2.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids2);
        activity.sendBroadcast(intent2);
        Intent intent3 = new Intent(activity, ScreensaverWidget.class);
        intent3.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        int[] ids3 = AppWidgetManager.getInstance(activity.getApplication()).getAppWidgetIds(new ComponentName(activity.getApplication(), ScreensaverWidget.class));
        intent3.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids3);
        activity.sendBroadcast(intent3);
        Intent intent4 = new Intent(activity, StopwatchWidget.class);
        intent4.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        int[] ids4 = AppWidgetManager.getInstance(activity.getApplication()).getAppWidgetIds(new ComponentName(activity.getApplication(), StopwatchWidget.class));
        intent4.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids4);
        activity.sendBroadcast(intent4);
    }

    private boolean isSyncOptionOpen = false;
    private void toggleSyncOptionsLayout() {
        if(isSyncOptionOpen){
            syncFreqOptionsLayout.setVisibility(View.GONE);
            isSyncOptionOpen = false;
        } else {
            toggleOthersClose();
            syncFreqOptionsLayout.setVisibility(View.VISIBLE);
            isSyncOptionOpen = true;
        }
    }

    private boolean isAccountOptionsOpen = false;
    private void toggleGoogleAccountOptionsLayout() {
        if(isAccountOptionsOpen){
            accountOptionsLayout.setVisibility(View.GONE);
            setupEmailText();
            isAccountOptionsOpen = false;
        } else {
            toggleOthersClose();
            accountOptionsLayout.setVisibility(View.VISIBLE);
            isAccountOptionsOpen = true;
        }
    }

    private boolean isDataOptionsOpen = false;
    private void toggleDataOptionsLayout() {
        if(isDataOptionsOpen){
            backupDataOptionsLayout.setVisibility(View.GONE);
            setupDataOptionsText();
            isDataOptionsOpen = false;
        } else {
            toggleOthersClose();
            backupDataOptionsLayout.setVisibility(View.VISIBLE);
            setupDataOptionsText();
            isDataOptionsOpen = true;
        }
    }

    private boolean isThemeOptionsOpen = false;
    private void toggleThemeOptionsLayout(){
        removeAllCheck();
        if(isThemeOptionsOpen){
            themeOptionsLayout.setVisibility(View.GONE);
            isThemeOptionsOpen = false;
        } else {
            toggleOthersClose();
            themeOptionsLayout.setVisibility(View.VISIBLE);
            isThemeOptionsOpen = true;
        }
    }

    private void toggleOthersClose() {

        if(isDataOptionsOpen){
            toggleDataOptionsLayout();
        }
        if(isSyncOptionOpen){
            toggleSyncOptionsLayout();
        }
        if(isAccountOptionsOpen){
            toggleGoogleAccountOptionsLayout();
        }
        if(isClockVisible){
            setupClockOptionsToggle();
        }
        if(isThemeOptionsOpen){
            toggleThemeOptionsLayout();
        }
    }

    private void setupHapticData() {
        if(tickTrackDatabase.isHapticEnabled()){
            hapticSwitch.setChecked(true);
        } else {
            hapticSwitch.setChecked(false);
        }
    }

    private int clockStyle = -1;
    private boolean isClockVisible = false;
    private void setupClockStyle() {
        if(clockStyle==-1){
            clockStyle = tickTrackDatabase.getScreenSaverClock();
        }
        removeAllCheck();
        if(clockStyle==1){
            unordinaryImageCheck.setVisibility(View.VISIBLE);
            clockStyleValue.setText("Unordinary");
        } else if (clockStyle==2){
            oxygenyImageCheck.setVisibility(View.VISIBLE);
            clockStyleValue.setText("Oxygeny");
        } else if (clockStyle==3){
            minimalImageCheck.setVisibility(View.VISIBLE);
            clockStyleValue.setText("Truly Minimal");
        } else if (clockStyle==4){
            simplisticImageCheck.setVisibility(View.VISIBLE);
            clockStyleValue.setText("Simplistic");
        } else if (clockStyle==5){
            romanImageCheck.setVisibility(View.VISIBLE);
            clockStyleValue.setText("Roman");
        } else if (clockStyle==6){
            funkyImageCheck.setVisibility(View.VISIBLE);
            clockStyleValue.setText("Funky");
        } else {
            unordinaryImageCheck.setVisibility(View.VISIBLE);
            clockStyleValue.setText("Unordinary");
        }
        tickTrackDatabase.storeScreenSaverClock(clockStyle);
    }
    private void removeAllCheck() {
        unordinaryImageCheck.setVisibility(View.INVISIBLE);
        oxygenyImageCheck.setVisibility(View.INVISIBLE);
        minimalImageCheck.setVisibility(View.INVISIBLE);
        simplisticImageCheck.setVisibility(View.INVISIBLE);
        romanImageCheck.setVisibility(View.INVISIBLE);
        funkyImageCheck.setVisibility(View.INVISIBLE);
    }
    private void setOnClickListeners(){
        unordinaryImage.setOnClickListener(view -> {
            clockStyle=1;
            setupClockStyle();
            setupSettingsChangeTime();
        });
        oxygenyImage.setOnClickListener(view -> {
            clockStyle=2;
            setupClockStyle();
            setupSettingsChangeTime();
        });
        minimalImage.setOnClickListener(view -> {
            clockStyle=3;
            setupClockStyle();
            setupSettingsChangeTime();
        });
        simplisticImage.setOnClickListener(view -> {
            clockStyle=4;
            setupClockStyle();
            setupSettingsChangeTime();
        });
        romanImage.setOnClickListener(view -> {
            clockStyle=5;
            setupClockStyle();
            setupSettingsChangeTime();
        });
        funkyImage.setOnClickListener(view -> {
            clockStyle=6;
            setupClockStyle();
            setupSettingsChangeTime();
        });
    }
    private void setupClockOptionsToggle() {
        if(isClockVisible){
            clockOptionsLayout.setVisibility(View.GONE);
            isClockVisible=false;
        } else {
            toggleOthersClose();
            clockOptionsLayout.setVisibility(View.VISIBLE);
            isClockVisible=true;
        }
    }

    private void setupTimerSound() {
        timerSoundValue.setText(tickTrackDatabase.getRingtoneName());
    }

    private void setupCounterSum() {
        if(tickTrackDatabase.isSumEnabled()){
            displaySumSwitch.setChecked(true);
        } else {
            displaySumSwitch.setChecked(false);
        }
    }

    private void initVariables() {
        themeLayout = findViewById(R.id.themeSettingsLayout);
        toolbar = findViewById(R.id.settingsToolbar);
        themeTitle = findViewById(R.id.themeSettingsLabel);
        themeName = findViewById(R.id.themeValueSettingsTextView);
        backButton = findViewById(R.id.settingsActivityBackButton);
        settingsScrollView = findViewById(R.id.settingsActivityScrollView);
        googleAccountLayout = findViewById(R.id.backupAccountLayout);
        backupGoogleTitle = findViewById(R.id.backupTitleSettingsTextView);
        backupEmail = findViewById(R.id.backupEmailSettingsTextView);
        accountOptionsLayout = findViewById(R.id.backupAccountOptionsLayout);
        switchAccountOptionLayout = findViewById(R.id.backupSettingsSwitchAccountLayout);
        disconnectAccountOptionLayout = findViewById(R.id.backupSettingsDisconnectAccountLayout);
        syncDataLayout = findViewById(R.id.backupFrequencyLayout);
        includeDataLayout = findViewById(R.id.backupDataLayout);
        switchAccountTitle = findViewById(R.id.backupAccountSwitchTitleText);
        disconnectAccountTitle = findViewById(R.id.backupAccountDisconnectTitleText);
        syncDataTitle = findViewById(R.id.backupFrequencyTitleSettingsTextView);
        syncDataFrequency = findViewById(R.id.backupFrequencyValueSettingsTextView);
        syncDataLastSync = findViewById(R.id.backupLastBackupValueSettingsTextView);
        includeDataTitle = findViewById(R.id.backupDataTitleSettingsTextView);
        includeDataValue = findViewById(R.id.backupDataSummarySettingsTextView);
        backupDataOptionsLayout = findViewById(R.id.backupDataOptionsLayout);
        counterCheckBox = findViewById(R.id.backupCounterDataOptionSettingsCheckbox);
        timerCheckBox = findViewById(R.id.backupTimerDataOptionSettingsCheckbox);
        syncFreqOptionsLayout = findViewById(R.id.backupFrequencySettingsOptionsLayout);
        monthlyButton = findViewById(R.id.syncDataSettingsTickTrackMonthlyRadio);
        weeklyButton = findViewById(R.id.syncDataSettingsTickTrackWeeklyRadio);
        dailyButton = findViewById(R.id.syncDataSettingsTickTrackDailyRadio);
        darkButton = findViewById(R.id.darkRadioButtonSettingsActivity);
        lightButton = findViewById(R.id.lightRadioButtonSettingsActivity);
        themeOptionsLayout = findViewById(R.id.themeSettingsOptionsLayout);
        hapticSwitch = findViewById(R.id.hapticVibrateSettingsSwitch);
        hapticLayout = findViewById(R.id.hapticVibrateSettingsLayout);
        hapticTextTitle = findViewById(R.id.hapticTitleSettingsTextView);
        lottieAnimationView = findViewById(R.id.settingsActivityLottieAnim);
        lottieAnimationView.setVisibility(View.INVISIBLE);
        generalRootLayout = findViewById(R.id.generalRootLayout);

        rateUsLayout = findViewById(R.id.rateUsSettingsLayout);
        displaySumLayout = findViewById(R.id.showSumCounterSettingsLayout);
        timerSoundLayout = findViewById(R.id.timerSoundSettingsLayout);
        clockStyleLayout = findViewById(R.id.clockStyleScreensaverSettingsLayout);
        clockOptionsLayout = findViewById(R.id.clockStyleScreensaverSettingsOptionsLayout);
        dateTimeLayout = findViewById(R.id.dateTimeSettingsLayout);
        rateUsTitle = findViewById(R.id.rateUsSettingsTitleText);
        rateUsValue = findViewById(R.id.rateUsSettingsContentText);
        displaySumTitle = findViewById(R.id.showSumCounterSettingsTitle);
        timerSoundTitle = findViewById(R.id.timerSoundSettingsTitle);
        timerSoundValue = findViewById(R.id.timerSoundSettingsValue);
        clockStyleTitle = findViewById(R.id.clockStyleScreensaverSettingsTitle);
        clockStyleValue = findViewById(R.id.clockStyleScreensaverSettingsValue);
        dateTimeTitle = findViewById(R.id.dateTimeSettingsTitle);
        dateTimeValue = findViewById(R.id.dateTimeSettingsValue);
        unordinaryImage = findViewById(R.id.screensaverUnordinaryImage);
        oxygenyImage = findViewById(R.id.screensaverOxygenyImage);
        minimalImage = findViewById(R.id.screensaverTrulyMinimalImage);
        simplisticImage = findViewById(R.id.screensaverSimplisticImage);
        romanImage = findViewById(R.id.screensaverRomanImage);
        funkyImage = findViewById(R.id.screensaverFunkyImage);
        unordinaryImageCheck = findViewById(R.id.screensaverUnordinaryImageCheck);
        oxygenyImageCheck = findViewById(R.id.screensaverOxygenyImageCheck);
        minimalImageCheck = findViewById(R.id.screensaverTrulyMinimalImageCheck);
        simplisticImageCheck = findViewById(R.id.screensaverSimplisticImageCheck);
        romanImageCheck = findViewById(R.id.screensaverRomanImageCheck);
        funkyImageCheck = findViewById(R.id.screensaverFunkyImageCheck);
        displaySumSwitch = findViewById(R.id.showSumCounterSettingsSwitch);
        vibrateMilestoneTitle = findViewById(R.id.settingsMilestoneVibrateTitle);
        milestoneSwitch = findViewById(R.id.settingsMilestoneVibrateSwitch);
        milestoneSoundTitle = findViewById(R.id.settingsMilestoneSoundTitle);
        milestoneSoundValue = findViewById(R.id.settingsMilestoneSoundValue);
        milestoneSoundLayout = findViewById(R.id.milestoneSoundSettingsLayout);
        milestoneVibrateLayout = findViewById(R.id.milestoneVibrateSettingsLayout);
        deleteBackupLayout = findViewById(R.id.dangerZoneDeleteBackupLayout);
        factoryResetLayout = findViewById(R.id.dangerZoneFactoryResetLayout);
        notificationsLayout = findViewById(R.id.notificationsSettingsLayout);
        notificationsTitle = findViewById(R.id.notificationsSettingsTitle);
        notificationValue = findViewById(R.id.notificationsSettingsValue);

        autostartLayout = findViewById(R.id.autoStartSettingsLayout);
        autostartTitle = findViewById(R.id.autoStartSettingsTitle);
        autostartValue = findViewById(R.id.autoStartSettingsValue);

        retrySyncLayoutAccount = findViewById(R.id.retrySyncLayoutAccount);

        if(AutoStartPermissionHelper.getInstance().isAutoStartPermissionMaybeAvailable(this)){
            autostartLayout.setVisibility(View.VISIBLE);
            autostartLayout.setOnClickListener(view -> {
                boolean resultCode = AutoStartPermissionHelper.getInstance().getAutoStartPermission(this);
                if(!resultCode){
                    if(AutoStartPermissionHelper.getInstance().isAutoStartPermissionMaybeAvailable(this)){
                        Intent intent = new Intent();
                        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", activity.getPackageName(), null);
                        intent.setData(uri);
                        startActivityForResult(intent, 298);
                    } else {
                        Snackbar snackbar = Snackbar
                                .make(generalRootLayout, "No Autostart feature detected on your device", Snackbar.LENGTH_LONG)
                                .setBackgroundTint(activity.getResources().getColor(R.color.Accent))
                                .setAnimationMode(Snackbar.ANIMATION_MODE_SLIDE)
                                .setDuration(1000);
                        snackbar.show();
                    }
                }
            });
        } else {
            autostartLayout.setVisibility(View.GONE);
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_NOTIFICATION_POLICY) == PackageManager.PERMISSION_GRANTED){
            notificationsLayout.setVisibility(View.GONE);
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                notificationsLayout.setVisibility(View.VISIBLE);
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                notificationsLayout.setVisibility(View.VISIBLE);
            }
        }

        activity = this;
        firebaseHelper = new FirebaseHelper(activity);
        firebaseHelper.setAction(StartUpActivity.ACTION_SETTINGS_ACCOUNT_ADD);
        tickTrackDatabase = new TickTrackDatabase(activity);
        tickTrackFirebaseDatabase = new TickTrackFirebaseDatabase(activity);
        sharedPreferences = tickTrackDatabase.getSharedPref(this);
        prevFragment = tickTrackDatabase.retrieveCurrentFragmentNumber();

        setupEmailText();
        setupLastBackupText();
        checkAccountAvailable();
        setupDataOptionsText();
        setupSyncFreqOptionText();
        setupThemeText();
        setupHapticData();
        setupMilestoneVibrate();
        setupMilestoneSound();
        setupClockStyle();
        setOnClickListeners();

        setupTimerSound();

        setupCounterSum();

        setupRetrySync();

        new Handler(Looper.getMainLooper()).post(() -> {
            accountOptionsLayout.setVisibility(View.GONE);
            clockOptionsLayout.setVisibility(View.GONE);
            themeOptionsLayout.setVisibility(View.GONE);
        });
    }

    private void setupRetrySync() {
        if(tickTrackFirebaseDatabase.isDriveLinkFail()){
            retrySyncLayoutAccount.setVisibility(View.VISIBLE);
            retrySyncLayoutAccount.setOnClickListener(view -> {
                Toast.makeText(activity, "Sync In Progress", Toast.LENGTH_SHORT).show();
                tickTrackFirebaseDatabase.cancelBackUpAlarm();
                tickTrackFirebaseDatabase.setBackUpAlarm(true);
                retrySyncLayoutAccount.setVisibility(View.GONE);
                tickTrackFirebaseDatabase.setDriveLinkFail(false);
                AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                Intent intent = new Intent(BackupScheduleReceiver.START_BACKUP_SCHEDULE);
                intent.setClassName("com.theflopguyproductions.ticktrack", "com.theflopguyproductions.ticktrack.receivers.BackupScheduleReceiver");
                intent.setPackage("com.theflopguyproductions.ticktrack");
                PendingIntent alarmPendingIntent = null;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
                    alarmPendingIntent = PendingIntent.getBroadcast(this, 825417, intent, PendingIntent.FLAG_MUTABLE);
                } else {
                    alarmPendingIntent = PendingIntent.getBroadcast(this, 825417, intent, 0);
                }
                alarmManager.cancel(alarmPendingIntent);
            });
        } else {
            retrySyncLayoutAccount.setVisibility(View.GONE);
        }
    }

    private void setupMilestoneSound() {
        milestoneSoundValue.setText(tickTrackDatabase.getMilestoneSound());
    }

    private boolean isMilestone = true;
    private void setupMilestoneVibrate() {
        if(tickTrackDatabase.isMilestoneVibrate()){
            milestoneSwitch.setChecked(true);
            isMilestone = true;
        } else {
            milestoneSwitch.setChecked(false);
            isMilestone = false;
        }
    }
    private void toggleMilestoneVibrate(){
        if(isMilestone){
            tickTrackDatabase.setMilestoneVibrate(false);
            milestoneSwitch.setChecked(false);
            isMilestone = false;
        } else {
            tickTrackDatabase.setMilestoneVibrate(true);
            milestoneSwitch.setChecked(true);
            isMilestone = true;
        }
    }

    private String receivedAction = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        initVariables();
        setupClickListeners();
        receivedAction = getIntent().getAction();

        if(tickTrackDatabase.getThemeMode()==1){
            getWindow().setNavigationBarColor(getResources().getColor(R.color.HoloLightGray) );
        } else {
            getWindow().setNavigationBarColor(getResources().getColor(R.color.HoloBlack) );
        }
    }

    private void toggleHapticEnable() {
        tickTrackDatabase.setHapticEnabled(!tickTrackDatabase.isHapticEnabled());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (101 == requestCode) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                readDataExternal(0);
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    if (PermissionUtils.neverAskAgainSelected(this, Manifest.permission.READ_MEDIA_AUDIO)) {
                        displayNeverAskAgainDialog();
                    }
                } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (PermissionUtils.neverAskAgainSelected(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                        displayNeverAskAgainDialog();
                    }
                }
            }
        } else if (102 == requestCode) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                readDataExternal(1);
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    if (PermissionUtils.neverAskAgainSelected(this, Manifest.permission.READ_MEDIA_AUDIO)) {
                        displayNeverAskAgainDialog();
                    }
                } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (PermissionUtils.neverAskAgainSelected(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                        displayNeverAskAgainDialog();
                    }
                }
            }
        }
    }

    private void readDataExternal(int requestCode) {
        final Intent ringtone = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
        if(requestCode==0){
            ringtone.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_ALARM);
        } else {
            ringtone.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_NOTIFICATION);
        }
        ringtone.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_DEFAULT, true);
        ringtone.putExtra(RingtoneManager.EXTRA_RINGTONE_DEFAULT_URI,
                RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
        startActivityForResult(ringtone, requestCode);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0 && resultCode == RESULT_OK) {
            assert data != null;
            final Uri uri = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
            Ringtone ringtone = RingtoneManager.getRingtone(this, uri);
            String title = ringtone.getTitle(this);
            if(uri!=null){
                tickTrackDatabase.storeRingtoneName(title);
                tickTrackDatabase.storeRingtoneURI(uri.toString());
            } else {
                tickTrackDatabase.storeRingtoneURI(null);
                tickTrackDatabase.storeRingtoneName("Default Ringtone");
            }
            setupTimerSound();
        } else if (requestCode == 1 && resultCode == RESULT_OK) {
            assert data != null;
            final Uri uri = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
            Ringtone ringtone = RingtoneManager.getRingtone(this, uri);
            String title = ringtone.getTitle(this);
            if(uri!=null){
                tickTrackDatabase.setMilestoneSound(title);
                tickTrackDatabase.setMilestoneSoundUri(uri.toString());
            } else {
                tickTrackDatabase.setMilestoneSoundUri(null);
                tickTrackDatabase.setMilestoneSound("Default Sound");
            }
            setupMilestoneSound();
        }
    }

    private void displayNeverAskAgainDialog() {
        DeleteTimer deleteTimer = new DeleteTimer(activity);
        deleteTimer.show();
        deleteTimer.yesButton.setText("Grant Permission");
        deleteTimer.dialogTitle.setText("Uh oh! Permission needed");
        deleteTimer.dialogMessage.setText("We need this permission to set custom timer sound");
        deleteTimer.yesButton.setOnClickListener(view -> {
            deleteTimer.dismiss();
            Intent intent = new Intent();
            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            Uri uri = Uri.fromParts("package", getPackageName(), null);
            intent.setData(uri);
            startActivity(intent);
        });
        deleteTimer.noButton.setOnClickListener(view -> deleteTimer.dismiss());
    }

    private void setupClickListeners() {

        notificationsLayout.setOnClickListener(v -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", getPackageName(), null);
                intent.setData(uri);
                startActivity(intent);
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", getPackageName(), null);
                intent.setData(uri);
                startActivity(intent);
            }
        });

        milestoneSoundLayout.setOnClickListener(view -> {
            int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
                    if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_MEDIA_AUDIO}, 102);
                    }
                } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 102);
                    }
                }
            } else {
                readDataExternal(1);
            }
            setupSettingsChangeTime();
        });

        milestoneVibrateLayout.setOnClickListener(view ->{
            toggleMilestoneVibrate();
            setupSettingsChangeTime();
        });
        milestoneSwitch.setOnCheckedChangeListener((compoundButton, b) -> {
            if(compoundButton.isChecked()){
                tickTrackDatabase.setMilestoneVibrate(true);
                isMilestone = true;
            } else {
                tickTrackDatabase.setMilestoneVibrate(false);
                isMilestone = false;
            }
            setupSettingsChangeTime();
        });

        rateUsLayout.setOnClickListener(view -> {
            RateUsUtil rateUsUtil = new RateUsUtil(this);
            rateUsUtil.rateApp();
        });

        timerSoundLayout.setOnClickListener(view -> {
            int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
                    if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_MEDIA_AUDIO}, 101);
                    }
                } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 101);
                    }
                }
            } else {
                readDataExternal(0);
            }
            setupSettingsChangeTime();
        });

        monthlyButton.setOnClickListener((view) -> {
            tickTrackDatabase.storeSyncFrequency(SettingsData.Frequency.MONTHLY.getCode());
            tickTrackFirebaseDatabase.cancelBackUpAlarm();
            tickTrackFirebaseDatabase.setBackUpAlarm(false);
            toggleSyncOptionsLayout();
            setupSettingsChangeTime();
        });
        weeklyButton.setOnClickListener((view) -> {
            tickTrackDatabase.storeSyncFrequency(SettingsData.Frequency.WEEKLY.getCode());
            tickTrackFirebaseDatabase.cancelBackUpAlarm();
            tickTrackFirebaseDatabase.setBackUpAlarm(false);
            toggleSyncOptionsLayout();
            setupSettingsChangeTime();
        });
        dailyButton.setOnClickListener((view) -> {
            tickTrackDatabase.storeSyncFrequency(SettingsData.Frequency.DAILY.getCode());
            tickTrackFirebaseDatabase.cancelBackUpAlarm();
            tickTrackFirebaseDatabase.setBackUpAlarm(false);
            toggleSyncOptionsLayout();
            setupSettingsChangeTime();
        });

        darkButton.setOnClickListener((view) -> {

            sharedPreferences = tickTrackDatabase.getSharedPref(this);
            sharedPreferences.unregisterOnSharedPreferenceChangeListener(sharedPreferenceChangeListener);
            tickTrackDatabase.setThemeMode(SettingsData.Theme.DARK.getCode());
            toggleThemeOptionsLayout();

            lottieAnimationView.setAnimation(R.raw.dark_theme_in_anim);
            lottieAnimationView.setProgress(0);
            lottieAnimationView.setVisibility(View.VISIBLE);
            lottieAnimationView.playAnimation();

            lottieAnimationView.addAnimatorListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) { }
                @Override
                public void onAnimationEnd(Animator animator) {
                    refreshTheme();
                    lottieAnimationView.removeAllAnimatorListeners();
                    generalRootLayout.setVisibility(View.INVISIBLE);
                    lottieAnimationView.setAnimation(R.raw.dark_theme_out_anim);
                    lottieAnimationView.setProgress(0);
                    lottieAnimationView.playAnimation();
                    generalRootLayout.setVisibility(View.VISIBLE);
                    lottieAnimationView.addAnimatorListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animator) {}
                        @Override
                        public void onAnimationEnd(Animator animator) {
                            lottieAnimationView.removeAllAnimatorListeners();
                            lottieAnimationView.setVisibility(View.INVISIBLE);
                            sharedPreferences.registerOnSharedPreferenceChangeListener(sharedPreferenceChangeListener);
                        }
                        @Override
                        public void onAnimationCancel(Animator animator) { }
                        @Override
                        public void onAnimationRepeat(Animator animator) { }
                    });
                }
                @Override
                public void onAnimationCancel(Animator animator) { }
                @Override
                public void onAnimationRepeat(Animator animator) { }
            });
            setupSettingsChangeTime();
        });
        lightButton.setOnClickListener((view) -> {

            sharedPreferences = tickTrackDatabase.getSharedPref(this);
            sharedPreferences.unregisterOnSharedPreferenceChangeListener(sharedPreferenceChangeListener);
            tickTrackDatabase.setThemeMode(SettingsData.Theme.LIGHT.getCode());
            toggleThemeOptionsLayout();
            lottieAnimationView.setAnimation(R.raw.light_theme_in_anim);
            lottieAnimationView.setProgress(0);
            lottieAnimationView.setVisibility(View.VISIBLE);
            lottieAnimationView.playAnimation();
            lottieAnimationView.addAnimatorListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {}
                @Override
                public void onAnimationEnd(Animator animator) {
                    refreshTheme();
                    lottieAnimationView.removeAllAnimatorListeners();
                    generalRootLayout.setVisibility(View.INVISIBLE);
                    lottieAnimationView.setAnimation(R.raw.light_theme_out_anim);
                    lottieAnimationView.setProgress(0);
                    lottieAnimationView.playAnimation();
                    generalRootLayout.setVisibility(View.VISIBLE);
                    lottieAnimationView.addAnimatorListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animator) { }
                        @Override
                        public void onAnimationEnd(Animator animator) {
                            lottieAnimationView.removeAllAnimatorListeners();
                            lottieAnimationView.setVisibility(View.INVISIBLE);
                            sharedPreferences.registerOnSharedPreferenceChangeListener(sharedPreferenceChangeListener);
                        }
                        @Override
                        public void onAnimationCancel(Animator animator) { }
                        @Override
                        public void onAnimationRepeat(Animator animator) { }
                    });
                }
                @Override
                public void onAnimationCancel(Animator animator) { }
                @Override
                public void onAnimationRepeat(Animator animator) { }
            });
            setupSettingsChangeTime();
        });

        themeLayout.setOnClickListener(view -> {
            toggleThemeOptionsLayout();
            setupSettingsChangeTime();
        });

        googleAccountLayout.setOnClickListener(view -> {
            if(firebaseHelper.isUserSignedIn() && !tickTrackFirebaseDatabase.isDriveLinkFail()){
                toggleGoogleAccountOptionsLayout();
            } else {
                firebaseHelper.signOut(activity);
                backupEmail.setText("...");
                tickTrackDatabase.storeStartUpFragmentID(2);
                Intent startUpSignInIntent = new Intent(this, StartUpActivity.class);
                startUpSignInIntent.setAction(StartUpActivity.ACTION_SETTINGS_ACCOUNT_ADD);
                startActivity(startUpSignInIntent);
            }
        });

        switchAccountOptionLayout.setOnClickListener(view -> {
            if(isMyServiceRunning(getApplicationContext())){
                Toast.makeText(activity, "Backup ongoing, please wait", Toast.LENGTH_SHORT).show();
            } else {
                if(firebaseHelper.isUserSignedIn()){
                    firebaseHelper.switchAccount(activity);
                    toggleGoogleAccountOptionsLayout();
                }
            }
        });

        disconnectAccountOptionLayout.setOnClickListener(view -> {
            if(isMyServiceRunning(this)){
                Toast.makeText(activity, "Backup ongoing, please wait", Toast.LENGTH_SHORT).show();
            } else {
                if(firebaseHelper.isUserSignedIn()){
                    firebaseHelper.signOut(activity);
                    toggleGoogleAccountOptionsLayout();
                    setupEmailText();
                    Toast.makeText(activity, "Signed out", Toast.LENGTH_SHORT).show();
                }
            }
        });

        counterCheckBox.setOnCheckedChangeListener((compoundButton, b) -> {
            if(compoundButton.isChecked()){
                tickTrackDatabase.setCounterDataBackup(true);
            } else {
                tickTrackDatabase.setCounterDataBackup(false);
            }
            setupSettingsChangeTime();
        });
        timerCheckBox.setOnCheckedChangeListener((compoundButton, b) -> {
            if(compoundButton.isChecked()){
                tickTrackDatabase.setTimerDataBackup(true);
            } else {
                tickTrackDatabase.setTimerDataBackup(false);
            }
            setupSettingsChangeTime();
        });

        hapticLayout.setOnClickListener(view -> {
            setupSettingsChangeTime();
            toggleHapticEnable();
        });
        hapticSwitch.setOnCheckedChangeListener((compoundButton, b) -> {
            if(compoundButton.isChecked()){
                tickTrackDatabase.setHapticEnabled(true);
            } else {
                tickTrackDatabase.setHapticEnabled(false);
            }
            setupSettingsChangeTime();
        });

        deleteBackupLayout.setOnClickListener(view -> {
            SwipeDialog swipeDialog = new SwipeDialog(activity);
            swipeDialog.show();
            swipeDialog.dialogTitle.setText("Are you sure?");
            swipeDialog.dialogMessage.setText("This will permanently delete your TickTrack cloud backup");
            swipeDialog.swipeButton.setOnClickListener(v -> {
                swipeDialog.dismiss();
                if(isMyServiceRunning(this)){
                    Toast.makeText(activity, "Backup ongoing, please wait", Toast.LENGTH_SHORT).show();
                } else {
                    if(firebaseHelper.isUserSignedIn()){
                        firebaseHelper.deleteBackup(this);
                        Toast.makeText(activity, "Deleting in background", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            swipeDialog.dismissButton.setOnClickListener(view1 -> swipeDialog.dismiss());
        });
        factoryResetLayout.setOnClickListener(view -> {
            setupSettingsChangeTime();
            SwipeDialog swipeDialog = new SwipeDialog(activity);
            swipeDialog.show();
            swipeDialog.dialogTitle.setText("Are you sure?");
            swipeDialog.dialogMessage.setText("This will permanently delete your device TickTrack Data");
            swipeDialog.swipeButton.setOnClickListener(v -> {
                swipeDialog.dismiss();
                if(isMyServiceRunning(this)){
                    Toast.makeText(activity, "Backup ongoing, please wait", Toast.LENGTH_SHORT).show();
                } else {
                    new Handler().post(() -> {
                        ProgressBarDialog progressBarDialog = new ProgressBarDialog(activity);
                        progressBarDialog.show();
                        progressBarDialog.setContentText("Resetting TickTrack");
                        progressBarDialog.titleText.setVisibility(View.GONE);
                        tickTrackDatabase.resetData(activity, this);
                        progressBarDialog.dismiss();
                        refreshTheme();
                        clockStyle=-1;
                        setupClockStyle();
                        Toast.makeText(activity, "Reset Complete", Toast.LENGTH_SHORT).show();
                    });
                }
            });
            swipeDialog.dismissButton.setOnClickListener(view1 -> swipeDialog.dismiss());
        });

        clockStyleLayout.setOnClickListener(view ->{
            setupClockOptionsToggle();
        });

        displaySumLayout.setOnClickListener(view -> {
            if(tickTrackDatabase.isSumEnabled()){
                tickTrackDatabase.setSumEnabled(false);
            } else {
                tickTrackDatabase.setSumEnabled(true);
            }
            setupCounterSum();
            setupSettingsChangeTime();
        });

        displaySumSwitch.setOnCheckedChangeListener((compoundButton, b) -> {
            if(compoundButton.isChecked()){
                tickTrackDatabase.setSumEnabled(true);
            } else {
                tickTrackDatabase.setSumEnabled(false);
            }
            setupCounterSum();
            setupSettingsChangeTime();
        });

        backButton.setOnClickListener(view -> onBackPressed());

        dateTimeLayout.setOnClickListener(view -> startActivityForResult(new Intent(Settings.ACTION_DATE_SETTINGS), 1));
    }



    private boolean isMyServiceRunning(Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (BackupRestoreService.class.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    private void setupEmailText() {
        if(firebaseHelper.isUserSignedIn()){
            if(tickTrackFirebaseDatabase.isDriveLinkFail()){
                backupEmail.setText("Drive link failed, Tap to sign in again");
                backupEmail.setTextColor(getResources().getColor(R.color.roboto_calendar_circle_1));
                setupAccountDisabled();
            } else {
                setupAccountEnabled();
                backupEmail.setText(tickTrackFirebaseDatabase.getCurrentUserEmail());
                if(tickTrackDatabase.getThemeMode()==1){
                    backupEmail.setTextColor(getResources().getColor(R.color.LightDarkText));
                } else {
                    backupEmail.setTextColor(getResources().getColor(R.color.DarkLightText));
                }

            }
        } else {
            backupEmail.setText("Add an account");
            if(tickTrackDatabase.getThemeMode()==1){
                backupEmail.setTextColor(getResources().getColor(R.color.LightDarkText));
            } else {
                backupEmail.setTextColor(getResources().getColor(R.color.DarkLightText));
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(sharedPreferenceChangeListener);

        Intent intent;
        if(ScreensaverActivity.ACTION_SCREENSAVER_EDIT.equals(receivedAction)){
            intent = new Intent(this, ScreensaverActivity.class);
        } else {
            intent = new Intent(this, SoYouADeveloperHuh.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        }
        startActivity(intent);
        overridePendingTransition(R.anim.enter_from_left, R.anim.exit_to_right);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sharedPreferences = tickTrackDatabase.getSharedPref(this);
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(sharedPreferenceChangeListener);
        tickTrackDatabase.storeCurrentFragmentNumber(prevFragment);
    }

}