package com.theflopguyproductions.ticktrack.utils.helpers;

import android.app.ActivityManager;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;

import com.theflopguyproductions.ticktrack.timer.data.TimerData;
import com.theflopguyproductions.ticktrack.timer.quick.QuickTimerData;
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

                        tickTrackTimerDatabase.setAlarm(nextAlarmStamp, timerData.get(i).getTimerIntID(), false);

                        tickTrackDatabase.storeTimerList(timerData);

                        if(!isMyServiceRunning(TimerService.class, activity)){
                            tickTrackTimerDatabase.startNotificationService();
                        }

                    } else { //TODO TIMER ALREADY ELAPSED
                        long endedAgoTime = System.currentTimeMillis() - timerData.get(i).getTimerEndTimeInMillis();
                        timerData.get(i).setTimerNotificationOn(false);
                        timerData.get(i).setTimerRinging(true);
                        timerData.get(i).setTimerEndedTimeInMillis(SystemClock.elapsedRealtime()-endedAgoTime);
                        timerData.get(i).setTimerStartTimeInMillis(-1);
                        timerData.get(i).setTimerEndTimeInMillis(System.currentTimeMillis()-endedAgoTime);
                        tickTrackDatabase.storeTimerList(timerData);
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

        }

        ArrayList<QuickTimerData> quickTimerData = tickTrackDatabase.retrieveQuickTimerList();

        for(int i = 0; i<quickTimerData.size(); i++){
            if(quickTimerData.get(i).isTimerOn() && !quickTimerData.get(i).isTimerPause() && !quickTimerData.get(i).isTimerRinging()){ //TODO TIMER IS RUNNING, NOT RINGING
                //TODO CAN BE ELAPSED AND YET TO ELAPSE HERE
                if(quickTimerData.get(i).getTimerStartTimeInMillis() != -1) {

                    long elapsedTime = System.currentTimeMillis() - quickTimerData.get(i).getTimerStartTimeInMillis();

                    if(elapsedTime<quickTimerData.get(i).getTimerTotalTimeInMillis()){ //TODO TIMER YET TO ELAPSE

                        long nextAlarmStamp = SystemClock.elapsedRealtime()+(quickTimerData.get(i).getTimerTotalTimeInMillis()-elapsedTime);

                        quickTimerData.get(i).setTimerAlarmEndTimeInMillis(nextAlarmStamp);

                        tickTrackTimerDatabase.setAlarm(nextAlarmStamp, quickTimerData.get(i).getTimerIntID(), true);

                        tickTrackDatabase.storeQuickTimerList(quickTimerData);

                        if(!isMyServiceRunning(TimerService.class, activity)){
                            tickTrackTimerDatabase.startNotificationService();
                        }

                    } else { //TODO TIMER ALREADY ELAPSED

                        long endedAgoTime = System.currentTimeMillis() - quickTimerData.get(i).getTimerEndTimeInMillis();
                        quickTimerData.get(i).setTimerNotificationOn(false);
                        quickTimerData.get(i).setTimerRinging(true);
                        quickTimerData.get(i).setTimerEndedTimeInMillis(SystemClock.elapsedRealtime()-endedAgoTime);
                        quickTimerData.get(i).setTimerStartTimeInMillis(-1);
                        quickTimerData.get(i).setTimerEndTimeInMillis(System.currentTimeMillis()-endedAgoTime);
                        tickTrackDatabase.storeQuickTimerList(quickTimerData);
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
                }
            } else if(quickTimerData.get(i).isTimerOn() && !quickTimerData.get(i).isTimerPause() && quickTimerData.get(i).isTimerRinging()){ //TODO TIMER IS RINGING
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
