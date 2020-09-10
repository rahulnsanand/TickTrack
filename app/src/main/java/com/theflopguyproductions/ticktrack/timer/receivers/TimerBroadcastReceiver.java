package com.theflopguyproductions.ticktrack.timer.receivers;

import android.app.ActivityManager;
import android.app.KeyguardManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.SystemClock;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.theflopguyproductions.ticktrack.timer.data.TimerData;
import com.theflopguyproductions.ticktrack.timer.quick.QuickTimerData;
import com.theflopguyproductions.ticktrack.timer.ringer.TimerRingerActivity;
import com.theflopguyproductions.ticktrack.timer.service.TimerRingService;
import com.theflopguyproductions.ticktrack.timer.service.TimerService;
import com.theflopguyproductions.ticktrack.utils.database.TickTrackDatabase;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Objects;

public class TimerBroadcastReceiver extends BroadcastReceiver {

    public static final String ACTION_TIMER_BROADCAST = "ACTION_TIMER_BROADCAST";
    public static final String ACTION_QUICK_TIMER_BROADCAST = "ACTION_QUICK_TIMER_BROADCAST";

    private ArrayList<TimerData> timerDataArrayList = new ArrayList<>();
    private ArrayList<QuickTimerData> quickTimerData = new ArrayList<>();
    private int timerIDInteger, quickTimerIDInteger;

    @Override
    public void onReceive(Context context, Intent intent) {
        String toastText = "TickTrack Timer";
        Toast.makeText(context, toastText, Toast.LENGTH_SHORT).show();
        if(Objects.equals(intent.getAction(), ACTION_TIMER_BROADCAST)){

            Toast.makeText(context, toastText, Toast.LENGTH_SHORT).show();

            TickTrackDatabase tickTrackDatabase = new TickTrackDatabase(context);

            timerIDInteger = intent.getIntExtra("timerIntegerID",0);
            timerDataArrayList = retrieveTimerData(tickTrackDatabase.getSharedPref(context));

            int position = getCurrentTimerPosition();
            if(position!=-1){
                timerDataArrayList.get(position).setTimerRinging(true);
                timerDataArrayList.get(position).setTimerNotificationOn(false);
                timerDataArrayList.get(position).setTimerEndedTimeInMillis(SystemClock.elapsedRealtime());
                timerDataArrayList.get(position).setTimerStartTimeInMillis(-1);
                timerDataArrayList.get(position).setTimerEndTimeInMillis(System.currentTimeMillis());

                storeTimerList(tickTrackDatabase.getSharedPref(context));
                if(!isMyServiceRunning(TimerRingService.class, context)){
                    startNotificationService(context);
                    KeyguardManager myKM = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
                    if( myKM.inKeyguardRestrictedInputMode()) {
                        Intent resultIntent = new Intent(context, TimerRingerActivity.class);
                        resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(resultIntent);
                    }
                }
                if(isMyServiceRunning(TimerService.class, context)){
                    stopTimerNotification(context);
                }
            }
        }
        else if (Objects.equals(intent.getAction(), ACTION_QUICK_TIMER_BROADCAST)){
            Toast.makeText(context, toastText, Toast.LENGTH_SHORT).show();

            TickTrackDatabase tickTrackDatabase = new TickTrackDatabase(context);

            quickTimerIDInteger = intent.getIntExtra("timerIntegerID",0);
            quickTimerData = retrieveQuickTimerData(tickTrackDatabase.getSharedPref(context));

            int position = getCurrentQuickTimerPosition();
            if(position!=-1){
                quickTimerData.get(position).setTimerRinging(true);
                quickTimerData.get(position).setTimerNotificationOn(false);
                quickTimerData.get(position).setTimerEndedTimeInMillis(SystemClock.elapsedRealtime());
                quickTimerData.get(position).setTimerStartTimeInMillis(-1);
                quickTimerData.get(position).setTimerEndTimeInMillis(System.currentTimeMillis());

                storeQuickTimerList(tickTrackDatabase.getSharedPref(context));
                if(!isMyServiceRunning(TimerRingService.class, context)){
                    startNotificationService(context);
                    KeyguardManager myKM = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
                    if( myKM.inKeyguardRestrictedInputMode()) {
                        Intent resultIntent = new Intent(context, TimerRingerActivity.class);
                        resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(resultIntent);
                    }
                }
                if(isMyServiceRunning(TimerService.class, context)){
                    stopTimerNotification(context);
                }
            }
        }
    }

    private void stopTimerNotification(Context context) {
        Intent intent = new Intent(context, TimerService.class);
        intent.setAction(TimerService.ACTION_STOP_TIMER_SERVICE);
        context.startService(intent);
    }

    private int getCurrentTimerPosition(){
        for(int i = 0; i < timerDataArrayList.size(); i ++){
            if(timerDataArrayList.get(i).getTimerIntID()==timerIDInteger){
                return i;
            }
        }
        return -1;
    }

    private int getCurrentQuickTimerPosition(){
        for(int i = 0; i < quickTimerData.size(); i ++){
            if(quickTimerData.get(i).getTimerIntID()==quickTimerIDInteger){
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

    private ArrayList<QuickTimerData> retrieveQuickTimerData(SharedPreferences sharedPreferences){

        Gson gson = new Gson();
        String json = sharedPreferences.getString("QuickTimerData", null);
        Type type = new TypeToken<ArrayList<QuickTimerData>>() {}.getType();
        ArrayList<QuickTimerData> quickTimerData = gson.fromJson(json, type);

        if(quickTimerData == null){
            quickTimerData = new ArrayList<>();
        }

        return quickTimerData;
    }
    public void storeQuickTimerList(SharedPreferences sharedPreferences){

        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(quickTimerData);
        editor.putString("QuickTimerData", json);
        editor.apply();

    }
}
