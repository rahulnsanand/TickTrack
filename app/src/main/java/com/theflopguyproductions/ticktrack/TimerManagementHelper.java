package com.theflopguyproductions.ticktrack;

import android.app.ActivityManager;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;

import com.theflopguyproductions.ticktrack.timer.TimerData;
import com.theflopguyproductions.ticktrack.timer.ringer.TimerRingerActivity;
import com.theflopguyproductions.ticktrack.timer.service.TimerRingService;
import com.theflopguyproductions.ticktrack.utils.database.TickTrackDatabase;
import com.theflopguyproductions.ticktrack.utils.database.TickTrackTimerDatabase;

import java.util.ArrayList;

public class TimerManagementHelper {

    private TickTrackDatabase tickTrackDatabase;
    private Context activity;

    public TimerManagementHelper(Context activity) {
        this.activity = activity;
    }

    public int getElapsedTimer(){
        tickTrackDatabase = new TickTrackDatabase(activity);
        ArrayList<TimerData> timerData = tickTrackDatabase.retrieveTimerList();
        int elapsed = 0;

        for(int i = 0; i<timerData.size(); i++){
            if(timerData.get(i).isTimerOn() && !timerData.get(i).isTimerPause()){
                if(timerData.get(i).getTimerAlarmEndTimeInMillis() < SystemClock.elapsedRealtime()){
                    timerData.get(i).setTimerRinging(false);
                    timerData.get(i).setTimerOn(false);
                    timerData.get(i).setTimerPause(false);
                    timerData.get(i).setTimerAlarmEndTimeInMillis(-1);
                    elapsed++;
                }
            }
        }

        tickTrackDatabase.storeTimerList(timerData);
        return elapsed;
    }

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
            if(timerData.get(i).isTimerOn() && !timerData.get(i).isTimerPause()){
                if(timerData.get(i).getTimerRecentUpdatedValue() < (timerData.get(i).getTimerRecentLocalTimeInMillis() - System.currentTimeMillis())){ //TODO TIMER IS ELAPSED
                    tickTrackTimerDatabase.cancelAlarm(timerData.get(i).getTimerID());
                    timerData.get(i).setTimerRinging(true);
                    timerData.get(i).setTimerNotificationOn(false);
                    timerData.get(i).setTimerEndedTimeInMillis(timerData.get(i).getTimerRecentUpdatedValue()-(timerData.get(i).getTimerRecentLocalTimeInMillis()-System.currentTimeMillis()));
                    tickTrackDatabase.storeTimerList(timerData);
                    if(!isMyServiceRunning(TimerRingService.class, activity)){
                        startNotificationService(activity);
                        KeyguardManager myKM = (KeyguardManager) activity.getSystemService(Context.KEYGUARD_SERVICE);
                        if( myKM.inKeyguardRestrictedInputMode()) {
                            Intent resultIntent = new Intent(activity, TimerRingerActivity.class);
                            resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            activity.startActivity(resultIntent);
                        }
                    }
                } else if(timerData.get(i).getTimerRecentUpdatedValue() > (timerData.get(i).getTimerRecentLocalTimeInMillis() - System.currentTimeMillis())){ // TODO TIMER IS YET TO RING

                    timerData.get(i).setTimerAlarmEndTimeInMillis(timerData.get(i).getTimerRecentUpdatedValue()
                            -timerData.get(i).getTimerRecentLocalTimeInMillis()
                            -System.currentTimeMillis()+SystemClock.elapsedRealtime());

                    tickTrackDatabase.storeTimerList(timerData);
                    tickTrackTimerDatabase.setAlarm(timerData.get(i).getTimerAlarmEndTimeInMillis(), timerData.get(i).getTimerID());
                    tickTrackTimerDatabase.startNotificationService();

                }
            }
        }
    }


    private void startNotificationService(Context context) {
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
