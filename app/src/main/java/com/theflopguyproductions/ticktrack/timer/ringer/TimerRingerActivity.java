package com.theflopguyproductions.ticktrack.timer.ringer;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.theflopguyproductions.ticktrack.R;
import com.theflopguyproductions.ticktrack.SoYouADeveloperHuh;
import com.theflopguyproductions.ticktrack.timer.data.TimerData;
import com.theflopguyproductions.ticktrack.timer.quick.QuickTimerData;
import com.theflopguyproductions.ticktrack.timer.service.TimerRingService;
import com.theflopguyproductions.ticktrack.utils.database.TickTrackDatabase;
import com.theflopguyproductions.ticktrack.utils.database.TickTrackTimerDatabase;
import com.theflopguyproductions.ticktrack.utils.helpers.TickTrackThemeSetter;

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
    private ArrayList<QuickTimerData> quickTimerDataArrayList;
    private ArrayList<TimerData> onlyOnTimersArrayList;
    private RingerAdapter timerStopAdapter;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer_ringer);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                |  View.SYSTEM_UI_FLAG_LOW_PROFILE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true);
            setTurnScreenOn(true);
            System.out.println("RINGER ACTIVITY BUILD ONE");
        }
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON |
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        System.out.println("RINGER ACTIVITY BUILD FLAG");

        rootLayout = findViewById(R.id.timerRingActivityRootLayout);
        timerStopRecyclerView = findViewById(R.id.timerStopActivityRecyclerView);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        timerStopRecyclerView.setLayoutManager(layoutManager);
        timerStopRecyclerView.setHasFixedSize(true);

        timerStopFAB = findViewById(R.id.timerStopActivityStopFAB);

        context = this;
        tickTrackDatabase = new TickTrackDatabase(context);
        tickTrackTimerDatabase = new TickTrackTimerDatabase(context);
        timerDataArrayList = tickTrackDatabase.retrieveTimerList();
        quickTimerDataArrayList = tickTrackDatabase.retrieveQuickTimerList();
        refreshOnlyOnTimer();
        TickTrackThemeSetter.timerRecycleTheme(this, timerStopRecyclerView, tickTrackDatabase);

        buildRecyclerView(this);
        timerStopAdapter.notifyDataSetChanged();

        timerStopFAB.setOnClickListener(view -> stopTimers());
        sharedPreferences = tickTrackDatabase.getSharedPref(this);
        sharedPreferences.registerOnSharedPreferenceChangeListener(sharedPreferenceChangeListener);

    }

    private void buildRecyclerView(Activity activity) {
        timerStopAdapter = new RingerAdapter(activity, onlyOnTimersArrayList);
        if(onlyOnTimersArrayList.size()>0){
            timerStopRecyclerView.setVisibility(View.VISIBLE);
            timerStopRecyclerView.setAdapter(timerStopAdapter);
            timerStopAdapter.diffUtilsChangeData(onlyOnTimersArrayList);
        } else {
            onBackPressed();
        }
    }

    SharedPreferences.OnSharedPreferenceChangeListener sharedPreferenceChangeListener = (sharedPreferences, s) ->  {
        timerDataArrayList = tickTrackDatabase.retrieveTimerList();
        quickTimerDataArrayList = tickTrackDatabase.retrieveQuickTimerList();
        if (s.equals("TimerData") || s.equals("QuickTimerData")){
            Collections.sort(timerDataArrayList);
            tickTrackDatabase.storeTimerList(timerDataArrayList);
            tickTrackDatabase.storeQuickTimerList(quickTimerDataArrayList);
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
        sharedPreferences = tickTrackDatabase.getSharedPref(this);
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(sharedPreferenceChangeListener);
        System.out.println("ON STOP RINGER ACTIVITY");
    }

    @Override
    public void onResume() {
        super.onResume();
        timerDataArrayList = tickTrackDatabase.retrieveTimerList();
        quickTimerDataArrayList = tickTrackDatabase.retrieveQuickTimerList();
        refreshOnlyOnTimer();
        if(!(onlyOnTimersArrayList.size() >0)){
            startActivity(new Intent(this, SoYouADeveloperHuh.class));
        }
        sharedPreferences = tickTrackDatabase.getSharedPref(this);
        sharedPreferences.registerOnSharedPreferenceChangeListener(sharedPreferenceChangeListener);
    }

    private void stopTimers() {
        stopTimerRinging();
        if(isMyServiceRunning(TimerRingService.class, context)){
            killForeground();
        }
        onStop();
        Intent a = new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(a);
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

        quickTimerDataArrayList = tickTrackDatabase.retrieveQuickTimerList();
        ArrayList<QuickTimerData> newQuickTimerList = quickTimerDataArrayList;
        int size = quickTimerDataArrayList.size();
        int count = 0;
        while(count<size){
            System.out.println("WHILE RAN");
            for(int i=0; i<quickTimerDataArrayList.size(); i++){
                System.out.println(quickTimerDataArrayList.size()+" TIMER DATA SIZE");
                System.out.println(i+" TIMER DATA NUMBER");
                if(quickTimerDataArrayList.get(i).isTimerRinging()){
                    quickTimerDataArrayList.get(i).setTimerOn(false);
                    quickTimerDataArrayList.get(i).setTimerPause(false);
                    quickTimerDataArrayList.get(i).setTimerRinging(false);
                    tickTrackDatabase.storeQuickTimerList(quickTimerDataArrayList);
                    removeQuickTimer(quickTimerDataArrayList.get(i).getTimerID(), newQuickTimerList);
                }
            }
            count++;
        }

        if(!(newQuickTimerList.size() > 0)){
            tickTrackDatabase.storeQuickTimerList(new ArrayList<>());
        } else {
            tickTrackDatabase.storeQuickTimerList(newQuickTimerList);
        }

        quickTimerDataArrayList = tickTrackDatabase.retrieveQuickTimerList();
        timerDataArrayList = tickTrackDatabase.retrieveTimerList();
        refreshOnlyOnTimer();
        timerStopAdapter.diffUtilsChangeData(onlyOnTimersArrayList);
        timerStopAdapter.notifyDataSetChanged();
    }
    private void removeQuickTimer(String timerID, ArrayList<QuickTimerData> newTimerList) {
        for(int i=0; i<newTimerList.size(); i++){
            if(newTimerList.get(i).getTimerID().equals(timerID)){
                newTimerList.remove(i);
                return;
            }
        }
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
        for(int i=0; i<quickTimerDataArrayList.size(); i++){
            if(quickTimerDataArrayList.get(i).isTimerRinging()){
                TimerData timerData = new TimerData();
                timerData.setTimerLastEdited(quickTimerDataArrayList.get(i).getTimerLastEdited());
                timerData.setTimerHour(quickTimerDataArrayList.get(i).getTimerHour());
                timerData.setTimerMinute(quickTimerDataArrayList.get(i).getTimerMinute());
                timerData.setTimerSecond(quickTimerDataArrayList.get(i).getTimerSecond());
                timerData.setTimerID(quickTimerDataArrayList.get(i).getTimerID());
                timerData.setTimerIntID(quickTimerDataArrayList.get(i).getTimerIntID());
                timerData.setQuickTimer(quickTimerDataArrayList.get(i).isQuickTimer());
                timerData.setTimerStartTimeInMillis(quickTimerDataArrayList.get(i).getTimerStartTimeInMillis());
                timerData.setTimerAlarmEndTimeInMillis(quickTimerDataArrayList.get(i).getTimerAlarmEndTimeInMillis());
                timerData.setTimerEndedTimeInMillis(quickTimerDataArrayList.get(i).getTimerEndedTimeInMillis());
                timerData.setTimerTotalTimeInMillis(quickTimerDataArrayList.get(i).getTimerTotalTimeInMillis());
                timerData.setTimerPause(quickTimerDataArrayList.get(i).isTimerPause());
                timerData.setTimerOn(quickTimerDataArrayList.get(i).isTimerOn());
                timerData.setTimerRinging(quickTimerDataArrayList.get(i).isTimerRinging());
                onlyOnTimersArrayList.add(timerData);
            }
        }
    }
}