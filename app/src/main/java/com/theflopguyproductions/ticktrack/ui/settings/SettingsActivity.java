package com.theflopguyproductions.ticktrack.ui.settings;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.Task;
import com.theflopguyproductions.ticktrack.R;
import com.theflopguyproductions.ticktrack.SoYouADeveloperHuh;
import com.theflopguyproductions.ticktrack.dialogs.SyncFrequencyDialog;
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

    private ConstraintLayout googleAccountLayout, accountOptionsLayout, switchAccountOptionLayout, disconnectAccountOptionLayout, syncDataLayout, includeDataLayout, backupDataOptionsLayout;
    private TextView backupGoogleTitle, backupEmail, switchAccountTitle, disconnectAccountTitle, syncDataTitle, syncDataFrequency, syncDataLastSync, includeDataTitle, includeDataValue;
    private CheckBox counterCheckBox, timerCheckBox;

    @Override
    protected void onStart() {
        super.onStart();
        sharedPreferences.registerOnSharedPreferenceChangeListener(sharedPreferenceChangeListener);
        if(firebaseHelper.isUserSignedIn()){
            setupEmailText();
        }
        TickTrackThemeSetter.settingsActivityTheme(activity, themeTitle, themeName, settingsScrollView, themeLayout,
                tickTrackDatabase, backupGoogleTitle, backupEmail, googleAccountLayout, switchAccountOptionLayout,
                disconnectAccountOptionLayout, switchAccountTitle, disconnectAccountTitle, counterCheckBox, timerCheckBox);
    }

    SharedPreferences.OnSharedPreferenceChangeListener sharedPreferenceChangeListener = (sharedPreferences, s) ->  {
        databaseChangeListener();
    };
    private void databaseChangeListener() {
        setupEmailText();
        checkAccountAvailable();
        setupLastBackupText();
        setupDataOptionsText();
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
    }
    private void setupLastBackupText() {
        if(tickTrackFirebaseDatabase.getLastBackupSystemTime()!=-1){
            syncDataLastSync.setText("Last backup at "+tickTrackFirebaseDatabase.getLastBackupSystemTime());
        } else {
            syncDataLastSync.setText("No backup yet");
        }
    }
    private void setupDataOptionsText(){
        ArrayList<Integer> optionsBackup = tickTrackFirebaseDatabase.getBackupDataOptions();
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

        activity = this;
        firebaseHelper = new FirebaseHelper(activity);
        tickTrackDatabase = new TickTrackDatabase(activity);
        tickTrackFirebaseDatabase = new TickTrackFirebaseDatabase(activity);
        sharedPreferences = tickTrackDatabase.getSharedPref(this);
        prevFragment = tickTrackDatabase.retrieveCurrentFragmentNumber();

        setupEmailText();
        setupLastBackupText();
        checkAccountAvailable();
        setupDataOptionsText();

        accountOptionsLayout.setVisibility(View.GONE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        initVariables();

        setupClickListeners();


    }

    private boolean isOptionsOpen = false;
    private void toggleDataOptionsLayout() {
        if(isOptionsOpen){
            backupDataOptionsLayout.setVisibility(View.GONE);
            setupDataOptionsText();
            isOptionsOpen = false;
        } else {
            backupDataOptionsLayout.setVisibility(View.VISIBLE);
            setupDataOptionsText();
            isOptionsOpen = true;
        }
    }

    private void setupClickListeners() {
        themeLayout.setOnClickListener(view -> {
            ThemeDialog themeDialog = new ThemeDialog(activity, tickTrackDatabase.getThemeMode());
            themeDialog.show();
        });

        googleAccountLayout.setOnClickListener(view -> {
            if(firebaseHelper.isUserSignedIn()){
                toggleGoogleAccountOptionsLayout();
            } else {
                backupEmail.setText("...");
                Intent signInIntent = firebaseHelper.getSignInIntent();
                activity.startActivityForResult(signInIntent, 1);
            }
        });

        disconnectAccountOptionLayout.setOnClickListener(view -> {
            firebaseHelper.signOut();
            toggleGoogleAccountOptionsLayout();
            setupEmailText();
        });

        syncDataLayout.setOnClickListener(view -> {
            SyncFrequencyDialog syncFrequencyDialog = new SyncFrequencyDialog(activity);
            syncFrequencyDialog.show();
            syncFrequencyDialog.monthlyRadioButton.setChecked(true);
            syncFrequencyDialog.dailyRadioButton.setEnabled(false);
            syncFrequencyDialog.weeklyRadioButton.setEnabled(false);

            syncFrequencyDialog.monthlyRadioButton.setOnClickListener(view1 -> {
                tickTrackFirebaseDatabase.storeSyncFrequency(1);
                syncFrequencyDialog.dismiss();
            });
        });

        counterCheckBox.setOnCheckedChangeListener((compoundButton, b) -> {
            if(compoundButton.isChecked()){
                tickTrackFirebaseDatabase.setCounterDataBackup(true);
            } else {
                tickTrackFirebaseDatabase.setCounterDataBackup(false);
            }
        });
        timerCheckBox.setOnCheckedChangeListener((compoundButton, b) -> {
            if(compoundButton.isChecked()){
                tickTrackFirebaseDatabase.setTimerDataBackup(true);
            } else {
                tickTrackFirebaseDatabase.setTimerDataBackup(false);
            }
        });

        includeDataLayout.setOnClickListener(view -> toggleDataOptionsLayout());

        backButton.setOnClickListener(view -> onBackPressed());
    }

    private void setupEmailText() {
        if(firebaseHelper.isUserSignedIn()){
            backupEmail.setText(tickTrackFirebaseDatabase.getCurrentUserEmail());
        } else {
            backupEmail.setText("Add a backup account");
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            firebaseHelper.signIn(task);
        }
    }

    private boolean isOpen = false;
    private void toggleGoogleAccountOptionsLayout() {
        if(isOpen){
            accountOptionsLayout.setVisibility(View.GONE);
            setupEmailText();
            isOpen = false;
        } else {
            accountOptionsLayout.setVisibility(View.VISIBLE);
            isOpen = true;
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