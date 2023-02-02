package com.theflopguyproductions.ticktrack.utils.database;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.SystemClock;

import com.theflopguyproductions.ticktrack.timer.receivers.TimerBroadcastReceiver;
import com.theflopguyproductions.ticktrack.timer.service.TimerRingService;
import com.theflopguyproductions.ticktrack.timer.service.TimerService;

public class TickTrackTimerDatabase {

    private Context context;

    public TickTrackTimerDatabase(Context context) {
        this.context = context;
    }

    public void setAlarm(long endTime, int timerIntegerID, boolean isQuick){

        long shortTime = endTime - SystemClock.elapsedRealtime();

        if(isQuick){
            if(!(shortTime > 5000)){
                new Handler().postDelayed(() -> {
                    Intent intent = new Intent(context, TimerBroadcastReceiver.class);
                    intent.setAction(TimerBroadcastReceiver.ACTION_QUICK_TIMER_BROADCAST);
                    intent.addFlags(Intent.FLAG_RECEIVER_FOREGROUND);
                    intent.putExtra("timerIntegerID", timerIntegerID);
                    this.context.sendBroadcast(intent);
                }, shortTime);

            } else {
                AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                Intent intent = new Intent(context, TimerBroadcastReceiver.class);
                intent.setAction(TimerBroadcastReceiver.ACTION_QUICK_TIMER_BROADCAST);
                intent.addFlags(Intent.FLAG_RECEIVER_FOREGROUND);
                intent.putExtra("timerIntegerID", timerIntegerID);
                PendingIntent alarmPendingIntent = null;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
                    alarmPendingIntent = PendingIntent.getBroadcast(context, timerIntegerID, intent, PendingIntent.FLAG_MUTABLE);
                } else {
                    alarmPendingIntent = PendingIntent.getBroadcast(context, timerIntegerID, intent, 0);
                }
                alarmManager.setExact(
                        AlarmManager.ELAPSED_REALTIME_WAKEUP,
                        endTime,
                        alarmPendingIntent
                );
            }
        } else {
            if(!(shortTime > 5000)){
                new Handler().postDelayed(() -> {
                    Intent intent = new Intent(context, TimerBroadcastReceiver.class);
                    intent.setAction(TimerBroadcastReceiver.ACTION_TIMER_BROADCAST);
                    intent.addFlags(Intent.FLAG_RECEIVER_FOREGROUND);
                    intent.putExtra("timerIntegerID", timerIntegerID);
                    this.context.sendBroadcast(intent);
                }, shortTime);

            } else {
                AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                Intent intent = new Intent(context, TimerBroadcastReceiver.class);
                intent.setAction(TimerBroadcastReceiver.ACTION_TIMER_BROADCAST);
                intent.addFlags(Intent.FLAG_RECEIVER_FOREGROUND);
                intent.putExtra("timerIntegerID", timerIntegerID);
                PendingIntent alarmPendingIntent = null;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
                    alarmPendingIntent = PendingIntent.getBroadcast(context, timerIntegerID, intent, PendingIntent.FLAG_MUTABLE);
                } else {
                    alarmPendingIntent = PendingIntent.getBroadcast(context, timerIntegerID, intent, 0);
                }
                alarmManager.setExact(
                        AlarmManager.ELAPSED_REALTIME_WAKEUP,
                        endTime,
                        alarmPendingIntent
                );
            }
        }
    }

    public void cancelAlarm(int timerIntegerID, boolean isQuick){
        if(isQuick){
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            Intent intent = new Intent(context, TimerBroadcastReceiver.class);
            intent.addFlags(Intent.FLAG_RECEIVER_FOREGROUND);
            intent.setAction(TimerBroadcastReceiver.ACTION_QUICK_TIMER_BROADCAST);
            PendingIntent alarmPendingIntent = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
                alarmPendingIntent = PendingIntent.getBroadcast(context, timerIntegerID, intent, PendingIntent.FLAG_MUTABLE);
            } else {
                alarmPendingIntent = PendingIntent.getBroadcast(context, timerIntegerID, intent, 0);
            }
            alarmManager.cancel(alarmPendingIntent);
        } else {
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            Intent intent = new Intent(context, TimerBroadcastReceiver.class);
            intent.addFlags(Intent.FLAG_RECEIVER_FOREGROUND);
            intent.setAction(TimerBroadcastReceiver.ACTION_TIMER_BROADCAST);
            PendingIntent alarmPendingIntent = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
                alarmPendingIntent = PendingIntent.getBroadcast(context, timerIntegerID, intent, PendingIntent.FLAG_MUTABLE);
            } else {
                alarmPendingIntent = PendingIntent.getBroadcast(context, timerIntegerID, intent, 0);
            }
            alarmManager.cancel(alarmPendingIntent);
        }
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

    public void stopRingService() {
        Intent intent = new Intent(context, TimerRingService.class);
        intent.setAction(TimerRingService.ACTION_STOP_SERVICE_CHECK);
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
