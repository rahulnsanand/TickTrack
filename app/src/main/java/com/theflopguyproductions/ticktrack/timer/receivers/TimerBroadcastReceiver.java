package com.theflopguyproductions.ticktrack.timer.receivers;

import android.app.ActivityManager;
import android.app.KeyguardManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.widget.Toast;

import com.theflopguyproductions.ticktrack.timer.data.TimerData;
import com.theflopguyproductions.ticktrack.timer.quick.QuickTimerData;
import com.theflopguyproductions.ticktrack.timer.ringer.TimerRingerActivity;
import com.theflopguyproductions.ticktrack.timer.service.TimerRingService;
import com.theflopguyproductions.ticktrack.timer.service.TimerService;
import com.theflopguyproductions.ticktrack.utils.database.TickTrackDatabase;

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

            timerIDInteger = intent.getIntExtra("timerIntegerID",-1);
            timerDataArrayList = tickTrackDatabase.retrieveTimerList();

            int position = getCurrentTimerPosition();
            if(position!=-1){
                if(timerDataArrayList.get(position).isTimerOn() && !timerDataArrayList.get(position).isTimerPause() || timerDataArrayList.get(position).isTimerRinging()){
                    System.out.println("RECEIVED TIMER FIRST");
                    if((SystemClock.elapsedRealtime() - timerDataArrayList.get(position).getTimerAlarmEndTimeInMillis() >= 0)){

                        System.out.println("RECEIVED TIMER BROADCAST");
                        timerDataArrayList.get(position).setTimerRinging(true);
                        timerDataArrayList.get(position).setTimerNotificationOn(false);
                        timerDataArrayList.get(position).setTimerEndedTimeInMillis(SystemClock.elapsedRealtime());
                        timerDataArrayList.get(position).setTimerStartTimeInMillis(-1);
                        tickTrackDatabase.storeTimerList(timerDataArrayList);

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
        }
        else if (Objects.equals(intent.getAction(), ACTION_QUICK_TIMER_BROADCAST)){
            Toast.makeText(context, toastText, Toast.LENGTH_SHORT).show();

            TickTrackDatabase tickTrackDatabase = new TickTrackDatabase(context);

            quickTimerIDInteger = intent.getIntExtra("timerIntegerID",0);
            quickTimerData = tickTrackDatabase.retrieveQuickTimerList();

            int position = getCurrentQuickTimerPosition();
            if(position!=-1){
                quickTimerData.get(position).setTimerRinging(true);
                quickTimerData.get(position).setTimerNotificationOn(false);
                quickTimerData.get(position).setTimerEndedTimeInMillis(SystemClock.elapsedRealtime());
                quickTimerData.get(position).setTimerStartTimeInMillis(-1);
//                quickTimerData.get(position).setTimerEndTimeInMillis(System.currentTimeMillis());

                tickTrackDatabase.storeQuickTimerList(quickTimerData);

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

}
