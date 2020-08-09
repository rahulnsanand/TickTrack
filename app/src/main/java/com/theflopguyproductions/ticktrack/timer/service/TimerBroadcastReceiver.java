package com.theflopguyproductions.ticktrack.timer.service;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.theflopguyproductions.ticktrack.R;
import com.theflopguyproductions.ticktrack.application.TickTrack;
import com.theflopguyproductions.ticktrack.counter.notification.CounterNotificationService;
import com.theflopguyproductions.ticktrack.timer.TimerData;
import com.theflopguyproductions.ticktrack.timer.ringer.TimerRingerActivity;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Objects;

import static android.content.Context.MODE_PRIVATE;

public class TimerBroadcastReceiver extends BroadcastReceiver {

    public static final String ACTION_TIMER_BROADCAST = "ACTION_TIMER_BROADCAST";

    private ArrayList<TimerData> timerDataArrayList = new ArrayList<>();

    private int timerIDInteger;

    @Override
    public void onReceive(Context context, Intent intent) {

        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {

            String toastText = "TickTrack Reboot";
            Toast.makeText(context, toastText, Toast.LENGTH_SHORT).show();

        } else if(Objects.equals(intent.getAction(), ACTION_TIMER_BROADCAST)){

            String toastText = "Timer Received";
            Toast.makeText(context, toastText, Toast.LENGTH_SHORT).show();

            timerIDInteger = intent.getIntExtra("timerIntegerID",0);
            timerDataArrayList = retrieveTimerData(context.getSharedPreferences("TickTrackData",MODE_PRIVATE));

            int position = getCurrentTimerPosition(timerIDInteger);
            if(position!=-1){
                timerDataArrayList.get(position).setTimerRinging(true);
                storeTimerList(context.getSharedPreferences("TickTrackData",MODE_PRIVATE));
            }

            if(!isMyServiceRunning(TimerRingService.class, context)){
                startNotificationService(context);
            }
        }
    }

    private int getCurrentTimerPosition(int timerIntegerID){
        for(int i = 0; i < timerDataArrayList.size(); i ++){
            if(timerDataArrayList.get(i).getTimerIntegerID()==timerIntegerID){
                return i;
            }
        }
        return -1;
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

    private void startNotificationService(Context context) {
        Intent intent = new Intent(context, TimerRingService.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.setAction(TimerRingService.ACTION_ADD_TIMER_FINISH);
        context.startService(intent);
    }

    private ArrayList<TimerData> retrieveTimerData(SharedPreferences sharedPreferences){

        Gson gson = new Gson();
        String json = sharedPreferences.getString("TimerData", null);
        Type type = new TypeToken<ArrayList<TimerData>>() {}.getType();
        ArrayList<TimerData> timerServiceData = gson.fromJson(json, type);

        if(timerServiceData == null){
            timerServiceData = new ArrayList<>();
        }

        return timerServiceData;
    }
    public void storeTimerList(SharedPreferences sharedPreferences){

        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(timerDataArrayList);
        editor.putString("TimerData", json);
        editor.apply();

    }
}
