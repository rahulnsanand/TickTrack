package com.theflopguyproductions.ticktrack.utils.helpers;

import android.content.Context;

import com.theflopguyproductions.ticktrack.timer.data.TimerData;
import com.theflopguyproductions.ticktrack.utils.database.TickTrackDatabase;

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


}
