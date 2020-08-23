package com.theflopguyproductions.ticktrack.stopwatch.service;

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
import com.theflopguyproductions.ticktrack.stopwatch.StopwatchData;
import com.theflopguyproductions.ticktrack.utils.helpers.TimeAgo;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class StopwatchNotificationService extends Service {


    NotificationCompat.Builder notificationBuilder;
    NotificationManager notificationManager;
    private NotificationManagerCompat notificationManagerCompat;

    private static ArrayList<StopwatchData> stopwatchData = new ArrayList<>();

    public StopwatchNotificationService() {
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

            initializeValues();
            startStopwatchService();

        }
        return super.onStartCommand(intent, flags, startId);
    }

    private void initializeValues(){
        SharedPreferences sharedPreferences = getSharedPreferences("TickTrackData",MODE_PRIVATE);
        stopwatchData = retrieveStopwatchData(sharedPreferences);
        stopForeground(true);
    }
    public static ArrayList<StopwatchData> retrieveStopwatchData(SharedPreferences sharedPreferences){
        Gson gson = new Gson();
        String json = sharedPreferences.getString("StopwatchData", null);
        Type type = new TypeToken<ArrayList<StopwatchData>>() {}.getType();
        ArrayList<StopwatchData> stopwatchData = gson.fromJson(json, type);
        if(stopwatchData == null){
            stopwatchData = new ArrayList<>();
        }
        return stopwatchData;
    }

    private void startStopwatchService() {
        startForegroundService();
        refreshingEverySecond();
    }

    private void startForegroundService() {
        setupCustomNotification();
        startForeground(3, notificationBuilder.build());
        Toast.makeText(this, "Stopwatch Notification created!", Toast.LENGTH_SHORT).show();
    }

    private void setupCustomNotification(){

        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManagerCompat = NotificationManagerCompat.from(getApplicationContext());

        Intent resultIntent = new Intent(this, SoYouADeveloperHuh.class);
        resultIntent.putExtra("FragmentID", 3);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addNextIntentWithParentStack(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(3, PendingIntent.FLAG_UPDATE_CURRENT);

        notificationBuilder = new NotificationCompat.Builder(this, TickTrack.STOPWATCH_NOTIFICATION)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setStyle(new NotificationCompat.DecoratedCustomViewStyle())
                .setDefaults(Notification.DEFAULT_ALL)
                .setPriority(NotificationCompat.PRIORITY_MIN)
                .setOnlyAlertOnce(true)
                .setContentIntent(resultPendingIntent);


        if (android.os.Build.VERSION. SDK_INT >= android.os.Build.VERSION_CODES. O ) {
            notificationBuilder.setChannelId(TickTrack.STOPWATCH_NOTIFICATION);
        }

    }

    public void notifyNotification(){
        stopwatchData = retrieveStopwatchData(getSharedPreferences("TickTrackData",MODE_PRIVATE));
        if(stopwatchData.get(0).isRunning()){
            notificationBuilder.setContentTitle("TickTrack stopwatch");
            String nextOccurrence = getNextOccurrence();
            notificationBuilder.setContentText(nextOccurrence);
            notificationManagerCompat.notify(3, notificationBuilder.build());
        }
    }

    private String getNextOccurrence() {
        long differenceFromNow = stopwatchData.get(0).getStopwatchTimerStartTimeInMillis() - System.currentTimeMillis();
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
        }
    };

    private void refreshingEverySecond(){
        handler.postDelayed(refreshRunnable, 1000);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(refreshRunnable);
        stopSelf();
    }

}
