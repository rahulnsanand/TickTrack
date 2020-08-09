package com.theflopguyproductions.ticktrack.timer.ringer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.theflopguyproductions.ticktrack.R;
import com.theflopguyproductions.ticktrack.timer.TimerData;
import com.theflopguyproductions.ticktrack.utils.TickTrackDatabase;
import com.theflopguyproductions.ticktrack.utils.TickTrackThemeSetter;
import com.theflopguyproductions.ticktrack.utils.TickTrackTimerDatabase;

import java.util.ArrayList;
import java.util.Collections;


public class TimerRingerActivity extends AppCompatActivity {

    private ConstraintLayout rootLayout;
    private TickTrackDatabase tickTrackDatabase;
    private TickTrackTimerDatabase tickTrackTimerDatabase;
    private Context context;
    private RecyclerView timerStopRecyclerView;
    private FloatingActionButton timerStopFAB;
    private ArrayList<TimerData> timerDataArrayList;
    private RingerAdapter timerStopAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer_ringer);

        rootLayout = findViewById(R.id.timerRingActivityRootLayout);
        timerStopRecyclerView = findViewById(R.id.timerStopActivityRecyclerView);
        timerStopFAB = findViewById(R.id.timerStopActivityStopFAB);

        context = this;
        tickTrackDatabase = new TickTrackDatabase(context);
        tickTrackTimerDatabase = new TickTrackTimerDatabase(context);
        timerDataArrayList = tickTrackDatabase.retrieveTimerList();

        TickTrackThemeSetter.timerRecycleTheme(this, timerStopRecyclerView, tickTrackDatabase);

        buildRecyclerView(this);

    }

    private void buildRecyclerView(Activity activity) {

        timerStopAdapter = new RingerAdapter(activity, timerDataArrayList);

        if(timerDataArrayList.size()>0){
            timerStopRecyclerView.setVisibility(View.VISIBLE);

            Collections.sort(timerDataArrayList);

            timerStopRecyclerView.setLayoutManager(new LinearLayoutManager(activity));
            timerStopRecyclerView.setItemAnimator(new DefaultItemAnimator());
            timerStopRecyclerView.setAdapter(timerStopAdapter);

            timerStopAdapter.diffUtilsChangeData(timerDataArrayList);

        }
    }
}