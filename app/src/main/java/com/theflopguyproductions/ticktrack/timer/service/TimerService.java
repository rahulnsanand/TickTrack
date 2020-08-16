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
import com.theflopguyproductions.ticktrack.timer.TimerData;
import com.theflopguyproductions.ticktrack.utils.helpers.TimeAgo;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

public class TimerService extends Service {

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
        timerDataArrayList = retrieveTimerDataList(getSharedPreferences("TickTrackData",MODE_PRIVATE));
        if(getAllOnTimers() == 0){
            System.out.println("KILL HERE ALSO CAME");
            stopForeground(true);
            killNotifications();
        }
    }

    private void startTimerService() {
        startForegroundService();
        refreshingEverySecond();
    }

    private void initializeValues(){
        SharedPreferences sharedPreferences = getSharedPreferences("TickTrackData",MODE_PRIVATE);
        timerDataArrayList = retrieveTimerDataList(sharedPreferences);
        endTimes = getEndTimes();
    }

    private void startForegroundService() {
        setupCustomNotification();
        startForeground(2, notificationBuilder.build());
        Toast.makeText(this, "Timer Notification created!", Toast.LENGTH_SHORT).show();
    }

    private void killNotifications(){

        handler.removeCallbacks(refreshRunnable);
        stopSelf();
        onDestroy();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(refreshRunnable);
    }

    private void setupCustomNotification(){

        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManagerCompat = NotificationManagerCompat.from(getApplicationContext());

        Intent resultIntent = new Intent(this, SoYouADeveloperHuh.class);
        resultIntent.putExtra("FragmentID", 2);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addNextIntentWithParentStack(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(2, PendingIntent.FLAG_UPDATE_CURRENT);

        notificationBuilder = new NotificationCompat.Builder(this, TickTrack.TIMER_RUNNING_NOTIFICATION)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setStyle(new NotificationCompat.DecoratedCustomViewStyle())
                .setDefaults(Notification.DEFAULT_ALL)
                .setPriority(NotificationCompat.PRIORITY_MIN)
                .setOnlyAlertOnce(true)
                .setOngoing(true)
                .setContentIntent(resultPendingIntent);

        updateTimerServiceData();

        if (android.os.Build.VERSION. SDK_INT >= android.os.Build.VERSION_CODES. O ) {
            notificationBuilder.setChannelId(TickTrack.TIMER_RUNNING_NOTIFICATION);
        }

    }

    private static ArrayList<TimerData> timerDataArrayList = new ArrayList<>();
    private ArrayList<Long> endTimes = new ArrayList<>();
    public static ArrayList<TimerData> retrieveTimerDataList(SharedPreferences sharedPreferences){
        Gson gson = new Gson();
        String json = sharedPreferences.getString("TimerData", null);
        Type type = new TypeToken<ArrayList<TimerData>>() {}.getType();
        ArrayList<TimerData> timerData = gson.fromJson(json, type);
        if(timerData == null){
            timerData = new ArrayList<>();
        }
        return timerData;
    }
    private ArrayList<Long> getEndTimes() {
        ArrayList<Long> returnTimes = new ArrayList<>();
        for(int i = 0; i < timerDataArrayList.size(); i ++){
            if(timerDataArrayList.get(i).getTimerAlarmEndTimeInMillis()!=-1){
                returnTimes.add(timerDataArrayList.get(i).getTimerAlarmEndTimeInMillis());
            }
        }
        return returnTimes;
    }

    private String getNextOccurrence() {
        endTimes = getEndTimes();
        long lowest = Collections.min(endTimes);
        long differenceFromNow = lowest - System.currentTimeMillis();
        int hours = (int) TimeUnit.MILLISECONDS.toHours(differenceFromNow);
        int minutes = (int) (TimeUnit.MILLISECONDS.toMinutes(differenceFromNow) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(differenceFromNow)));
        int seconds = (int) (TimeUnit.MILLISECONDS.toSeconds(differenceFromNow) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(differenceFromNow)));

        return TimeAgo.getTimerDurationLeft(hours, minutes, seconds);
    }

    final Handler handler = new Handler();

    final Runnable refreshRunnable = new Runnable() {
        public void run() {
            notifyNotification();
            handler.postDelayed(refreshRunnable, 1000);
            System.out.println("UpdateNotification");
        }
    };

    private void refreshingEverySecond(){
        handler.postDelayed(refreshRunnable, 1000);
    }

    private void updateTimerServiceData(){

        timerDataArrayList = retrieveTimerDataList(getSharedPreferences("TickTrackData",MODE_PRIVATE));
        int OnTimers = getAllOnTimers();
        System.out.println("NOTIFICATION ON TIMER COUNT "+OnTimers);
        if(OnTimers>1){
            System.out.println("MORE NOTIFICATION");
            notificationBuilder.setContentTitle(OnTimers+" TickTrack timers running");
            String nextOccurrence = getNextOccurrence();
            notificationBuilder.setContentText("Next timer in "+nextOccurrence);
            notificationManagerCompat.notify(2, notificationBuilder.build());
        } else if(OnTimers==1){
            System.out.println("ONE NOTIFICATION");
            notificationBuilder.setContentTitle("TickTrack timer running");
            String nextOccurrence = getNextOccurrence();
            notificationBuilder.setContentText("Next timer in "+nextOccurrence);
            notificationManagerCompat.notify(2, notificationBuilder.build());
        } else {
            System.out.println("KILL NOTIFICATION >>>"+getAllOnTimers());
            stopTimerService();
        }
    }

    private int getAllOnTimers() {
        int result = 0;
        for(int i = 0; i < timerDataArrayList.size(); i ++){
            if(timerDataArrayList.get(i).isTimerNotificationOn()){
                result++;
            }
        }
        return result;
    }

    public void notifyNotification(){
        updateTimerServiceData();
    }

}
