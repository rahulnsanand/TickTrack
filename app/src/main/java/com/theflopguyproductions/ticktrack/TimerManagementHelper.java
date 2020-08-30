package com.theflopguyproductions.ticktrack;

import android.app.ActivityManager;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;

import com.theflopguyproductions.ticktrack.timer.TimerData;
import com.theflopguyproductions.ticktrack.timer.ringer.TimerRingerActivity;
import com.theflopguyproductions.ticktrack.timer.service.TimerRingService;
import com.theflopguyproductions.ticktrack.timer.service.TimerService;
import com.theflopguyproductions.ticktrack.utils.database.TickTrackDatabase;
import com.theflopguyproductions.ticktrack.utils.database.TickTrackTimerDatabase;

import java.util.ArrayList;

public class TimerManagementHelper {

    private TickTrackDatabase tickTrackDatabase;
    private Context activity;

    public TimerManagementHelper(Context activity) {
        this.activity = activity;
    }

//    public int getElapsedTimer(){
//        tickTrackDatabase = new TickTrackDatabase(activity);
//        ArrayList<TimerData> timerData = tickTrackDatabase.retrieveTimerList();
//        int elapsed = 0;
//
//        for(int i = 0; i<timerData.size(); i++){
//            if(timerData.get(i).isTimerOn() && !timerData.get(i).isTimerPause()){
//                if(timerData.get(i).getTimerAlarmEndTimeInMillis() < SystemClock.elapsedRealtime()){
//                    timerData.get(i).setTimerRinging(false);
//                    timerData.get(i).setTimerOn(false);
//                    timerData.get(i).setTimerPause(false);
//                    timerData.get(i).setTimerAlarmEndTimeInMillis(-1);
//                    elapsed++;
//                }
//            }
//        }
//
//        tickTrackDatabase.storeTimerList(timerData);
//        return elapsed;
//    }

    public int getAlmostMissedTimer(){
        tickTrackDatabase = new TickTrackDatabase(activity);
        ArrayList<TimerData> timerData = tickTrackDatabase.retrieveTimerList();
        int elapsed = 0;

        for(int i = 0; i<timerData.size(); i++){
            if(timerData.get(i).isTimerOn() && !timerData.get(i).isTimerPause()){
                if(timerData.get(i).getTimerAlarmEndTimeInMillis() > System.currentTimeMillis()){
                    elapsed++;
                }
            }
        }

        return elapsed;
    }

    public void reestablishTimers(){
        tickTrackDatabase = new TickTrackDatabase(activity);
        ArrayList<TimerData> timerData = tickTrackDatabase.retrieveTimerList();
        TickTrackTimerDatabase tickTrackTimerDatabase = new TickTrackTimerDatabase(activity);

        for(int i = 0; i<timerData.size(); i++){
            if(timerData.get(i).isTimerOn() && !timerData.get(i).isTimerPause() && !timerData.get(i).isTimerRinging()){ //TODO TIMER IS RUNNING, NOT RINGING
                //TODO CAN BE ELAPSED AND YET TO ELAPSE HERE
                if(timerData.get(i).getTimerStartTimeInMillis() != -1) {

                    long elapsedTime = System.currentTimeMillis() - timerData.get(i).getTimerStartTimeInMillis();

                    if(elapsedTime<timerData.get(i).getTimerTotalTimeInMillis()){ //TODO TIMER YET TO ELAPSE

                        long nextAlarmStamp = SystemClock.elapsedRealtime()+(timerData.get(i).getTimerTotalTimeInMillis()-elapsedTime);

                        timerData.get(i).setTimerAlarmEndTimeInMillis(nextAlarmStamp);
                        tickTrackTimerDatabase.setAlarm(nextAlarmStamp, timerData.get(i).getTimerIntID());

                        if(!isMyServiceRunning(TimerService.class, activity)){
                            tickTrackTimerDatabase.startNotificationService();
                        }

                    } else { //TODO TIMER ALREADY ELAPSED

                        timerData.get(i).setTimerOn(false);
                        timerData.get(i).setTimerPause(false);
                        timerData.get(i).setTimerNotificationOn(false);
                        timerData.get(i).setTimerRinging(false);
                    }
                }
            } else if(timerData.get(i).isTimerOn() && !timerData.get(i).isTimerPause() && timerData.get(i).isTimerRinging()){ //TODO TIMER IS RINGING
                if(!isMyServiceRunning(TimerRingService.class, activity)){
                    startTimerRingNotificationService(activity);
                    KeyguardManager myKM = (KeyguardManager) activity.getSystemService(Context.KEYGUARD_SERVICE);
                    if( myKM.inKeyguardRestrictedInputMode()) {
                        Intent resultIntent = new Intent(activity, TimerRingerActivity.class);
                        resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        activity.startActivity(resultIntent);
                    }
                }
                if(isMyServiceRunning(TimerService.class, activity)){
                    tickTrackTimerDatabase.stopNotificationService();
                }

            }
            tickTrackDatabase.storeTimerList(timerData);
        }

    }


    private void startTimerRingNotificationService(Context context) {
        Intent intent = new Intent(context, TimerRingService.class);
        intent.setAction(TimerRingService.ACTION_ADD_TIMER_FINISH);
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
