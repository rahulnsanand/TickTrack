package com.theflopguyproductions.ticktrack.utils;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.theflopguyproductions.ticktrack.timer.TimerData;
import com.theflopguyproductions.ticktrack.timer.service.TimerBroadcastReceiver;
import com.theflopguyproductions.ticktrack.timer.service.TimerService;
import com.theflopguyproductions.ticktrack.timer.service.TimerServiceData;

import java.lang.reflect.Type;
import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

public class TickTrackTimerDatabase {

    private Context context;
    private SharedPreferences sharedPreferences;


    public TickTrackTimerDatabase(Context context) {
        this.context = context;
        this.sharedPreferences = context.getSharedPreferences("TickTrackData", MODE_PRIVATE);
    }


    public int currentServiceDataPosition(int timerIntegerID, ArrayList<TimerServiceData> timerServiceDataArrayList){
        for(int i = 0; i < timerServiceDataArrayList.size(); i++){
            if(timerServiceDataArrayList.get(i).getTimerIDInteger() == timerIntegerID){
                return i;
            }
        }
        return -1;
    }


    public ArrayList<TimerServiceData> retrieveTimerServiceData(){
        Gson gson = new Gson();
        String json = sharedPreferences.getString("TimerServiceData", null);
        Type type = new TypeToken<ArrayList<TimerServiceData>>() {}.getType();
        ArrayList<TimerServiceData> timerServiceData = gson.fromJson(json, type);

        if(timerServiceData == null){
            timerServiceData = new ArrayList<>();
        }

        return timerServiceData;
    }

    public void storeTimerServiceData(ArrayList<TimerServiceData> timerServiceDataArrayList){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(timerServiceDataArrayList);
        editor.putString("TimerServiceData", json);
        editor.apply();
    }

    public void setAlarm(long currentTimeInMillis, int timerIntegerID){
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, TimerBroadcastReceiver.class);
        intent.setAction(TimerBroadcastReceiver.ACTION_TIMER_BROADCAST);
        intent.putExtra("timerIntegerID", timerIntegerID);
        PendingIntent alarmPendingIntent = PendingIntent.getBroadcast(context, timerIntegerID, intent, 0);
        alarmManager.setExact(
                AlarmManager.RTC_WAKEUP,
                currentTimeInMillis+System.currentTimeMillis(),
                alarmPendingIntent
        );
    }

    public void cancelAlarm(int timerIntegerID){
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, TimerBroadcastReceiver.class);
        intent.setAction(TimerBroadcastReceiver.ACTION_TIMER_BROADCAST);
        PendingIntent alarmPendingIntent = PendingIntent.getBroadcast(context, timerIntegerID, intent, 0);
        alarmManager.cancel(alarmPendingIntent);
        //removeServiceData(timerIntegerID, timerServiceDataArrayList);
    }

    private void addServiceData(long currentTimeInMillis, int timerIntegerID, String timerStringID, ArrayList<TimerServiceData> timerServiceDataArrayList) {

        TimerServiceData timerServiceData = new TimerServiceData();
        timerServiceData.setEndTimeInMillis(currentTimeInMillis+System.currentTimeMillis());
        timerServiceData.setTimerIDInteger(timerIntegerID);
        timerServiceData.setTimerIDString(timerStringID);
        timerServiceDataArrayList.add(timerServiceData);
        storeTimerServiceData(timerServiceDataArrayList);

    }
    private void removeServiceData(int timerIntegerID, ArrayList<TimerServiceData> timerServiceDataArrayList) {
        for(int i = 0; i < timerServiceDataArrayList.size(); i++){
            if(timerServiceDataArrayList.get(i).getTimerIDInteger()==timerIntegerID){
                timerServiceDataArrayList.remove(i);
                storeTimerServiceData(timerServiceDataArrayList);
                return;
            }
        }
    }

    private void startNotificationService() {
        Intent intent = new Intent(context, TimerService.class);
        intent.setAction(TimerService.ACTION_START_TIMER_SERVICE);
        context.startService(intent);
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

}
