package com.theflopguyproductions.ticktrack.settings;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.theflopguyproductions.ticktrack.R;
import com.theflopguyproductions.ticktrack.SoYouADeveloperHuh;
import com.theflopguyproductions.ticktrack.service.BackupRestoreService;
import com.theflopguyproductions.ticktrack.startup.StartUpActivity;
import com.theflopguyproductions.ticktrack.utils.database.TickTrackDatabase;
import com.theflopguyproductions.ticktrack.utils.database.TickTrackFirebaseDatabase;
import com.theflopguyproductions.ticktrack.utils.helpers.FirebaseHelper;
import com.theflopguyproductions.ticktrack.utils.helpers.TickTrackThemeSetter;

import java.util.ArrayList;

public class SettingsActivity extends AppCompatActivity {

    private Activity activity;
    private ImageButton backButton;
    private ScrollView settingsScrollView;
    private int prevFragment = -1;

    private FirebaseHelper firebaseHelper;
    private TickTrackDatabase tickTrackDatabase;
    private TickTrackFirebaseDatabase tickTrackFirebaseDatabase;
    private SharedPreferences sharedPreferences;

    private ConstraintLayout themeLayout;
    private TextView themeName, themeTitle;

    private ConstraintLayout googleAccountLayout, accountOptionsLayout, switchAccountOptionLayout, disconnectAccountOptionLayout, syncDataLayout, syncFreqOptionsLayout,
            includeDataLayout, backupDataOptionsLayout, themeOptionsLayout;
    private TextView backupGoogleTitle, backupEmail, switchAccountTitle, disconnectAccountTitle, syncDataTitle, syncDataFrequency, syncDataLastSync, includeDataTitle, includeDataValue;
    private CheckBox counterCheckBox, timerCheckBox;
    private RadioButton monthlyButton, weeklyButton, dailyButton, darkButton, lightButton;

    @Override
    protected void onStart() {
        super.onStart();
        sharedPreferences.registerOnSharedPreferenceChangeListener(sharedPreferenceChangeListener);
        if(firebaseHelper.isUserSignedIn()){
            setupEmailText();
        }
        refreshTheme();
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
            syncDataFrequency.setTextColor(getResources().getColor(R.color.DarkText));
            syncDataLastSync.setTextColor(getResources().getColor(R.color.DarkText));
            includeDataTitle.setTextColor(getResources().getColor(R.color.DarkText));
            includeDataValue.setTextColor(getResources().getColor(R.color.DarkText));
        } else {
            syncDataLayout.setBackgroundResource(R.drawable.clickable_layout_dark_background);
            includeDataLayout.setBackgroundResource(R.drawable.clickable_layout_dark_background);

            syncDataTitle.setTextColor(getResources().getColor(R.color.LightText));
            syncDataFrequency.setTextColor(getResources().getColor(R.color.LightText));
            syncDataLastSync.setTextColor(getResources().getColor(R.color.LightText));
            includeDataTitle.setTextColor(getResources().getColor(R.color.LightText));
            includeDataValue.setTextColor(getResources().getColor(R.color.LightText));
        }
        syncDataLayout.setOnClickListener(view -> toggleSyncOptionsLayout());
        includeDataLayout.setOnClickListener(view -> toggleDataOptionsLayout());
    }

    private void setupLastBackupText() {
        if(tickTrackDatabase.getLastBackupSystemTime()!=-1){
            syncDataLastSync.setText("Last backup at "+tickTrackDatabase.getLastBackupSystemTime());
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
                counterCheckBox, timerCheckBox, monthlyButton, weeklyButton, dailyButton, syncFreqOptionsLayout, darkButton, lightButton, themeOptionsLayout);
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
    }

    private void initVariables() {
        themeLayout = findViewById(R.id.themeSettingsLayout);
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

        accountOptionsLayout.setVisibility(View.GONE);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        initVariables();

        setupClickListeners();


    }

    private void setupClickListeners() {

        monthlyButton.setOnClickListener((view) -> {
            tickTrackDatabase.storeSyncFrequency(SettingsData.Frequency.MONTHLY.getCode());
            toggleSyncOptionsLayout();
        });
        weeklyButton.setOnClickListener((view) -> {
            tickTrackDatabase.storeSyncFrequency(SettingsData.Frequency.WEEKLY.getCode());
            toggleSyncOptionsLayout();
        });
        dailyButton.setOnClickListener((view) -> {
            tickTrackDatabase.storeSyncFrequency(SettingsData.Frequency.DAILY.getCode());
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
            if(firebaseHelper.isUserSignedIn()){
                toggleGoogleAccountOptionsLayout();
            } else {
                backupEmail.setText("...");
                tickTrackDatabase.storeStartUpFragmentID(2);
                Intent startUpSignInIntent = new Intent(this, StartUpActivity.class);
                startUpSignInIntent.setAction(StartUpActivity.ACTION_SETTINGS_ACCOUNT_ADD);
                startActivity(startUpSignInIntent);
            }
        });

        disconnectAccountOptionLayout.setOnClickListener(view -> {
            if(isMyServiceRunning(BackupRestoreService.class,this)){
                Toast.makeText(activity, "Backup/Restore Service Ongoing, Please wait", Toast.LENGTH_SHORT).show();
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

        backButton.setOnClickListener(view -> onBackPressed());
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
            backupEmail.setText(tickTrackFirebaseDatabase.getCurrentUserEmail());
        } else {
            backupEmail.setText("Add an account");
        }
    }



    @Override
    public void onBackPressed() {
        super.onBackPressed();
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(sharedPreferenceChangeListener);
        Intent intent = new Intent(this, SoYouADeveloperHuh.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    @Override
    protected void onStop() {
        super.onStop();
        sharedPreferences = tickTrackDatabase.getSharedPref(this);
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(sharedPreferenceChangeListener);
        tickTrackDatabase.storeCurrentFragmentNumber(prevFragment);
    }

}