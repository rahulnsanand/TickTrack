package com.theflopguyproductions.ticktrack.utils;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.theflopguyproductions.ticktrack.timer.TimerData;
import com.theflopguyproductions.ticktrack.timer.service.TimerBroadcastReceiver;
import com.theflopguyproductions.ticktrack.timer.service.TimerService;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

public class TickTrackTimerDatabase {

    private Context context;
    private SharedPreferences sharedPreferences;
    private TickTrackDatabase tickTrackDatabase;
    private ArrayList<TimerData> timerDataArrayList;

    public TickTrackTimerDatabase(Context context) {
        this.context = context;
        this.sharedPreferences = context.getSharedPreferences("TickTrackData", MODE_PRIVATE);
    }

    public void setAlarm(long endTime, int timerIntegerID){

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, TimerBroadcastReceiver.class);
        intent.setAction(TimerBroadcastReceiver.ACTION_TIMER_BROADCAST);
        intent.putExtra("timerIntegerID", timerIntegerID);
        PendingIntent alarmPendingIntent = PendingIntent.getBroadcast(context, timerIntegerID, intent, 0);
        alarmManager.setExact(
                AlarmManager.RTC_WAKEUP,
                endTime,
                alarmPendingIntent
        );
        System.out.println("TimerCreateAlarm");
    }

    public void cancelAlarm(int timerIntegerID){
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, TimerBroadcastReceiver.class);
        intent.setAction(TimerBroadcastReceiver.ACTION_TIMER_BROADCAST);
        PendingIntent alarmPendingIntent = PendingIntent.getBroadcast(context, timerIntegerID, intent, 0);
        alarmManager.cancel(alarmPendingIntent);
        System.out.println("TimerCancelledAlarm");
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
