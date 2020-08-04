package com.theflopguyproductions.ticktrack.timer.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
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
import com.theflopguyproductions.ticktrack.utils.TimeAgo;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class TimerService extends Service {

//    public static final String ACTION_REFRESH_TIMER = "ACTION_REFRESH_TIMER";
    public static final String ACTION_STOP_TIMER_SERVICE = "ACTION_STOP_TIMER_SERVICE";
    public static final String ACTION_START_TIMER_SERVICE = "ACTION_START_TIMER_SERVICE";

    NotificationCompat.Builder notificationBuilder;
    NotificationManager notificationManager;
    private NotificationManagerCompat notificationManagerCompat;

    public TimerService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("TAG_TIMER_SERVICE", "My foreground service onCreate().");
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
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private void stopTimerService() {
        if(!(timerServiceData.size() >0)){
            killNotifications();
        }
    }
    private void startTimerService() {
        startForegroundService();
        refreshingEverySecond();

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
        stopSelf();
    }

    @Override
    public void onDestroy() {
        handler.removeCallbacks(refreshRunnable);
        super.onDestroy();
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
                .setOngoing(true)
                .setContentIntent(resultPendingIntent);



        updateTimerServiceData();

        if (android.os.Build.VERSION. SDK_INT >= android.os.Build.VERSION_CODES. O ) {
            notificationBuilder.setChannelId(TickTrack.COUNTER_NOTIFICATION);
        }

    }

    private static ArrayList<TimerServiceData> timerServiceData = new ArrayList<>();
    private ArrayList<Long> endTimes = new ArrayList<>();
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

    private String getNextOccurrence() {
        endTimes = getEndTimes();
        long lowest = Collections.min(endTimes);
        int hours = (int) ((lowest-System.currentTimeMillis())/(1000*60*60));
        int minutes = (int) ((lowest-System.currentTimeMillis())%(1000*60*60))/(1000*60);
        int seconds = (int) ((lowest-System.currentTimeMillis())%(1000*60*60))%(1000*60)/1000;
        System.out.println(hours+"+"+minutes+"+"+seconds);
        return TimeAgo.getTimerDurationLeft(hours, minutes, seconds);
    }

    final Handler handler = new Handler();

    final Runnable refreshRunnable = new Runnable() {
        public void run() {
            notifyNotification();
            handler.postDelayed(refreshRunnable, 1000);
        }
    };

    private void refreshingEverySecond(){
        handler.postDelayed(refreshRunnable, 1000);
    }

    private void updateTimerServiceData(){

        timerServiceData = retrieveTimerServiceDataList(getSharedPreferences("TickTrackData",MODE_PRIVATE));

        if(timerServiceData.size()>1){
            System.out.println("MORE NOTIFICATION");
            notificationBuilder.setContentTitle(timerServiceData.size()+" TickTrack timers running");
            String nextOccurrence = getNextOccurrence();
            notificationBuilder.setContentText("Next timer in "+nextOccurrence);
            notificationManagerCompat.notify(2, notificationBuilder.build());
        } else if(timerServiceData.size()==1){
            System.out.println("ONE NOTIFICATION");
            notificationBuilder.setContentTitle("TickTrack timer running");
            String nextOccurrence = getNextOccurrence();
            notificationBuilder.setContentText("Next timer in "+nextOccurrence);
            notificationManagerCompat.notify(2, notificationBuilder.build());
        } else {
            System.out.println("KILL NOTIFICATION");
            stopTimerService();
        }

    }

    public void notifyNotification(){
        updateTimerServiceData();
    }

}
