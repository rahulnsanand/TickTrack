package com.theflopguyproductions.ticktrack.startup.fragments;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.theflopguyproductions.ticktrack.R;
import com.theflopguyproductions.ticktrack.SoYouADeveloperHuh;
import com.theflopguyproductions.ticktrack.dialogs.ProgressBarDialog;
import com.theflopguyproductions.ticktrack.dialogs.SwipeDialog;
import com.theflopguyproductions.ticktrack.service.BackupRestoreService;
import com.theflopguyproductions.ticktrack.settings.SettingsActivity;
import com.theflopguyproductions.ticktrack.settings.SettingsData;
import com.theflopguyproductions.ticktrack.startup.StartUpActivity;
import com.theflopguyproductions.ticktrack.utils.database.TickTrackDatabase;
import com.theflopguyproductions.ticktrack.utils.database.TickTrackFirebaseDatabase;
import com.theflopguyproductions.ticktrack.utils.firebase.FirebaseHelper;
import com.theflopguyproductions.ticktrack.utils.firebase.InternetChecker;
import com.theflopguyproductions.ticktrack.utils.firebase.JsonHelper;

import java.util.ArrayList;

public class RestoreFragment extends Fragment {

    private TickTrackDatabase tickTrackDatabase;
    private FirebaseHelper firebaseHelper;
    private TickTrackFirebaseDatabase tickTrackFirebaseDatabase;

    private TextView mainTitle, subTitle, dataReadyTitle, preferencesText, timersText, countersText;
    private Button restoreDataButton, startFreshButton;
    private SharedPreferences sharedPreferences;

    private ProgressBarDialog progressBarDialog;

    private Activity activity;

    private String receivedAction;

    public RestoreFragment() {
    }

    public RestoreFragment(String receivedAction) {
        this.receivedAction = receivedAction;
    }

    @Override
    public void onResume() {
        super.onResume();
        sharedPreferences.registerOnSharedPreferenceChangeListener(sharedPreferenceChangeListener);
        tickTrackDatabase.storeStartUpFragmentID(3);
    }

    SharedPreferences.OnSharedPreferenceChangeListener sharedPreferenceChangeListener = (sharedPreferences, s) -> databaseChangeListener();

    private void databaseChangeListener() {
        setupChanges();
        checkRestoreMode();
    }

