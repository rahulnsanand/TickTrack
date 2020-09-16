package com.theflopguyproductions.ticktrack.settings;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.widget.NestedScrollView;

import com.theflopguyproductions.ticktrack.R;
import com.theflopguyproductions.ticktrack.SoYouADeveloperHuh;
import com.theflopguyproductions.ticktrack.dialogs.ProgressBarDialog;
import com.theflopguyproductions.ticktrack.screensaver.ScreensaverActivity;
import com.theflopguyproductions.ticktrack.service.BackupRestoreService;
import com.theflopguyproductions.ticktrack.startup.StartUpActivity;
import com.theflopguyproductions.ticktrack.utils.database.TickTrackDatabase;
import com.theflopguyproductions.ticktrack.utils.database.TickTrackFirebaseDatabase;
import com.theflopguyproductions.ticktrack.utils.firebase.FirebaseHelper;
import com.theflopguyproductions.ticktrack.utils.helpers.TickTrackThemeSetter;
import com.theflopguyproductions.ticktrack.utils.helpers.TimeAgo;

import java.util.ArrayList;

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
            includeDataLayout, backupDataOptionsLayout, themeOptionsLayout;
    private TextView backupGoogleTitle, backupEmail, switchAccountTitle, disconnectAccountTitle, syncDataTitle, syncDataFrequency, syncDataLastSync, includeDataTitle, includeDataValue;
    private CheckBox counterCheckBox, timerCheckBox;
    private RadioButton monthlyButton, weeklyButton, dailyButton, darkButton, lightButton;

    private ConstraintLayout hapticLayout;
    private TextView hapticTextTitle;
    private Switch hapticSwitch;

    private ConstraintLayout deleteBackupLayout, factoryResetLayout;

    private ConstraintLayout rateUsLayout, displaySumLayout, timerSoundLayout, clockStyleLayout, clockOptionsLayout, dateTimeLayout;
    private TextView rateUsTitle, rateUsValue, displaySumTitle, timerSoundTitle, timerSoundValue, clockStyleTitle, clockStyleValue, dateTimeTitle, dateTimeValue;
    private ImageView unordinaryImage, oxygenyImage, minimalImage, simplisticImage, romanImage, funkyImage;
    private ImageView unordinaryImageCheck, oxygenyImageCheck, minimalImageCheck, simplisticImageCheck, romanImageCheck, funkyImageCheck;
    private Switch displaySumSwitch;

    @Override
    protected void onResume() {
        super.onResume();
        if(settingsLoadState){
            sharedPreferences.registerOnSharedPreferenceChangeListener(sharedPreferenceChangeListener);
            if(firebaseHelper.isUserSignedIn()){
                setupEmailText();
            }
            refreshTheme();
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
    }

    private void setupAccountBusy() {
        if(isMyServiceRunning(BackupRestoreService.class, getApplicationContext())){
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
            syncDataLastSync.setText("Last backup "+ TimeAgo.getTimeAgo(tickTrackDatabase.getLastBackupSystemTime()));
        } else {
            syncDataLastSync.setText("No backup yet");
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
        syncDataFrequency.setText(frequencyOption);
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
                tickTrackDatabase, backupGoogleTitle, backupEmail, googleAccountLayout, switchAccountOptionLayout, disconnectAccountOptionLayout, switchAccountTitle, disconnectAccountTitle,
                counterCheckBox, timerCheckBox, monthlyButton, weeklyButton, dailyButton, syncFreqOptionsLayout, darkButton, lightButton, themeOptionsLayout,
                hapticLayout, hapticTextTitle, deleteBackupLayout, factoryResetLayout, rateUsLayout, displaySumLayout, timerSoundLayout, clockStyleLayout, clockOptionsLayout, dateTimeLayout,
                rateUsTitle, rateUsValue, displaySumTitle, timerSoundTitle, timerSoundValue, clockStyleTitle, clockStyleValue, dateTimeTitle, dateTimeValue, toolbar);
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
        });
        oxygenyImage.setOnClickListener(view -> {
            clockStyle=2;
            setupClockStyle();
        });
        minimalImage.setOnClickListener(view -> {
            clockStyle=3;
            setupClockStyle();
        });
        simplisticImage.setOnClickListener(view -> {
            clockStyle=4;
            setupClockStyle();
        });
        romanImage.setOnClickListener(view -> {
            clockStyle=5;
            setupClockStyle();
        });
        funkyImage.setOnClickListener(view -> {
            clockStyle=6;
            setupClockStyle();
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

        deleteBackupLayout = findViewById(R.id.dangerZoneDeleteBackupLayout);
        factoryResetLayout = findViewById(R.id.dangerZoneFactoryResetLayout);

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

        setupClockStyle();
        setOnClickListeners();

        setupTimerSound();

        setupCounterSum();

        new Handler(Looper.getMainLooper()).post(() -> {
            accountOptionsLayout.setVisibility(View.GONE);
            clockOptionsLayout.setVisibility(View.GONE);
            themeOptionsLayout.setVisibility(View.GONE);
        });
    }

    private String receivedAction = null;
    private boolean settingsLoadState = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        loadHandler.post(settingsLoadRunnable);
        AsyncTaskRunner asyncTaskRunner = new AsyncTaskRunner();
        asyncTaskRunner.execute();
        receivedAction = getIntent().getAction();
    }

    private class AsyncTaskRunner extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... params) {
            initVariables();
            setupClickListeners();
            return "Done";
        }
        @Override
        protected void onPostExecute(String result) {
            settingsLoadState = true;
        }
    }
    private Handler loadHandler = new Handler();
    private Runnable settingsLoadRunnable = new Runnable() {
        @Override
        public void run() {
            if(settingsLoadState){
                System.out.println("SETTINGS DONE");
                loadHandler.removeCallbacks(settingsLoadRunnable);
                onResume();
            } else {
                System.out.println("SETTINGS NOT DONE");
                loadHandler.post(settingsLoadRunnable);
            }
        }
    };

    private void toggleHapticEnable() {
        if(tickTrackDatabase.isHapticEnabled()){
            tickTrackDatabase.setHapticEnabled(false);
        } else {
            tickTrackDatabase.setHapticEnabled(true);
        }
    }

    private void setupClickListeners() {

        monthlyButton.setOnClickListener((view) -> {
            tickTrackDatabase.storeSyncFrequency(SettingsData.Frequency.MONTHLY.getCode());
            tickTrackFirebaseDatabase.cancelBackUpAlarm();
            tickTrackFirebaseDatabase.setBackUpAlarm();
            toggleSyncOptionsLayout();
        });
        weeklyButton.setOnClickListener((view) -> {
            tickTrackDatabase.storeSyncFrequency(SettingsData.Frequency.WEEKLY.getCode());
            tickTrackFirebaseDatabase.cancelBackUpAlarm();
            tickTrackFirebaseDatabase.setBackUpAlarm();
            toggleSyncOptionsLayout();
        });
        dailyButton.setOnClickListener((view) -> {
            tickTrackDatabase.storeSyncFrequency(SettingsData.Frequency.DAILY.getCode());
            tickTrackFirebaseDatabase.cancelBackUpAlarm();
            tickTrackFirebaseDatabase.setBackUpAlarm();
            toggleSyncOptionsLayout();
        });

        darkButton.setOnClickListener((view) -> {
            tickTrackDatabase.setThemeMode(SettingsData.Theme.DARK.getCode());
            toggleThemeOptionsLayout();
            refreshTheme();
        });
        lightButton.setOnClickListener((view) -> {
            tickTrackDatabase.setThemeMode(SettingsData.Theme.LIGHT.getCode());
            toggleThemeOptionsLayout();
            refreshTheme();
        });

        themeLayout.setOnClickListener(view -> {
            toggleThemeOptionsLayout();
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
            if(isMyServiceRunning(BackupRestoreService.class, getApplicationContext())){
                Toast.makeText(activity, "Backup/Restore Ongoing, Please wait", Toast.LENGTH_SHORT).show();
            } else {
                if(firebaseHelper.isUserSignedIn()){
                    firebaseHelper.switchAccount(activity);
                    toggleGoogleAccountOptionsLayout();
                }
            }
        });

        disconnectAccountOptionLayout.setOnClickListener(view -> {
            if(isMyServiceRunning(BackupRestoreService.class,this)){
                Toast.makeText(activity, "Backup/Restore Ongoing, Please wait", Toast.LENGTH_SHORT).show();
            } else {
                if(firebaseHelper.isUserSignedIn()){
                    firebaseHelper.signOut(activity);
                    toggleGoogleAccountOptionsLayout();
                    setupEmailText();
                }
            }
        });

        counterCheckBox.setOnCheckedChangeListener((compoundButton, b) -> {
            if(compoundButton.isChecked()){
                tickTrackDatabase.setCounterDataBackup(true);
            } else {
                tickTrackDatabase.setCounterDataBackup(false);
            }
        });
        timerCheckBox.setOnCheckedChangeListener((compoundButton, b) -> {
            if(compoundButton.isChecked()){
                tickTrackDatabase.setTimerDataBackup(true);
            } else {
                tickTrackDatabase.setTimerDataBackup(false);
            }
        });

        hapticLayout.setOnClickListener(view -> {
            toggleHapticEnable();
        });
        hapticSwitch.setOnCheckedChangeListener((compoundButton, b) -> {
            if(compoundButton.isChecked()){
                tickTrackDatabase.setHapticEnabled(true);
            } else {
                tickTrackDatabase.setHapticEnabled(false);
            }
        });

        deleteBackupLayout.setOnClickListener(view -> {
            if(isMyServiceRunning(BackupRestoreService.class,this)){
                Toast.makeText(activity, "Backup/Restore Ongoing, Please wait", Toast.LENGTH_SHORT).show();
            } else {
                if(firebaseHelper.isUserSignedIn()){
                    firebaseHelper.deleteBackup(this);
                }
            }
        });
        factoryResetLayout.setOnClickListener(view -> {
            if(isMyServiceRunning(BackupRestoreService.class,this)){
                Toast.makeText(activity, "Backup/Restore Ongoing, Please wait", Toast.LENGTH_SHORT).show();
            } else {
                if(firebaseHelper.isUserSignedIn()){
                    new Handler().post(new Runnable() {
                        @Override
                        public void run() {
                            ProgressBarDialog progressBarDialog = new ProgressBarDialog(activity);
                            progressBarDialog.show();
                            progressBarDialog.setContentText("Resetting TickTrack");
                            progressBarDialog.titleText.setVisibility(View.GONE);
                            tickTrackDatabase.resetData(activity);
                            progressBarDialog.dismiss();
                        }
                    });
                }
            }
        });

        clockStyleLayout.setOnClickListener(view -> setupClockOptionsToggle());

        displaySumLayout.setOnClickListener(view -> {
            if(tickTrackDatabase.isSumEnabled()){
                tickTrackDatabase.setSumEnabled(false);
            } else {
                tickTrackDatabase.setSumEnabled(true);
            }
            setupCounterSum();
        });

        displaySumSwitch.setOnCheckedChangeListener((compoundButton, b) -> {
            if(compoundButton.isChecked()){
                tickTrackDatabase.setSumEnabled(true);
            } else {
                tickTrackDatabase.setSumEnabled(false);
            }
            setupCounterSum();
        });

        backButton.setOnClickListener(view -> onBackPressed());

        dateTimeLayout.setOnClickListener(view -> startActivityForResult(new Intent(Settings.ACTION_DATE_SETTINGS), 1));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }

    private boolean isMyServiceRunning(Class<?> serviceClass, Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    private void setupEmailText() {
        if(firebaseHelper.isUserSignedIn()){
            if(tickTrackFirebaseDatabase.isDriveLinkFail()){
                backupEmail.setTextColor(getResources().getColor(R.color.roboto_calendar_circle_1));
                backupEmail.setText("Drive link failed. Sign in Again");
            } else {
                backupEmail.setText(tickTrackFirebaseDatabase.getCurrentUserEmail());
            }
        } else {
            backupEmail.setText("Add an account");
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(sharedPreferenceChangeListener);

        if(ScreensaverActivity.ACTION_SCREENSAVER_EDIT.equals(receivedAction)){
            Intent intent = new Intent(this, ScreensaverActivity.class);
            startActivity(intent);
        } else {
            Intent intent = new Intent(this, SoYouADeveloperHuh.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        sharedPreferences = tickTrackDatabase.getSharedPref(this);
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(sharedPreferenceChangeListener);
        tickTrackDatabase.storeCurrentFragmentNumber(prevFragment);
    }

}