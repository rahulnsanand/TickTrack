package com.theflopguyproductions.ticktrack.stopwatch.service;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.SystemClock;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.theflopguyproductions.ticktrack.stopwatch.StopwatchData;
import com.theflopguyproductions.ticktrack.stopwatch.StopwatchLapData;
import com.theflopguyproductions.ticktrack.utils.database.TickTrackDatabase;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;

public class TickTrackNotificationStopwatch {

    private Context context;
    private ArrayList<StopwatchData> stopwatchDataArrayList;
    private ArrayList<StopwatchLapData> stopwatchLapData;

    private Handler notificationStopwatchHandler = new Handler();
    private final Runnable stopwatchNotificationRunnable = this::stopwatchNotificationRunnable;
    private long stopwatchRetrievedStartTime, stopwatchDurationElapsed, differenceValue = 0;
    private TickTrackDatabase tickTrackDatabase;

    private NotificationManagerCompat notificationManagerCompat;
    private NotificationCompat.Builder notificationBuilder;

    public TickTrackNotificationStopwatch(Context context) {
        this.context = context;
        tickTrackDatabase = new TickTrackDatabase(context);
        stopwatchDataArrayList = tickTrackDatabase.retrieveStopwatchData();
        stopwatchLapData = tickTrackDatabase.retrieveStopwatchLapData();
    }

    public void setupNotificationStuff(NotificationManagerCompat notificationManagerCompat, NotificationCompat.Builder notificationBuilder) {
        this.notificationBuilder = notificationBuilder;
        this.notificationManagerCompat = notificationManagerCompat;
    }

    private void refreshData() {
        System.out.println("DATA REFRESH");
        tickTrackDatabase.storeStopwatchData(stopwatchDataArrayList);
        tickTrackDatabase.storeLapData(stopwatchLapData);
        stopwatchDataArrayList = tickTrackDatabase.retrieveStopwatchData();
        stopwatchLapData = tickTrackDatabase.retrieveStopwatchLapData();
    }

    static String getNotificationFormattedHourMinute(long elapsedTime) {
        final StringBuilder displayTime = new StringBuilder();
        int seconds = (int) ((elapsedTime / 1000) % 60);
        int minutes = (int) (elapsedTime / (60 * 1000) % 60);
        int hours = (int) (elapsedTime / (60 * 60 * 1000));
        NumberFormat f = new DecimalFormat("00");
        if (minutes == 0) {
            displayTime.append("00:").append(f.format(seconds));
        } else if (hours == 0) {
            displayTime.append("00:").append(f.format(minutes)).append(":").append(f.format(seconds));
        } else {
            displayTime.append(hours).append(":").append(f.format(minutes)).append(":").append(f.format(seconds));
        }
        return displayTime.toString();
    }

    private long getDifference() {
        if (stopwatchDataArrayList.get(0).getStopwatchTimerStartTimeInRealTimeMillis() < SystemClock.elapsedRealtime()) {
            return SystemClock.elapsedRealtime() - stopwatchDataArrayList.get(0).getStopwatchTimerStartTimeInRealTimeMillis();
        }
        return 0;
    }

    private void stopwatchNotificationRunnable() {
        if (!stopwatchDataArrayList.get(0).isRunning() || stopwatchDataArrayList.get(0).isPause()) {
            notificationStopwatchHandler.removeCallbacks(stopwatchNotificationRunnable);
            return;
        }
        long elapsedRealTime = SystemClock.elapsedRealtime() + differenceValue;
        stopwatchDurationElapsed = elapsedRealTime - stopwatchRetrievedStartTime;
        updateNotification();
        notificationStopwatchHandler.postDelayed(stopwatchNotificationRunnable, 250);
    }

    private void updateNotification() {
        if (stopwatchLapData.size() > 0) {
            notificationBuilder.setContentText("Lap " + (stopwatchLapData.size() + 1));
        }
        notificationBuilder.setContentTitle(getNotificationFormattedHourMinute(stopwatchDurationElapsed));
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        notificationManagerCompat.notify(4, notificationBuilder.build());
    }

