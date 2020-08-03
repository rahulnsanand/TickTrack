package com.theflopguyproductions.ticktrack.timer.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.app.TaskStackBuilder;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.theflopguyproductions.ticktrack.R;
import com.theflopguyproductions.ticktrack.SoYouADeveloperHuh;
import com.theflopguyproductions.ticktrack.application.TickTrack;
import com.theflopguyproductions.ticktrack.counter.CounterData;
import com.theflopguyproductions.ticktrack.counter.activity.CounterActivity;
import com.theflopguyproductions.ticktrack.counter.notification.CounterNotificationService;
import com.theflopguyproductions.ticktrack.timer.activity.TimerActivity;
import com.theflopguyproductions.ticktrack.utils.TimeAgo;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TimerService extends Service {

    public static final String ACTION_REFRESH_TIMER = "ACTION_REFRESH_TIMER";
    public static final String ACTION_STOP_TIMER_SERVICE = "ACTION_STOP_TIMER_SERVICE";
    public static final String ACTION_START_TIMER_SERVICE = "ACTION_START_TIMER_SERVICE";


    private static ArrayList<TimerServiceData> timerServiceData = new ArrayList<>();
    private ArrayList<Long> endTimes = new ArrayList<>();
    private String notificationNextTimer = "";
    NotificationCompat.Builder notificationBuilder;
    NotificationManager notificationManager;
    private NotificationManagerCompat notificationManagerCompat;
    private int timerIntegerID;
    private String timerStringID;


    public TimerService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("TAG_TIMER_SERVICE", "My foreground service onCreate().");
    }

    public static ArrayList<TimerServiceData> retrieveTimerServiceDataList(SharedPreferences sharedPreferences){

        Gson gson = new Gson();
        String json = sharedPreferences.getString("TimerServiceData", null);
        Type type = new TypeToken<ArrayList<TimerServiceData>>() {}.getType();
        ArrayList<TimerServiceData> timerServiceData = gson.fromJson(json, type);

        if(timerServiceData == null){
            timerServiceData = new ArrayList<>();
        }

        return timerServiceData;
    }
    public static void storeTimerServiceData(SharedPreferences sharedPreferences){

        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(timerServiceData);
        editor.putString("TimerServiceData", json);
        editor.apply();

    }
    private ArrayList<Long> getEndTimes() {
        ArrayList<Long> returnTimes = new ArrayList<>();
        for(int i = 0; i < timerServiceData.size(); i ++){
            returnTimes.add(timerServiceData.get(i).getEndTimeInMillis());
        }
        return returnTimes;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(intent != null) {

            String action = intent.getAction();


            initializeValues();

            assert action != null;
            switch (action) {
                case ACTION_START_TIMER_SERVICE:
                    startTimerService();
                    break;
                case ACTION_STOP_TIMER_SERVICE:
                    stopTimerService();
                    break;
                case ACTION_REFRESH_TIMER:
                    timerIntegerID = intent.getIntExtra("timerIntegerID",0);
                    timerStringID = intent.getStringExtra("timerStringID");
                    refreshTimerService();
                    break;
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private void refreshTimerService() {
        notifyNotification();
    }

    private void stopTimerService() {
        if(timerServiceData.size()==0){
            killNotifications();
        }
    }
    private void startTimerService() {

        startForegroundService();
    }

    private void initializeValues(){
        SharedPreferences sharedPreferences = getSharedPreferences("TickTrackData",MODE_PRIVATE);
        timerServiceData = retrieveTimerServiceDataList(sharedPreferences);
        endTimes = getEndTimes();
    }

    private void startForegroundService() {
        setupCustomNotification();
        startForeground(2, notificationBuilder.build());
        Toast.makeText(this, "Timer Notification created!", Toast.LENGTH_SHORT).show();
    }

    private void killNotifications(){
        stopForeground(true);
    }

    private void setupCustomNotification(){

        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManagerCompat = NotificationManagerCompat.from(getApplicationContext());

        Intent resultIntent = new Intent(this, SoYouADeveloperHuh.class);
        resultIntent.putExtra("FragmentID", 2);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addNextIntentWithParentStack(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        notificationBuilder = new NotificationCompat.Builder(this, TickTrack.COUNTER_NOTIFICATION)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setStyle(new NotificationCompat.DecoratedCustomViewStyle())
                .setDefaults(Notification.DEFAULT_ALL)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setOnlyAlertOnce(true)
                .setAutoCancel(false)
                .setContentIntent(resultPendingIntent);

        notificationBuilder.setContentTitle("TickTrack Timer");
        if(timerServiceData!=null){
            if(timerServiceData.size()>1){
                String nextOccurrence = getNextOccurrence();
                notificationBuilder.setContentText(nextOccurrence);
            } else {
                int hours = (int) (timerServiceData.get(0).getEndTimeInMillis()/(1000*60*60));
                int minutes = (int) (timerServiceData.get(0).getEndTimeInMillis()%(1000*60*60))/(1000*60);
                int seconds = (int) (timerServiceData.get(0).getEndTimeInMillis()%(1000*60*60))%(1000*60)/1000;
                String nextOccurrence = TimeAgo.getTimerDurationLeft(hours, minutes, seconds);
                notificationBuilder.setContentText(nextOccurrence);
            }
        }


        if (android.os.Build.VERSION. SDK_INT >= android.os.Build.VERSION_CODES. O ) {
            notificationBuilder.setChannelId(TickTrack.COUNTER_NOTIFICATION);
        }

    }

    private String getNextOccurrence() {
        long lowest = Collections.min(endTimes);
        int hours = (int) ((lowest-System.currentTimeMillis())/(1000*60*60));
        int minutes = (int) ((lowest-System.currentTimeMillis())%(1000*60*60))/(1000*60);
        int seconds = (int) ((lowest-System.currentTimeMillis())%(1000*60*60))%(1000*60)/1000;
        return TimeAgo.getTimerDurationLeft(hours, minutes, seconds);
    }

    public void notifyNotification(){
        if(timerServiceData!=null){
            if(timerServiceData.size()>1){
                String nextOccurrence = getNextOccurrence();
                notificationBuilder.setContentText(nextOccurrence);
            } else {
                int hours = (int) (timerServiceData.get(0).getEndTimeInMillis()/(1000*60*60));
                int minutes = (int) (timerServiceData.get(0).getEndTimeInMillis()%(1000*60*60))/(1000*60);
                int seconds = (int) (timerServiceData.get(0).getEndTimeInMillis()%(1000*60*60))%(1000*60)/1000;
                String nextOccurrence = TimeAgo.getTimerDurationLeft(hours, minutes, seconds);
                notificationBuilder.setContentText(nextOccurrence);
            }
        }

        notificationManagerCompat.notify(2, notificationBuilder.build());
    }

}
