package com.theflopguyproductions.ticktrack.receivers;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;

import com.theflopguyproductions.ticktrack.stopwatch.StopwatchData;
import com.theflopguyproductions.ticktrack.stopwatch.StopwatchLapData;
import com.theflopguyproductions.ticktrack.stopwatch.service.StopwatchNotificationService;
import com.theflopguyproductions.ticktrack.utils.database.TickTrackDatabase;
import com.theflopguyproductions.ticktrack.utils.helpers.FirebaseHelper;
import com.theflopguyproductions.ticktrack.utils.helpers.TimerManagementHelper;

import java.util.ArrayList;
import java.util.Calendar;

public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        TimerManagementHelper timerManagementHelper = new TimerManagementHelper(context);

        timerManagementHelper.reestablishTimers();

        TickTrackDatabase tickTrackDatabase = new TickTrackDatabase(context);
        ArrayList<StopwatchData> stopwatchData = tickTrackDatabase.retrieveStopwatchData();
        ArrayList<StopwatchLapData> stopwatchLapData = tickTrackDatabase.retrieveStopwatchLapData();
        if(stopwatchData.get(0).getStopwatchTimerStartTimeInMillis()!=-1){
            long timeElapsedOnBoot = System.currentTimeMillis() - stopwatchData.get(0).getStopwatchTimerStartTimeInMillis();
            stopwatchData.get(0).setStopwatchTimerStartTimeInRealTimeMillis(SystemClock.elapsedRealtime()-timeElapsedOnBoot);

            if(stopwatchData.get(0).isPause() && stopwatchData.get(0).isRunning()){
                long pauseElapsedOnBoot = System.currentTimeMillis() - stopwatchData.get(0).getLastPauseTimeInMillis();
                stopwatchData.get(0).setLastPauseTimeRealTimeInMillis(SystemClock.elapsedRealtime()-pauseElapsedOnBoot);
            }

            if(stopwatchLapData.size()>0){
                long lapElapsedOnBoot = System.currentTimeMillis() - stopwatchData.get(0).getProgressSystemValue();
                stopwatchData.get(0).setProgressValue(SystemClock.elapsedRealtime()-lapElapsedOnBoot);
                long lapLastUpdateTime = System.currentTimeMillis() - stopwatchLapData.get(0).getLastLapUpdateSystemTimeInMillis();
                stopwatchLapData.get(0).setLastLapUpdateRealtimeInMillis(SystemClock.elapsedRealtime() - lapLastUpdateTime);

            }

            tickTrackDatabase.storeStopwatchData(stopwatchData);
            tickTrackDatabase.storeLapData(stopwatchLapData);

            if(!isMyServiceRunning(StopwatchNotificationService.class, context)){
                startNotificationService(context);
            }
        }

        FirebaseHelper firebaseHelper = new FirebaseHelper(context);
        if(firebaseHelper.isUserSignedIn()){
            scheduleBackupAlarm(context);
        }
    }

    private void scheduleBackupAlarm(Context context) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 2);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, BackupScheduleReceiver.class);
        intent.setAction(BackupScheduleReceiver.START_BACKUP_SCHEDULE);
        intent.addFlags(Intent.FLAG_RECEIVER_FOREGROUND);
        PendingIntent alarmPendingIntent = PendingIntent.getBroadcast(context, 21, intent, 0);
        alarmManager.setInexactRepeating(
                AlarmManager.RTC,
                calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY,
                alarmPendingIntent
        );
    }

    public void startNotificationService(Context context) {
        Intent intent = new Intent(context, StopwatchNotificationService.class);
        intent.setAction(StopwatchNotificationService.ACTION_START_STOPWATCH_SERVICE);
        context.startService(intent);
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

}
