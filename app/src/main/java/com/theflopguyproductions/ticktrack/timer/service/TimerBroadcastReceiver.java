package com.theflopguyproductions.ticktrack.timer.service;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Objects;

import static android.content.Context.MODE_PRIVATE;

public class TimerBroadcastReceiver extends BroadcastReceiver {

    public static final String ACTION_TIMER_BROADCAST = "ACTION_TIMER_BROADCAST";

    private ArrayList<TimerServiceData> timerServiceDataArrayList = new ArrayList<>();

    private int timerIDInteger;
    private String timerStringID;

    @Override
    public void onReceive(Context context, Intent intent) {

        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {

            String toastText = "Tick Track Reboot";
            Toast.makeText(context, toastText, Toast.LENGTH_SHORT).show();

        } else if(Objects.equals(intent.getAction(), ACTION_TIMER_BROADCAST)){

            String toastText = "Timer Received";
            Toast.makeText(context, toastText, Toast.LENGTH_SHORT).show();

            timerIDInteger = intent.getIntExtra("timerIntegerID",0);
            timerStringID = intent.getStringExtra("timerStringID");
            timerServiceDataArrayList = retrieveTimerServiceDataList(context.getSharedPreferences("TickTrackData",MODE_PRIVATE));
            removeServiceData(context.getSharedPreferences("TickTrackData",MODE_PRIVATE));
            if(isMyServiceRunning(TimerService.class, context)){
                stopNotificationService(context);
            }
        }

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
    private void removeServiceData(SharedPreferences sharedPreferences) {
        for(int i = 0; i < timerServiceDataArrayList.size(); i++){
            if(timerServiceDataArrayList.get(i).getTimerIDInteger()==timerIDInteger){
                timerServiceDataArrayList.remove(i);
                storeTimerServiceData(sharedPreferences);
                return;
            }
        }
    }
    private void stopNotificationService(Context context) {
        Intent intent = new Intent(context, TimerService.class);
        intent.setAction(TimerService.ACTION_STOP_TIMER_SERVICE);
        context.startService(intent);
    }
    private ArrayList<TimerServiceData> retrieveTimerServiceDataList(SharedPreferences sharedPreferences){

        Gson gson = new Gson();
        String json = sharedPreferences.getString("TimerServiceData", null);
        Type type = new TypeToken<ArrayList<TimerServiceData>>() {}.getType();
        ArrayList<TimerServiceData> timerServiceData = gson.fromJson(json, type);

        if(timerServiceData == null){
            timerServiceData = new ArrayList<>();
        }

        return timerServiceData;
    }
    private void storeTimerServiceData(SharedPreferences sharedPreferences){

        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(timerServiceDataArrayList);
        editor.putString("TimerServiceData", json);
        editor.apply();

    }
}
