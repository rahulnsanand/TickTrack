package com.theflopguyproductions.ticktrack.ui.stopwatch;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.theflopguyproductions.ticktrack.R;
import com.theflopguyproductions.ticktrack.application.TickTrack;
import com.theflopguyproductions.ticktrack.counter.CounterAdapter;
import com.theflopguyproductions.ticktrack.stopwatch.StopwatchAdapter;
import com.theflopguyproductions.ticktrack.stopwatch.StopwatchData;
import com.theflopguyproductions.ticktrack.stopwatch.StopwatchLapData;
import com.theflopguyproductions.ticktrack.ui.utils.TickTrackAnimator;
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

    private static StopwatchAdapter stopwatchAdapter;



    SharedPreferences.OnSharedPreferenceChangeListener sharedPreferenceChangeListener = (sharedPreferences, s) ->  {
        stopwatchDataArrayList = tickTrackDatabase.retrieveStopwatchData();
        stopwatchLapDataArrayList = tickTrackDatabase.retrieveStopwatchLapData();
        if (s.equals("StopwatchLapData")){
            if(stopwatchLapDataArrayList.size()>0){
                Collections.sort(stopwatchLapDataArrayList);
                stopwatchAdapter.diffUtilsChangeData(stopwatchLapDataArrayList);
            }
            buildRecyclerView(activity);
        }
        if(s.equals("StopwatchData")){
            stopwatchDataArrayList = tickTrackDatabase.retrieveStopwatchData();
        }
    };

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_stopwatch, container, false);
        activity = getActivity();
        initVariables(root);
        tickTrackDatabase = new TickTrackDatabase(activity);

        sharedPreferences = activity.getSharedPreferences("TickTrackData", MODE_PRIVATE);
        sharedPreferences.registerOnSharedPreferenceChangeListener(sharedPreferenceChangeListener);

        return root;
    }

    private void buildRecyclerView(Activity activity) {

        stopwatchAdapter = new StopwatchAdapter(stopwatchLapDataArrayList);

        if(stopwatchLapDataArrayList.size()>0){

            stopwatchLapLayout.setVisibility(View.VISIBLE);

            Collections.sort(stopwatchLapDataArrayList);

            stopwatchLapRecyclerView.setLayoutManager(new LinearLayoutManager(activity));
            stopwatchLapRecyclerView.setItemAnimator(new DefaultItemAnimator());
            stopwatchLapRecyclerView.setAdapter(stopwatchAdapter);

            stopwatchAdapter.diffUtilsChangeData(stopwatchLapDataArrayList);

        } else {
            stopwatchLapLayout.setVisibility(View.GONE);
        }

    }

    private void checkConditions() {
        if(stopwatchLapDataArrayList.size()>0){
            System.out.println("checkConditions Lap Data Size More than 0");

            stopwatchLapLayout.setVisibility(View.VISIBLE);
        } else {
            System.out.println("checkConditions Lap Data Size NOT More than 0");
            stopwatchLapLayout.setVisibility(View.GONE);
        }

        if(tickTrackStopwatchTimer.isStarted() && !tickTrackStopwatchTimer.isPaused()){
            System.out.println("checkConditions StartStopwatch Called");
            startStopwatch();
        } else if(tickTrackStopwatchTimer.isPaused()){
            System.out.println("checkConditions Pause Stopwatch Called");
            pauseStopwatch();
        } else {
            System.out.println("checkConditions RESET Stopwatch Called");
            resetStopwatch();
        }
    }

    private void setupClickListeners() {

        playPauseFAB.setOnClickListener(view -> {
            if(!stopwatchDataArrayList.get(0).isRunning()){
                System.out.println("START STOPWATCH CONDITION CLICK");
                TickTrackAnimator.fabBounce(playPauseFAB, ContextCompat.getDrawable(activity, R.drawable.ic_round_pause_white_24));
                TickTrackAnimator.fabDissolve(resetFAB);
                TickTrackAnimator.fabUnDissolve(flagFAB);
                startStopwatch();
            } else {
                System.out.println("PAUSE STOPWATCH CONDITION CLICK");
                TickTrackAnimator.fabBounce(playPauseFAB, ContextCompat.getDrawable(activity, R.drawable.ic_round_play_white_24));
                TickTrackAnimator.fabUnDissolve(resetFAB);
                TickTrackAnimator.fabDissolve(flagFAB);
                pauseStopwatch();
            }
        });

        resetFAB.setOnClickListener(view -> {
            System.out.println("RESET STOPWATCH CONDITION CLICK");
            resetStopwatch();
        });
        flagFAB.setOnClickListener(view -> {
            System.out.println("LAP STOPWATCH CONDITION CLICK");
            lapStopwatch();
        });
    }

    private void lapStopwatch() {
        if(tickTrackStopwatchTimer.isStarted()){
            tickTrackStopwatchTimer.lap();
        }
    }



    private void resetStopwatch() {
        TickTrackAnimator.fabDissolve(resetFAB);
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

        stopwatchDataArrayList = tickTrackDatabase.retrieveStopwatchData();
        stopwatchLapDataArrayList = tickTrackDatabase.retrieveStopwatchLapData();
        tickTrackStopwatchTimer = new TickTrackStopwatchTimer(tickTrackDatabase);
        tickTrackStopwatchTimer.setTextView(stopwatchValueText, stopwatchMillisText, foregroundProgressBar);

        if(stopwatchDataArrayList.get(0).isRunning() && !stopwatchDataArrayList.get(0).isPause()){
            System.out.println("Init Values Got Running and Not Paused");
            TickTrackAnimator.fabBounce(playPauseFAB, ContextCompat.getDrawable(activity, R.drawable.ic_round_pause_white_24));
            TickTrackAnimator.fabDissolve(resetFAB);
            TickTrackAnimator.fabUnDissolve(flagFAB);
        } else if(!stopwatchDataArrayList.get(0).isRunning()){
            System.out.println("Init Values Got Not Running");

            TickTrackAnimator.fabBounce(playPauseFAB, ContextCompat.getDrawable(activity, R.drawable.ic_round_play_white_24));
            TickTrackAnimator.fabDissolve(resetFAB);
            TickTrackAnimator.fabDissolve(flagFAB);
        } else if(stopwatchDataArrayList.get(0).isRunning() && stopwatchDataArrayList.get(0).isPause()){
            System.out.println("Init Values Got Running and Paused");

            TickTrackAnimator.fabBounce(playPauseFAB, ContextCompat.getDrawable(activity, R.drawable.ic_round_play_white_24));
            TickTrackAnimator.fabUnDissolve(resetFAB);
            TickTrackAnimator.fabDissolve(flagFAB);
        } else {
            System.out.println("Init Values Got ELSE");

            TickTrackAnimator.fabBounce(playPauseFAB, ContextCompat.getDrawable(activity, R.drawable.ic_round_play_white_24));
            TickTrackAnimator.fabDissolve(resetFAB);
            TickTrackAnimator.fabDissolve(flagFAB);
        }

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
        foregroundProgressBar.setLinearProgress(true);
        foregroundProgressBar.setSpinSpeed(2.500f);
    }

    @Override
    public void onStart() {
        super.onStart();
        TickTrackThemeSetter.stopwatchFragmentTheme(activity, stopwatchRootLayout, stopwatchLapTitleText, stopwatchValueText,
                tickTrackDatabase, backgroundProgressBar, stopwatchMillisText);
        sharedPreferences = activity.getSharedPreferences("TickTrackData", MODE_PRIVATE);
        sharedPreferences.registerOnSharedPreferenceChangeListener(sharedPreferenceChangeListener);

        initValues();
        buildRecyclerView(activity);
        checkConditions();
        setupClickListeners();

    }

    @Override
    public void onStop() {
        super.onStop();
        sharedPreferences = activity.getSharedPreferences("TickTrackData", MODE_PRIVATE);
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(sharedPreferenceChangeListener);
        tickTrackStopwatchTimer.weAreDying();
    }
}