    private void checkRestoreMode() {
        if(tickTrackFirebaseDatabase.isDriveLinkFail()){
            firebaseHelper.signOut(activity);
            Toast.makeText(activity, "Kindly login again", Toast.LENGTH_SHORT).show();
            tickTrackDatabase.storeStartUpFragmentID(3);
            startActivity(new Intent(activity, StartUpActivity.class));
        } else if(tickTrackFirebaseDatabase.isRestoreInitMode()==1){
            internetHandler.removeCallbacks(internetCheck);
            if(tickTrackFirebaseDatabase.hasPreferencesDataBackup() || tickTrackFirebaseDatabase.getRetrievedCounterCount()!=-1 || tickTrackFirebaseDatabase.getRetrievedTimerCount()!=-1){
                progressBarDialog.dismiss();
                setupOptionsDisplay();
            } else {
                tickTrackFirebaseDatabase.storeSettingsRestoredData(new ArrayList<>());
                tickTrackFirebaseDatabase.setRestoreCompleteStatus(1);
                tickTrackFirebaseDatabase.setRestoreInitMode(0);
                tickTrackFirebaseDatabase.storeTimerRestoreString("");
                tickTrackFirebaseDatabase.storeCounterRestoreString("");
                progressBarDialog.dismiss();
                tickTrackFirebaseDatabase.setBackUpAlarm(false);
                if(StartUpActivity.ACTION_SETTINGS_ACCOUNT_ADD.equals(receivedAction)){
                    Intent intent = new Intent(requireContext(), SettingsActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    requireContext().startActivity(intent);
                } else {
                    if(!tickTrackDatabase.retrieveFirstLaunch()){
                        startActivity(new Intent(activity, SoYouADeveloperHuh.class));
                    }
                }
            }
        } else if(tickTrackFirebaseDatabase.isRestoreInitMode()==-1){
            progressBarDialog.dismiss();
            setupNoInternet();
        }
    }

    private void setupNoInternet() {
        mainTitle.setText("Restore your data");
        subTitle.setText("Oops.");
        restoreDataButton.setText("Retry Restoration");
        dataReadyTitle.setText("We need internet");

        restoreDataButton.setOnClickListener(view -> startRestoreInitService());
        restoreDataButton.setVisibility(View.VISIBLE);
        startFreshButton.setVisibility(View.VISIBLE);
        preferencesText.setVisibility(View.GONE);
        countersText.setVisibility(View.GONE);
        timersText.setVisibility(View.GONE);
    }

    private void setupOptionsDisplay() {
        mainTitle.setText("Restore your data");
        subTitle.setText("We found something of yours");
        restoreDataButton.setText("Restore Data");
        restoreDataButton.setOnClickListener(view ->{
            restoreData();
            tickTrackFirebaseDatabase.setBackUpAlarm(false);
            if(StartUpActivity.ACTION_SETTINGS_ACCOUNT_ADD.equals(receivedAction)){
                Intent intent = new Intent(requireContext(), SettingsActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                requireContext().startActivity(intent);
            } else {
                if(!tickTrackDatabase.retrieveFirstLaunch()){
                    startActivity(new Intent(activity, SoYouADeveloperHuh.class));
                }
            }
            prefixVariables();
        });
        restoreDataButton.setVisibility(View.VISIBLE);
        startFreshButton.setVisibility(View.VISIBLE);
    }

    private void setupChanges() {
        if(tickTrackFirebaseDatabase.hasPreferencesDataBackup()){
            preferencesText.setVisibility(View.VISIBLE);
            preferencesText.setText("Preferences retrieved");
            dataReadyTitle.setVisibility(View.VISIBLE);
        }  else {
            preferencesText.setVisibility(View.GONE);
        }
        if(tickTrackFirebaseDatabase.getRetrievedCounterCount()!=-1){
            dataReadyTitle.setVisibility(View.VISIBLE);
            countersText.setVisibility(View.VISIBLE);
            if(tickTrackFirebaseDatabase.getRetrievedCounterCount()>1){
                countersText.setText("Retrieved "+tickTrackFirebaseDatabase.getRetrievedCounterCount()+" counters data");
            } else {
                countersText.setText("Retrieved "+tickTrackFirebaseDatabase.getRetrievedCounterCount()+" counter data");
            }
        } else {
            countersText.setVisibility(View.GONE);
        }
        if(tickTrackFirebaseDatabase.getRetrievedTimerCount()!=-1){
            dataReadyTitle.setVisibility(View.VISIBLE);
            timersText.setVisibility(View.VISIBLE);
            if(tickTrackFirebaseDatabase.getRetrievedTimerCount()>1){
                timersText.setText("Retrieved "+tickTrackFirebaseDatabase.getRetrievedTimerCount()+" timers data");
            } else {
                timersText.setText("Retrieved "+tickTrackFirebaseDatabase.getRetrievedTimerCount()+" timer data");
            }
        }  else {
            timersText.setVisibility(View.GONE);
        }
    }

    private void prefixVariables() {
        tickTrackFirebaseDatabase.foundPreferencesDataBackup(false);
        tickTrackFirebaseDatabase.storeRetrievedCounterCount(-1);
        tickTrackFirebaseDatabase.storeRetrievedTimerCount(-1);
        tickTrackFirebaseDatabase.setRestoreInitMode(0);
    }

    private void initVariables(View root) {
        mainTitle = root.findViewById(R.id.restoreFragmentTitleText);
        subTitle = root.findViewById(R.id.restoreFragmentSubtitleText);
        dataReadyTitle = root.findViewById(R.id.restoreFragmentDataReadyText);
        dataReadyTitle.setVisibility(View.GONE);
        preferencesText = root.findViewById(R.id.restoreFragmentPreferencesText);
        preferencesText.setVisibility(View.GONE);
        timersText = root.findViewById(R.id.restoreFragmentTimerText);
        timersText.setVisibility(View.GONE);
        countersText = root.findViewById(R.id.restoreFragmentCounterText);
        countersText.setVisibility(View.GONE);
        restoreDataButton = root.findViewById(R.id.restoreFragmentRestoreDataButton);
        startFreshButton = root.findViewById(R.id.restoreFragmentStartFreshButton);

        activity = getActivity();

        progressBarDialog = new ProgressBarDialog(activity);
        firebaseHelper = new FirebaseHelper(getActivity());
        firebaseHelper.setAction(receivedAction);
        System.out.println("RESTORE ACTIVITY RECEIVED "+receivedAction);

        tickTrackDatabase = new TickTrackDatabase(getContext());
        tickTrackFirebaseDatabase = new TickTrackFirebaseDatabase(getContext());
        sharedPreferences = tickTrackDatabase.getSharedPref(getContext());

        restoreDataButton.setOnClickListener(view -> {
            progressBarDialog.show();
            progressBarDialog.titleText.setVisibility(View.GONE);
            progressBarDialog.setContentText("Restoring Data");
            restoreData();
            progressBarDialog.dismiss();
            tickTrackFirebaseDatabase.setBackUpAlarm(false);
            if(StartUpActivity.ACTION_SETTINGS_ACCOUNT_ADD.equals(receivedAction)){
                Intent intent = new Intent(requireContext(), SettingsActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                requireContext().startActivity(intent);
            } else {
                if(!tickTrackDatabase.retrieveFirstLaunch()){
                    startActivity(new Intent(activity, SoYouADeveloperHuh.class));
                }
            }
            prefixVariables();
        });
        startFreshButton.setOnClickListener(view -> {
            SwipeDialog swipeDialog = new SwipeDialog(activity);
            swipeDialog.show();
            swipeDialog.dialogTitle.setText("Are you sure?");
            swipeDialog.dialogMessage.setText("This will delete your cloud TickTrack data on next backup");
            swipeDialog.swipeButton.setOnClickListener(v -> {
                swipeDialog.dismiss();

                tickTrackFirebaseDatabase.storeSettingsRestoredData(new ArrayList<>());
                tickTrackFirebaseDatabase.setRestoreCompleteStatus(1);
                tickTrackFirebaseDatabase.setRestoreInitMode(0);
                tickTrackFirebaseDatabase.storeTimerRestoreString("");
                tickTrackFirebaseDatabase.storeCounterRestoreString("");
                if(isMyServiceRunning(BackupRestoreService.class, activity)){
                    stopRestoreService();
                }
                tickTrackFirebaseDatabase.setBackUpAlarm(true);
                if(StartUpActivity.ACTION_SETTINGS_ACCOUNT_ADD.equals(receivedAction)){
                    Intent intent = new Intent(requireContext(), SettingsActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    requireContext().startActivity(intent);
                } else {
                    if(!tickTrackDatabase.retrieveFirstLaunch()){
                        startActivity(new Intent(activity, SoYouADeveloperHuh.class));
                    }
                }
                prefixVariables();
            });
            swipeDialog.dismissButton.setOnClickListener(view1 -> swipeDialog.dismiss());
        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root =  inflater.inflate(R.layout.fragment_ticktrack_restore, container, false);

        initVariables(root);

        progressBarDialog = new ProgressBarDialog(activity);

        if(tickTrackFirebaseDatabase.isRestoreInitMode()==1){
            progressBarDialog.dismiss();
            setupOptionsDisplay();
            setupChanges();
        } else {
            startRestoreInitService();
        }

        return root;
    }

    private void stopRestoreService() {
        Intent intent = new Intent(activity, BackupRestoreService.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.setAction(BackupRestoreService.RESTORE_SERVICE_STOP_FOREGROUND);
        intent.putExtra("receivedAction", receivedAction);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            activity.startForegroundService(intent);
        } else {
            activity.startService(intent);
        }
    }


    private Handler internetHandler = new Handler();
    Runnable internetCheck = new Runnable() {
        @Override
        public void run() {
            if(!InternetChecker.isOnline(requireContext())){
                setupNoInternet();
                tickTrackFirebaseDatabase.setRestoreInitMode(-1);
                internetHandler.removeCallbacks(internetCheck);
                System.out.println("LOOP END");
            } else {
                System.out.println("LOOP RUN");
                internetHandler.post(internetCheck);
            }
        }
    };



    private void startRestoreInitService() {

        progressBarDialog.show();
        progressBarDialog.titleText.setVisibility(View.GONE);
        progressBarDialog.setContentText("Checking for backup");
        mainTitle.setText("Checking for backup");
        subTitle.setText("Please wait");

        restoreDataButton.setVisibility(View.INVISIBLE);
        startFreshButton.setVisibility(View.INVISIBLE);

        tickTrackFirebaseDatabase.setRestoreMode(false);
        tickTrackFirebaseDatabase.setCounterDownloadStatus(0);
        tickTrackFirebaseDatabase.setTimerDownloadStatus(0);
        tickTrackFirebaseDatabase.setRestoreCompleteStatus(0);
        tickTrackFirebaseDatabase.storeTimerRestoreString("");
        tickTrackFirebaseDatabase.storeCounterRestoreString("");
        tickTrackFirebaseDatabase.setCounterBackupComplete(false);
        tickTrackFirebaseDatabase.setTimerBackupComplete(false);
        tickTrackFirebaseDatabase.setSettingsBackupComplete(false);
        tickTrackFirebaseDatabase.setBackupMode(false);

        internetHandler.post(internetCheck);

        firebaseHelper.restoreInit();


    }

    private void restoreData(){
        tickTrackFirebaseDatabase.setRestoreInitMode(0);
        tickTrackFirebaseDatabase.setRestoreMode(true);
        initPreferences();
        new JsonHelper(requireContext()).restoreCounterData();
        new JsonHelper(requireContext()).restoreTimerData();
        tickTrackFirebaseDatabase.setRestoreMode(false);
    }
    private void initPreferences() {
        ArrayList<SettingsData> settingsData = tickTrackFirebaseDatabase.retrieveSettingsRestoredData();
        if(settingsData.size()>0){
            tickTrackDatabase.setThemeMode(settingsData.get(0).getThemeMode());
            tickTrackDatabase.setCounterDataBackup(settingsData.get(0).isCounterBackupOn());
            tickTrackDatabase.setTimerDataBackup(settingsData.get(0).isTimerBackupOn());
            tickTrackDatabase.setHapticEnabled(settingsData.get(0).isHapticFeedback());
            tickTrackDatabase.setLastBackupSystemTime(settingsData.get(0).getLastBackupTime());
            tickTrackDatabase.storeSyncFrequency(settingsData.get(0).getSyncDataFrequency());
            tickTrackDatabase.storeScreenSaverClock(settingsData.get(0).getScreensaverClockStyle());
            tickTrackDatabase.setMilestoneVibrate(settingsData.get(0).isMilestoneVibrate());
            tickTrackDatabase.setSumEnabled(settingsData.get(0).isSumDisplayed());
            System.out.println("INITIALISED PREFERENCES");
        }
        tickTrackFirebaseDatabase.storeSettingsRestoredData(new ArrayList<>());
    }

    @Override
    public void onPause() {
        super.onPause();
        sharedPreferences = tickTrackDatabase.getSharedPref(activity);
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(sharedPreferenceChangeListener);
        tickTrackDatabase.storeStartUpFragmentID(3);
        internetHandler.removeCallbacks(internetCheck);
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
    private StartFreshListener startFreshListener;
    public interface StartFreshListener {
        void onStartFreshClickListener(boolean nextFragment);
    }
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            startFreshListener = (StartFreshListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement " + StartFreshListener.class.getName());
        }
    }
    @Override
    public void onDetach() {
        super.onDetach();
        startFreshListener = null;
    }
}