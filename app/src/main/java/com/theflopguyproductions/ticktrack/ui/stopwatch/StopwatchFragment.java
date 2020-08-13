package com.theflopguyproductions.ticktrack.ui.stopwatch;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.theflopguyproductions.ticktrack.R;
import com.theflopguyproductions.ticktrack.stopwatch.StopwatchData;
import com.theflopguyproductions.ticktrack.stopwatch.StopwatchLapData;
import com.theflopguyproductions.ticktrack.ui.utils.TickTrackProgressBar;
import com.theflopguyproductions.ticktrack.utils.TickTrackDatabase;
import com.theflopguyproductions.ticktrack.utils.TickTrackStopwatchTimer;
import com.theflopguyproductions.ticktrack.utils.TickTrackThemeSetter;

import java.util.ArrayList;
import java.util.Collections;

import static android.content.Context.MODE_PRIVATE;

public class StopwatchFragment extends Fragment {

    private ConstraintLayout stopwatchRootLayout, stopwatchLapLayout;
    private TextView stopwatchValueText, stopwatchLapTitleText, stopwatchMillisText;
    private RecyclerView stopwatchLapRecyclerView;
    private TickTrackProgressBar foregroundProgressBar, backgroundProgressBar;
    private FloatingActionButton playPauseFAB, flagFAB, resetFAB;

    private Activity activity;
    private TickTrackDatabase tickTrackDatabase;

    private ArrayList<StopwatchData> stopwatchDataArrayList = new ArrayList<>();
    private ArrayList<StopwatchLapData> stopwatchLapDataArrayList = new ArrayList<>();
    private SharedPreferences sharedPreferences;
    private TickTrackStopwatchTimer tickTrackStopwatchTimer;

    private boolean isRunning = false;

    SharedPreferences.OnSharedPreferenceChangeListener sharedPreferenceChangeListener = (sharedPreferences, s) ->  {
        stopwatchDataArrayList = tickTrackDatabase.retrieveStopwatchData();
        stopwatchLapDataArrayList = tickTrackDatabase.retrieveStopwatchLapData();
        if (s.equals("StopwatchData")){
            if(stopwatchLapDataArrayList.size()>0){
                Collections.sort(stopwatchLapDataArrayList);
                tickTrackDatabase.storeLapData(stopwatchLapDataArrayList);
            }
            if(stopwatchDataArrayList.size()>0){
                if(!stopwatchDataArrayList.get(0).isRunning()){
                    killAndResetStopwatch();
                }
            }
        }
    };

    private void killAndResetStopwatch() {

    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_stopwatch, container, false);
        activity = getActivity();
        initVariables(root);
        initValues();

        checkConditions();

        setupClickListeners();

        sharedPreferences = activity.getSharedPreferences("TickTrackData", MODE_PRIVATE);
        sharedPreferences.registerOnSharedPreferenceChangeListener(sharedPreferenceChangeListener);

        return root;
    }

    private void checkConditions() {
        if(stopwatchLapDataArrayList.size()>0){
            stopwatchLapLayout.setVisibility(View.VISIBLE);
        } else {
            stopwatchLapLayout.setVisibility(View.GONE);
        }
    }

    private void setupClickListeners() {

        playPauseFAB.setOnClickListener(view -> {
            if(!isRunning){
                startStopwatch();
                isRunning = true;
            } else {
                pauseStopwatch();
                isRunning = false;
            }
        });

        resetFAB.setOnClickListener(view -> resetStopwatch());

        flagFAB.setOnClickListener(view -> lapStopwatch());

    }

    private void lapStopwatch() {
        if(tickTrackStopwatchTimer.isStarted()){
            tickTrackStopwatchTimer.lap();
        }
    }

    private void resetStopwatch() {
        if (tickTrackStopwatchTimer.isStarted()){
            tickTrackStopwatchTimer.stop();
        }
    }

    private void pauseStopwatch() {
        if(tickTrackStopwatchTimer.isStarted() && !tickTrackStopwatchTimer.isPaused()){
            tickTrackStopwatchTimer.pause();
        }
    }

    private void startStopwatch() {
        if(!tickTrackStopwatchTimer.isStarted()){
            tickTrackStopwatchTimer.start();
        } else if (tickTrackStopwatchTimer.isPaused()){
            tickTrackStopwatchTimer.resume();
        }
    }

    private void initValues() {
        tickTrackDatabase = new TickTrackDatabase(activity);
        stopwatchDataArrayList = tickTrackDatabase.retrieveStopwatchData();
        stopwatchLapDataArrayList = tickTrackDatabase.retrieveStopwatchLapData();
        tickTrackStopwatchTimer = new TickTrackStopwatchTimer(tickTrackDatabase);
        tickTrackStopwatchTimer.setTextView(stopwatchValueText, stopwatchMillisText);
    }

    private void initVariables(View parent) {

        stopwatchRootLayout = parent.findViewById(R.id.stopwatchRootLayout);
        stopwatchLapLayout = parent.findViewById(R.id.stopwatchFragmentLapLayout);
        stopwatchValueText = parent.findViewById(R.id.stopwatchFragmentTimeTextView);
        stopwatchMillisText = parent.findViewById(R.id.stopwatchFragmentMillisTextView);
        stopwatchLapTitleText = parent.findViewById(R.id.stopwatchFragmentLapTextView);
        stopwatchLapRecyclerView = parent.findViewById(R.id.stopwatchFragmentRecyclerView);
        foregroundProgressBar = parent.findViewById(R.id.stopwatchFragmentProgressForeground);
        backgroundProgressBar = parent.findViewById(R.id.stopwatchFragmentProgressBackground);
        playPauseFAB = parent.findViewById(R.id.stopwatchFragmentPlayPauseFAB);
        flagFAB = parent.findViewById(R.id.stopwatchFragmentFlagFAB);
        resetFAB = parent.findViewById(R.id.stopwatchFragmentResetFAB);

        backgroundProgressBar.setInstantProgress(1);
    }

    @Override
    public void onStart() {
        super.onStart();
        TickTrackThemeSetter.stopwatchFragmentTheme(activity, stopwatchRootLayout, stopwatchLapTitleText, stopwatchValueText,
                tickTrackDatabase, backgroundProgressBar, stopwatchMillisText);
        sharedPreferences = activity.getSharedPreferences("TickTrackData", MODE_PRIVATE);
        sharedPreferences.registerOnSharedPreferenceChangeListener(sharedPreferenceChangeListener);

        //TODO DELETE THIS MACHA
        foregroundProgressBar.setProgress(1);
    }

    @Override
    public void onStop() {
        super.onStop();
        sharedPreferences = activity.getSharedPreferences("TickTrackData", MODE_PRIVATE);
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(sharedPreferenceChangeListener);
    }
}