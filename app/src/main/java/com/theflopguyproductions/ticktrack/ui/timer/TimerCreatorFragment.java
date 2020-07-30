package com.theflopguyproductions.ticktrack.ui.timer;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.theflopguyproductions.ticktrack.R;
import com.theflopguyproductions.ticktrack.timer.TimerData;
import com.theflopguyproductions.ticktrack.timer.activity.TimerActivity;
import com.theflopguyproductions.ticktrack.utils.TickTrackDatabase;
import com.theflopguyproductions.ticktrack.utils.UniqueIdGenerator;

import java.sql.Timestamp;
import java.util.ArrayList;

public class TimerCreatorFragment extends Fragment {


    private FloatingActionButton timerCreateFAB;
    private ArrayList<TimerData> timerDataArrayList= new ArrayList<>();
    private Activity activity;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_timer_creator, container, false);
        activity = getActivity();

        timerDataArrayList = TickTrackDatabase.retrieveTimerList(activity);

        timerCreateFAB = view.findViewById(R.id.timerCreateFragmentPlayFAB);

        timerCreateFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createTimer();
            }
        });

        return view;
    }

    private void createTimer() {
        TimerData timerData = new TimerData();
        timerData.setTimerCreateTimeStamp(new Timestamp(System.currentTimeMillis()));
        timerData.setTimerFlag(0);
        timerData.setTimerHour(10);
        timerData.setTimerMinute(40);
        timerData.setTimerSecond(0);
        timerData.setTimerID(UniqueIdGenerator.getUniqueTimerID());
        timerData.setTimerIntegerID(UniqueIdGenerator.getUniqueIntegerTimerID());
        timerData.setTimerOn(true);
        timerDataArrayList.add(0,timerData);
        TickTrackDatabase.storeTimerList(timerDataArrayList, activity);

        Intent timerIntent = new Intent(activity, TimerActivity.class);
        timerIntent.putExtra("timerID", UniqueIdGenerator.getUniqueTimerID());
        startActivity(timerIntent);

    }


}