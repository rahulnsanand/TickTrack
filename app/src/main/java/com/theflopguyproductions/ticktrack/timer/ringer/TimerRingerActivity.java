package com.theflopguyproductions.ticktrack.timer.ringer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.theflopguyproductions.ticktrack.R;
import com.theflopguyproductions.ticktrack.timer.TimerData;
import com.theflopguyproductions.ticktrack.timer.service.TimerRingService;
import com.theflopguyproductions.ticktrack.ui.utils.recyclerutils.ScrollingPagerIndicator;
import com.theflopguyproductions.ticktrack.ui.utils.recyclerutils.SnappingRecyclerView;
import com.theflopguyproductions.ticktrack.utils.TickTrackDatabase;
import com.theflopguyproductions.ticktrack.utils.TickTrackThemeSetter;
import com.theflopguyproductions.ticktrack.utils.TickTrackTimerDatabase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;


public class TimerRingerActivity extends AppCompatActivity {

    private ConstraintLayout rootLayout;
    private TickTrackDatabase tickTrackDatabase;
    private TickTrackTimerDatabase tickTrackTimerDatabase;
    private Context context;
    private SnappingRecyclerView timerStopRecyclerView;
    private FloatingActionButton timerStopFAB;
    private ArrayList<TimerData> timerDataArrayList;
    private ArrayList<TimerData> onlyOnTimersArrayList;
    private RingerAdapter timerStopAdapter;
    private SharedPreferences sharedPreferences;
    private ScrollingPagerIndicator recyclerIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer_ringer);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true);
            setTurnScreenOn(true);
            KeyguardManager keyguardManager = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
            if(keyguardManager!=null)
                keyguardManager.requestDismissKeyguard(this, null);
        }
        else {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                    WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                    WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON |
                    WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
        rootLayout = findViewById(R.id.timerRingActivityRootLayout);
        timerStopRecyclerView = findViewById(R.id.timerStopActivityRecyclerView);
        timerStopRecyclerView.setHasFixedSize(true);
        timerStopRecyclerView.enableViewScaling(true);
        recyclerIndicator = findViewById(R.id.indicator);

        timerStopFAB = findViewById(R.id.timerStopActivityStopFAB);

        context = this;
        tickTrackDatabase = new TickTrackDatabase(context);
        tickTrackTimerDatabase = new TickTrackTimerDatabase(context);
        timerDataArrayList = tickTrackDatabase.retrieveTimerList();
        refreshOnlyOnTimer();
        TickTrackThemeSetter.timerRecycleTheme(this, timerStopRecyclerView, tickTrackDatabase);

        buildRecyclerView(this);
        timerStopAdapter.notifyDataSetChanged();

        timerStopFAB.setOnClickListener(view -> stopTimers());
        sharedPreferences = getSharedPreferences("TickTrackData", MODE_PRIVATE);
        sharedPreferences.registerOnSharedPreferenceChangeListener(sharedPreferenceChangeListener);

    }

    private void buildRecyclerView(Activity activity) {

        timerStopAdapter = new RingerAdapter(activity, onlyOnTimersArrayList);

        if(onlyOnTimersArrayList.size()>0){
            timerStopRecyclerView.setVisibility(View.VISIBLE);

            timerStopRecyclerView.setAdapter(timerStopAdapter);
            timerStopRecyclerView.setOrientation(SnappingRecyclerView.Orientation.VERTICAL);
            recyclerIndicator.attachToRecyclerView(timerStopRecyclerView);

            timerStopAdapter.diffUtilsChangeData(onlyOnTimersArrayList);

        } else {
            onBackPressed();
        }
    }

    SharedPreferences.OnSharedPreferenceChangeListener sharedPreferenceChangeListener = (sharedPreferences, s) ->  {
        timerDataArrayList = tickTrackDatabase.retrieveTimerList();
        if (s.equals("TimerData")){
            Collections.sort(timerDataArrayList);
            tickTrackDatabase.storeTimerList(timerDataArrayList);
            refreshOnlyOnTimer();
            if(onlyOnTimersArrayList.size()>0){
                timerStopAdapter.diffUtilsChangeData(onlyOnTimersArrayList);
            } else {
                onBackPressed();
            }
        }
    };


    @Override
    public void onStop() {
        super.onStop();
        sharedPreferences = context.getSharedPreferences("TickTrackData", MODE_PRIVATE);
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(sharedPreferenceChangeListener);
    }

    @Override
    public void onResume() {
        super.onResume();
        sharedPreferences = context.getSharedPreferences("TickTrackData", MODE_PRIVATE);
        sharedPreferences.registerOnSharedPreferenceChangeListener(sharedPreferenceChangeListener);
    }

    private void stopTimers() {
        stopTimerRinging();
        if(isMyServiceRunning(TimerRingService.class, context)){
            killForeground();
        }
        finish();
        overridePendingTransition(0, R.anim.to_right);
    }
    private void killForeground() {
        Intent intent = new Intent(context, TimerRingService.class);
        intent.setAction(TimerRingService.ACTION_KILL_ALL_TIMERS);
        context.startService(intent);
    }
    private void stopTimerRinging() {
        for(int i = 0; i < timerDataArrayList.size(); i++){
            if(timerDataArrayList.get(i).isTimerRinging()){
                timerDataArrayList.get(i).setTimerOn(false);
                timerDataArrayList.get(i).setTimerPause(false);
                timerDataArrayList.get(i).setTimerNotificationOn(false);
                timerDataArrayList.get(i).setTimerRinging(false);
                tickTrackDatabase.storeTimerList(timerDataArrayList);

            }
        }
        timerDataArrayList = tickTrackDatabase.retrieveTimerList();
        timerStopAdapter.diffUtilsChangeData(timerDataArrayList);
        timerStopAdapter.notifyDataSetChanged();
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
    private void refreshOnlyOnTimer(){
        onlyOnTimersArrayList = new ArrayList<>();
        for(int i=0; i<timerDataArrayList.size(); i++){
            if(timerDataArrayList.get(i).isTimerRinging()){
                onlyOnTimersArrayList.add(timerDataArrayList.get(i));
            }
        }
    }
}