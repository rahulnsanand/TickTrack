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
import com.theflopguyproductions.ticktrack.timer.TimerData;
import com.theflopguyproductions.ticktrack.timer.ringer.TimerRingerActivity;
import com.theflopguyproductions.ticktrack.timer.service.TimerRingService;
import com.theflopguyproductions.ticktrack.timer.service.TimerService;

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
        String toastText = "TickTrack Timer";
        Toast.makeText(context, toastText, Toast.LENGTH_SHORT).show();
        if(Objects.equals(intent.getAction(), ACTION_TIMER_BROADCAST)){

            Toast.makeText(context, toastText, Toast.LENGTH_SHORT).show();

            timerIDInteger = intent.getIntExtra("timerIntegerID",0);
            timerDataArrayList = retrieveTimerData(context.getSharedPreferences("TickTrackData",MODE_PRIVATE));

            int position = getCurrentTimerPosition();
            if(position!=-1){
                timerDataArrayList.get(position).setTimerRinging(true);
                timerDataArrayList.get(position).setTimerNotificationOn(false);
                timerDataArrayList.get(position).setTimerEndedTimeInMillis(SystemClock.elapsedRealtime());
                storeTimerList(context.getSharedPreferences("TickTrackData",MODE_PRIVATE));
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
            if(timerDataArrayList.get(i).getTimerID()==timerIDInteger){
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
}
