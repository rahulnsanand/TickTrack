package com.theflopguyproductions.ticktrack.utils.database;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.SystemClock;

import com.theflopguyproductions.ticktrack.timer.receivers.TimerBroadcastReceiver;
import com.theflopguyproductions.ticktrack.timer.service.TimerService;

public class TickTrackTimerDatabase {

    private Context context;

    public TickTrackTimerDatabase(Context context) {
        this.context = context;
    }

    public void setAlarm(long endTime, int timerIntegerID){

        long shortTime = endTime - SystemClock.elapsedRealtime();
        if(!(shortTime > 5000)){
            new Handler().postDelayed(() -> {
                Intent intent = new Intent(context, TimerBroadcastReceiver.class);
                intent.setAction(TimerBroadcastReceiver.ACTION_TIMER_BROADCAST);
                intent.putExtra("timerIntegerID", timerIntegerID);
                this.context.sendBroadcast(intent);
            }, shortTime);

        } else {
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            Intent intent = new Intent(context, TimerBroadcastReceiver.class);
            intent.setAction(TimerBroadcastReceiver.ACTION_TIMER_BROADCAST);
            intent.putExtra("timerIntegerID", timerIntegerID);
            PendingIntent alarmPendingIntent = PendingIntent.getBroadcast(context, timerIntegerID, intent, 0);
            alarmManager.setExact(
                    AlarmManager.ELAPSED_REALTIME_WAKEUP,
                    endTime,
                    alarmPendingIntent
            );
        }
    }

    public void cancelAlarm(int timerIntegerID){
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, TimerBroadcastReceiver.class);
        intent.setAction(TimerBroadcastReceiver.ACTION_TIMER_BROADCAST);
        PendingIntent alarmPendingIntent = PendingIntent.getBroadcast(context, timerIntegerID, intent, 0);
        alarmManager.cancel(alarmPendingIntent);
    }

    public void startNotificationService() {
        Intent intent = new Intent(context, TimerService.class);
        intent.setAction(TimerService.ACTION_START_TIMER_SERVICE);
        context.startService(intent);
    }

    public void stopNotificationService() {
        Intent intent = new Intent(context, TimerService.class);
        intent.setAction(TimerService.ACTION_STOP_TIMER_SERVICE);
        context.startService(intent);
    }

    public boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

}
