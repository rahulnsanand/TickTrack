package com.theflopguyproductions.ticktrack.startup.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.theflopguyproductions.ticktrack.R;
import com.theflopguyproductions.ticktrack.service.RestoreService;
import com.theflopguyproductions.ticktrack.utils.database.TickTrackDatabase;
import com.theflopguyproductions.ticktrack.utils.database.TickTrackFirebaseDatabase;
import com.theflopguyproductions.ticktrack.utils.helpers.FirebaseHelper;

public class RestoreFragment extends Fragment {

    private TickTrackDatabase tickTrackDatabase;
    private FirebaseHelper firebaseHelper;
    private TickTrackFirebaseDatabase tickTrackFirebaseDatabase;

    private TextView mainTitle, subTitle, DataRetrieveTitle, preferencesText, timersText, countersText, restoreQuestionText;
    private Button restoreDataButton, startFreshButton;
    private CheckBox preferencesCheck, timersCheck, countersCheck;
    private SharedPreferences sharedPreferences;

    private Activity activity;

    private String receivedAction;

    public RestoreFragment(String receivedAction) {
        this.receivedAction = receivedAction;
    }

    @Override
    public void onStart() {
        super.onStart();
        sharedPreferences.registerOnSharedPreferenceChangeListener(sharedPreferenceChangeListener);
        tickTrackDatabase.storeCurrentFragmentNumber(3);
    }

    SharedPreferences.OnSharedPreferenceChangeListener sharedPreferenceChangeListener = (sharedPreferences, s) -> databaseChangeListener();

    private void databaseChangeListener() {
        setupChanges();
        checkRestoreMode();
    }

    private void checkRestoreMode() {
        if(!tickTrackFirebaseDatabase.isRestoreMode()){
            stopRestoreService();
        }
    }

    private void setupChanges() {

    }

    private void initVariables(View root){
        mainTitle = root.findViewById(R.id.restoreFragmentTitleText);
        subTitle = root.findViewById(R.id.restoreFragmentSubtitleText);
        DataRetrieveTitle = root.findViewById(R.id.restoreFragmentDataReadyText);
        preferencesText = root.findViewById(R.id.restoreFragmentPreferencesText);
        timersText = root.findViewById(R.id.restoreFragmentTimerText);
        countersText = root.findViewById(R.id.restoreFragmentCounterText);
        restoreQuestionText = root.findViewById(R.id.restoreFragmentRestoreOptionsTitle);
        restoreDataButton = root.findViewById(R.id.restoreFragmentRestoreDataButton);
        startFreshButton = root.findViewById(R.id.restoreFragmentStartFreshButton);
        preferencesCheck = root.findViewById(R.id.restoreFragmentPreferencesCheckBox);
        countersCheck = root.findViewById(R.id.restoreFragmentCounterCheckBox);
        timersCheck = root.findViewById(R.id.restoreFragmentTimerCheckBox);

        activity = getActivity();

        firebaseHelper = new FirebaseHelper(getActivity());
        tickTrackDatabase = new TickTrackDatabase(getContext());
        tickTrackFirebaseDatabase = new TickTrackFirebaseDatabase(getContext());
        sharedPreferences = tickTrackDatabase.getSharedPref(getContext());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root =  inflater.inflate(R.layout.fragment_ticktrack_restore, container, false);

        initVariables(root);

        startRestoreService();

        return root;
    }

    private void stopRestoreService() {
        tickTrackFirebaseDatabase.setRestoreMode(true);
        Intent intent = new Intent(activity, RestoreService.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.setAction(RestoreService.DATA_RESTORATION_STOP);
        activity.startService(intent);
    }

    private void startRestoreService() {

        Intent intent = new Intent(activity, RestoreService.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.setAction(RestoreService.DATA_RESTORATION_START);
        intent.putExtra("receivedAction", receivedAction);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            activity.startForegroundService(intent);
        } else {
            activity.startService(intent);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        sharedPreferences = tickTrackDatabase.getSharedPref(activity);
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(sharedPreferenceChangeListener);
        tickTrackDatabase.storeCurrentFragmentNumber(3);
    }



    private RestoreCompleteListener restoreCompleteListener;

    public interface RestoreCompleteListener {
        void onRestoreCompleteListener();
    }
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            restoreCompleteListener = (RestoreCompleteListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement " + RestoreCompleteListener.class.getName());
        }
    }
    @Override
    public void onDetach() {
        super.onDetach();
        restoreCompleteListener = null;
    }
}