    public void stop(){
        if(!stopwatchDataArrayList.get(0).isRunning()){
            throw new IllegalStateException("Not Started");
        } else {
            notificationStopwatchHandler.removeCallbacks(stopwatchNotificationRunnable);

            stopwatchDataArrayList.get(0).setRunning(false);
            stopwatchDataArrayList.get(0).setPause(false);
            stopwatchDataArrayList.get(0).setStopwatchTimerStartTimeInMillis(-1);
            stopwatchDataArrayList.get(0).setStopwatchTimerStartTimeInRealTimeMillis(-1);
            stopwatchDataArrayList.get(0).setLastPauseTimeRealTimeInMillis(-1);
            stopwatchDataArrayList.get(0).setLastPauseTimeInMillis(-1);
            stopwatchDataArrayList.get(0).setLastLapEndTimeInMillis(0);
            stopwatchDataArrayList.get(0).setNotification(false);
            stopwatchLapData.clear();
            tickTrackDatabase.storeLapNumber(0);
            tickTrackDatabase.storeLapData(stopwatchLapData);
            refreshData();

        }
    }

    public void resumeInit(){

        differenceValue = getDifference();
        stopwatchRetrievedStartTime = SystemClock.elapsedRealtime();
        notificationStopwatchHandler.post(stopwatchNotificationRunnable);

        stopwatchDataArrayList.get(0).setPause(false);
        stopwatchDataArrayList.get(0).setRunning(true);

        refreshData();

    }

    public void pauseInit() {
        stopwatchDurationElapsed = stopwatchDataArrayList.get(0).getLastPauseValueInMillis();
        updateNotification();
    }

    public void resume() {
        if(!stopwatchDataArrayList.get(0).isPause()){
            throw new IllegalStateException("Not Paused");
        } else if (!stopwatchDataArrayList.get(0).isRunning()){
            throw new IllegalStateException("Not Started");
        } else {
            stopwatchDurationElapsed = stopwatchDataArrayList.get(0).getLastPauseValueInMillis();
            stopwatchRetrievedStartTime = SystemClock.elapsedRealtime();
            differenceValue = stopwatchDurationElapsed;

            notificationStopwatchHandler.post(stopwatchNotificationRunnable);

            stopwatchDataArrayList.get(0).setPause(false);
            stopwatchDataArrayList.get(0).setRunning(true);

            refreshData();
        }
    }

    public void lap(){
        if(!stopwatchDataArrayList.get(0).isRunning()){
            throw new IllegalStateException("Not Started");
        } else {

            long lapTime = stopwatchDurationElapsed - stopwatchDataArrayList.get(0).getLastLapEndTimeInMillis();
            stopwatchDataArrayList.get(0).setLastLapEndTimeInMillis(stopwatchDurationElapsed);

            int currentLapNumber = tickTrackDatabase.retrieveLapNumber();
            if(!(currentLapNumber >= 0)){
                currentLapNumber = 0;
            }

            StopwatchLapData stopwatchLapData = new StopwatchLapData();
            stopwatchLapData.setLapNumber(currentLapNumber+1);
            stopwatchLapData.setElapsedTimeInMillis(stopwatchDurationElapsed);
            stopwatchLapData.setLapTimeInMillis(lapTime);
            this.stopwatchLapData.add(0, stopwatchLapData);
            this.stopwatchLapData.get(0).setLastLapUpdateRealtimeInMillis(SystemClock.elapsedRealtime());
            this.stopwatchLapData.get(0).setLastLapUpdateSystemTimeInMillis(System.currentTimeMillis());
            refreshData();
            tickTrackDatabase.storeLapNumber(currentLapNumber+1);

        }
    }

    public void pause(){
        if(stopwatchDataArrayList.get(0).isPause()){
            throw new IllegalStateException("Already Paused");

        } else if(!stopwatchDataArrayList.get(0).isRunning()){
            throw new IllegalStateException("Not Started");
        } else {

            notificationStopwatchHandler.removeCallbacks(stopwatchNotificationRunnable);

            stopwatchDataArrayList.get(0).setRunning(true);
            stopwatchDataArrayList.get(0).setPause(true);
            stopwatchDataArrayList.get(0).setLastPauseValueInMillis(stopwatchDurationElapsed);
            stopwatchDataArrayList.get(0).setLastPauseTimeRealTimeInMillis(SystemClock.elapsedRealtime());
            stopwatchDataArrayList.get(0).setLastPauseTimeInMillis(System.currentTimeMillis());
            stopwatchDataArrayList.get(0).setProgressSystemValue(System.currentTimeMillis());
            refreshData();

            updateNotification();
        }
    }

    public void killStopwatch(){
        stopwatchDataArrayList.get(0).setProgressSystemValue(System.currentTimeMillis());
        refreshData();
        notificationStopwatchHandler.removeCallbacks(stopwatchNotificationRunnable);
    }

}
