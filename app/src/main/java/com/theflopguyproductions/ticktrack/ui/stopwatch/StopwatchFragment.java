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
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.theflopguyproductions.ticktrack.R;
import com.theflopguyproductions.ticktrack.stopwatch.StopwatchAdapter;
import com.theflopguyproductions.ticktrack.stopwatch.StopwatchData;
import com.theflopguyproductions.ticktrack.stopwatch.StopwatchLapData;
import com.theflopguyproductions.ticktrack.ui.utils.TickTrackAnimator;
import com.theflopguyproductions.ticktrack.ui.utils.TickTrackProgressBar;
import com.theflopguyproductions.ticktrack.utils.database.TickTrackDatabase;
import com.theflopguyproductions.ticktrack.utils.helpers.TickTrackThemeSetter;
import com.theflopguyproductions.ticktrack.utils.runnable.TickTrackStopwatch;

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
    private TickTrackStopwatch tickTrackStopwatchTimer;

    private static StopwatchAdapter stopwatchAdapter;





    SharedPreferences.OnSharedPreferenceChangeListener sharedPreferenceChangeListener = (sharedPreferences, s) ->  {
        if (s.equals("StopwatchLapData")){
            stopwatchLapDataArrayList = tickTrackDatabase.retrieveStopwatchLapData();
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
        initValues();
        buildRecyclerView(activity);
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

        if(stopwatchDataArrayList.get(0).isRunning() && !stopwatchDataArrayList.get(0).isPause()){
            System.out.println("checkConditions StartStopwatch Called");
            resumeStopwatch();
        } else if(stopwatchDataArrayList.get(0).isPause()){
            System.out.println("checkConditions Pause Stopwatch Called");
            pauseStopwatch();
        } else {
            System.out.println("checkConditions RESET Stopwatch Called");
            resetStopwatch();
        }
    }

    private void resumeStopwatch() {
        if(stopwatchDataArrayList.get(0).isRunning() && !stopwatchDataArrayList.get(0).isPause()){
            tickTrackStopwatchTimer.setupResumeValues();
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
            } else if(stopwatchDataArrayList.get(0).isRunning() && stopwatchDataArrayList.get(0).isPause()) {
                System.out.println("START STOPWATCH CONDITION CLICK");
                TickTrackAnimator.fabBounce(playPauseFAB, ContextCompat.getDrawable(activity, R.drawable.ic_round_pause_white_24));
                TickTrackAnimator.fabDissolve(resetFAB);
                TickTrackAnimator.fabUnDissolve(flagFAB);
                startStopwatch();
            } else if(!stopwatchDataArrayList.get(0).isPause() && stopwatchDataArrayList.get(0).isRunning()) {
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
        if(stopwatchDataArrayList.get(0).isRunning()){
            tickTrackStopwatchTimer.lap();
        }
    }



    private void resetStopwatch() {
        TickTrackAnimator.fabDissolve(resetFAB);
        if (stopwatchDataArrayList.get(0).isRunning()){
            tickTrackStopwatchTimer.stop();
            stopwatchDataArrayList.get(0).setRunning(false);
            stopwatchDataArrayList.get(0).setPause(false);
            stopwatchDataArrayList.get(0).setRecentLocalTimeInMillis(0);
            stopwatchDataArrayList.get(0).setRecentRealTimeInMillis(0);
            stopwatchDataArrayList.get(0).setLastLapEndTimeInMillis(0);
            stopwatchDataArrayList.get(0).setLastUpdatedValueInMillis(0);
            stopwatchDataArrayList.get(0).setNotification(false);
            tickTrackDatabase.storeStopwatchData(stopwatchDataArrayList);
            stopwatchDataArrayList = tickTrackDatabase.retrieveStopwatchData();
        }
    }

    private void pauseStopwatch() {
        if(stopwatchDataArrayList.get(0).isRunning() && !stopwatchDataArrayList.get(0).isPause()){
            System.out.println("PAUSE KIYSA");
            tickTrackStopwatchTimer.pause();
            stopwatchDataArrayList.get(0).setRunning(true);
            stopwatchDataArrayList.get(0).setPause(true);
            tickTrackDatabase.storeStopwatchData(stopwatchDataArrayList);
            stopwatchDataArrayList = tickTrackDatabase.retrieveStopwatchData();
        }
    }

    private void startStopwatch() {
        if(!stopwatchDataArrayList.get(0).isRunning()){
            tickTrackStopwatchTimer.start();
        } else if (stopwatchDataArrayList.get(0).isPause()){
            tickTrackStopwatchTimer.resume();
        }
    }

    private void initValues() {

        stopwatchDataArrayList = tickTrackDatabase.retrieveStopwatchData();
        stopwatchLapDataArrayList = tickTrackDatabase.retrieveStopwatchLapData();
        tickTrackStopwatchTimer = new TickTrackStopwatch(activity);
        tickTrackStopwatchTimer.setGraphics(stopwatchValueText, stopwatchMillisText, foregroundProgressBar);

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
            tickTrackStopwatchTimer.setupPauseValues();
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
        stopwatchDataArrayList = tickTrackDatabase.retrieveStopwatchData();
        stopwatchLapDataArrayList = tickTrackDatabase.retrieveStopwatchLapData();
        System.out.println(stopwatchDataArrayList.get(0).isPause()+"<<<ISPAUSE ONSTART");
        System.out.println(stopwatchDataArrayList.get(0).isRunning()+"<<<ISRUNNING ONSTART");
        checkConditions();
        setupClickListeners();

    }

    @Override
    public void onStop() {
        super.onStop();
        sharedPreferences = activity.getSharedPreferences("TickTrackData", MODE_PRIVATE);
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(sharedPreferenceChangeListener);
        tickTrackStopwatchTimer.onStopCalled();

    }
}