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
import com.theflopguyproductions.ticktrack.application.TickTrack;
import com.theflopguyproductions.ticktrack.counter.activity.CounterActivity;
import com.theflopguyproductions.ticktrack.timer.TimerData;
import com.theflopguyproductions.ticktrack.timer.activity.TimerVisibleActivity;
import com.theflopguyproductions.ticktrack.timer.ringer.TimerRingerActivity;
import com.theflopguyproductions.ticktrack.utils.TimeAgo;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;

public class TimerRingService extends Service {

    public static final String ACTION_ADD_TIMER_FINISH = "ACTION_ADD_TIMER_FINISH";
    public static final String ACTION_KILL_ALL_TIMERS = "ACTION_KILL_ALL_TIMERS";

    private NotificationCompat.Builder notificationBuilder;
    private NotificationManagerCompat notificationManagerCompat;

    private static ArrayList<TimerData> timerDataArrayList = new ArrayList<>();
    private int currentTimerPosition, timerCount = 0;
    final Handler handler = new Handler();

    public TimerRingService(){
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("TAG_TIMER_RANG_SERVICE", "My foreground service onCreate().");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    public static ArrayList<TimerData> retrieveTimerList(SharedPreferences sharedPreferences){

        Gson gson = new Gson();
        String json = sharedPreferences.getString("TimerData", null);
        Type type = new TypeToken<ArrayList<TimerData>>() {}.getType();
        ArrayList<TimerData> timerDataArrayList = gson.fromJson(json, type);

        if(timerDataArrayList == null){
            timerDataArrayList = new ArrayList<>();
        }

        return timerDataArrayList;
    }
    public static void storeTimerList(SharedPreferences sharedPreferences){

        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(timerDataArrayList);
        editor.putString("TimerData", json);
        editor.apply();

    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(intent != null) {

            String action = intent.getAction();

            initializeValues();

            assert action != null;
            switch (action) {
                case ACTION_ADD_TIMER_FINISH:
                    startForegroundService();
                    break;
                case ACTION_KILL_ALL_TIMERS:
                    stopTimers();
                    break;
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private void stopTimers() {
        stopTimerRinging(getSharedPreferences("TickTrackData",MODE_PRIVATE));
        stopForeground(true);
        stopSelf();
    }

    private void initializeValues() {
        SharedPreferences sharedPreferences = getSharedPreferences("TickTrackData", MODE_PRIVATE);
        timerDataArrayList = retrieveTimerList(sharedPreferences);
        timerCount = getAllOnTimers();
        if(timerCount>0){
            if(timerCount>1){
                setupStopAllNotification();
            } else {
                setupCustomNotification();
                if(getSingleOnTimer()!=-1){
                    currentTimerPosition = getCurrentTimerPosition(getSingleOnTimer());
                }
            }
        }
    }
    float UpdateTime = 0L;
    final Runnable refreshRunnable = new Runnable() {
        public void run() {
            UpdateTime += 1;
            updateStopTimeText();
            notifyNotification();
            handler.postDelayed(refreshRunnable, 1000);
        }
    };

    private void updateStopTimeText() {

        float totalSeconds = UpdateTime;
        float hours = totalSeconds/3600;
        float minutes = totalSeconds/60%60;
        float seconds = totalSeconds%60;

        String hourLeft = String.format(Locale.getDefault(),"-%02d:%02d:%02d",(int)hours,(int)minutes,(int)seconds);
        notificationBuilder.setContentText(hourLeft);
    }

    private void refreshingEverySecond(){
        handler.postDelayed(refreshRunnable, 1000);
    }

    public void notifyNotification(){
        updateTimerServiceData();
    }
    private void updateTimerServiceData(){

        timerDataArrayList = retrieveTimerList(getSharedPreferences("TickTrackData",MODE_PRIVATE));
        int OnTimers = getAllOnTimers();
        if(OnTimers==1){
            System.out.println("ONE NOTIFICATION");
            notificationBuilder.setContentTitle("TickTrack timer running");
            String nextOccurrence = "getNextOccurrence()";
            notificationBuilder.setContentText("Next timer in "+nextOccurrence);
            notificationManagerCompat.notify(2, notificationBuilder.build());
        } else {
            System.out.println("KILL NOTIFICATION");
            stopRingerService();
        }
    }

    private void startForegroundService() {
        startForeground(1, notificationBuilder.build());
        Toast.makeText(this, "Timer Complete!", Toast.LENGTH_SHORT).show();
    }

    private void stopRingerService() {
        timerDataArrayList = retrieveTimerList(getSharedPreferences("TickTrackData",MODE_PRIVATE));
        if(!(getAllOnTimers() > 0)){
            stopTimers();
        }
    }

    private void setupStopAllNotification() {

        handler.removeCallbacks(refreshRunnable);

        notificationManagerCompat = NotificationManagerCompat.from(getApplicationContext());

        Intent killTimerIntent = new Intent(this, TimerRingService.class);
        killTimerIntent.setAction(ACTION_KILL_ALL_TIMERS);
        PendingIntent killTimerPendingIntent = PendingIntent.getService(this, 4, killTimerIntent, 0);
        NotificationCompat.Action killTimers = new NotificationCompat.Action(R.drawable.ic_add_white_24, "Stop all", killTimerPendingIntent);

        Intent resultIntent = new Intent(this, TimerRingerActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addNextIntentWithParentStack(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        notificationBuilder = new NotificationCompat.Builder(this,TickTrack.COUNTER_NOTIFICATION)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setStyle(new NotificationCompat.DecoratedCustomViewStyle())
                .setDefaults(Notification.DEFAULT_ALL)
                .setPriority(Notification.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_CALL)
                .setVibrate(new long[0])
                .setOnlyAlertOnce(true)
                .setOngoing(true)
                .setContentIntent(resultPendingIntent);

        notificationBuilder.addAction(killTimers);

        notificationBuilder.setContentTitle("TickTrack Timers");
        notificationBuilder.setContentText(timerCount+" timers complete");

        if (android.os.Build.VERSION. SDK_INT >= android.os.Build.VERSION_CODES. O ) {
            notificationBuilder.setChannelId(TickTrack.COUNTER_NOTIFICATION);
        }
    }

    private void setupCustomNotification(){

        notificationManagerCompat = NotificationManagerCompat.from(getApplicationContext());

        Intent killTimerIntent = new Intent(this, TimerRingService.class);
        killTimerIntent.setAction(ACTION_KILL_ALL_TIMERS);
        PendingIntent killTimerPendingIntent = PendingIntent.getService(this, 4, killTimerIntent, 0);
        NotificationCompat.Action killTimers = new NotificationCompat.Action(R.drawable.ic_add_white_24, "Stop", killTimerPendingIntent);

        Intent resultIntent = new Intent(this, TimerVisibleActivity.class);
        resultIntent.putExtra("timerID",timerDataArrayList.get(currentTimerPosition).getTimerStringID());
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addNextIntentWithParentStack(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        notificationBuilder = new NotificationCompat.Builder(this,TickTrack.COUNTER_NOTIFICATION)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setStyle(new NotificationCompat.DecoratedCustomViewStyle())
                .setDefaults(Notification.DEFAULT_ALL)
                .setPriority(Notification.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_CALL)
                .setVibrate(new long[0])
                .setOnlyAlertOnce(true)
                .setOngoing(true)
                .setContentIntent(resultPendingIntent);

        notificationBuilder.addAction(killTimers);

        String timerLabel = timerDataArrayList.get(currentTimerPosition).getTimerLabel();
        if(!timerLabel.equals("Set label")){
            notificationBuilder.setContentTitle("TickTrack Timer: "+timerDataArrayList.get(currentTimerPosition).getTimerLabel());
        } else {
            notificationBuilder.setContentTitle("TickTrack Timer");
        }

        if (android.os.Build.VERSION. SDK_INT >= android.os.Build.VERSION_CODES. O ) {
            notificationBuilder.setChannelId(TickTrack.COUNTER_NOTIFICATION);
        }

        if(timerDataArrayList.get(currentTimerPosition).getTimerEndedTimeInMillis() != -1){
            long stopTimeRetrieve = timerDataArrayList.get(currentTimerPosition).getTimerEndedTimeInMillis();
            UpdateTime = (System.currentTimeMillis() - stopTimeRetrieve) / 1000F;
        } else {
            timerDataArrayList.get(currentTimerPosition).setTimerEndedTimeInMillis(System.currentTimeMillis());
        }

        refreshingEverySecond();
    }

    private int getSingleOnTimer() {
        for(int i=0; i<timerDataArrayList.size(); i++){
            if(timerDataArrayList.get(i).isTimerRinging()){
                return timerDataArrayList.get(i).getTimerIntegerID();
            }
        }
        return -1;
    }
    private int getAllOnTimers() {
        int result = 0;
        for(int i = 0; i < timerDataArrayList.size(); i ++){
            if(timerDataArrayList.get(i).isTimerRinging()){
                result++;
            }
        }

        return result;
    }
    private int getCurrentTimerPosition(int timerIntegerID){
        for(int i = 0; i < timerDataArrayList.size(); i ++){
            if(timerDataArrayList.get(i).getTimerIntegerID()==timerIntegerID){
                return i;
            }
        }
        return -1;
    }
    private void stopTimerRinging(SharedPreferences sharedPreferences) {
        for(int i = 0; i < timerDataArrayList.size(); i++){
            if(timerDataArrayList.get(i).isTimerRinging()){
                timerDataArrayList.get(i).setTimerOn(false);
                timerDataArrayList.get(i).setTimerPause(false);
                timerDataArrayList.get(i).setTimerStop(true);
                timerDataArrayList.get(i).setTimerReset(true);
                timerDataArrayList.get(i).setTimerNotificationOn(false);
                timerDataArrayList.get(i).setTimerRinging(false);
                storeTimerList(sharedPreferences);
                return;
            }
        }
    }
}
