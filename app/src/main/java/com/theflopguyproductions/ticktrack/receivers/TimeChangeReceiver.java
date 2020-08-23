package com.theflopguyproductions.ticktrack.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.widget.Toast;

import com.theflopguyproductions.ticktrack.stopwatch.StopwatchData;
import com.theflopguyproductions.ticktrack.timer.TimerData;
import com.theflopguyproductions.ticktrack.utils.database.TickTrackDatabase;

import java.util.ArrayList;

public class TimeChangeReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        TickTrackDatabase tickTrackDatabase = new TickTrackDatabase(context);

        ArrayList<TimerData> timerData = tickTrackDatabase.retrieveTimerList();

        for(int i = 0; i <timerData.size(); i ++){
            if(timerData.get(i).isTimerOn()){
                if(timerData.get(i).getTimerStartTimeInMillis()!=-1){

                    timerData.get(i).setTimerStartTimeInMillis(System.currentTimeMillis() - (timerData.get(i).getTimerTotalTimeInMillis()
                            - (timerData.get(i).getTimerAlarmEndTimeInMillis() - SystemClock.elapsedRealtime())));

                    Toast.makeText(context, "Time Changed", Toast.LENGTH_SHORT).show();

                }
            }
        }
        tickTrackDatabase.storeTimerList(timerData);

        ArrayList<StopwatchData> stopwatchData = tickTrackDatabase.retrieveStopwatchData();

        if(stopwatchData.size()>0){
            if(stopwatchData.get(0).getStopwatchTimerStartTimeInMillis()!=-1){
                if(!stopwatchData.get(0).isPause()){
                    stopwatchData.get(0).setStopwatchTimerStartTimeInMillis(System.currentTimeMillis()
                            - (SystemClock.elapsedRealtime()-stopwatchData.get(0).getStopwatchTimerStartTimeInRealTimeMillis()));

                    Toast.makeText(context, "STOPWATCH START TIME CHANGE", Toast.LENGTH_SHORT).show();
                }
                if(stopwatchData.get(0).isPause()){
                    stopwatchData.get(0).setLastPauseTimeInMillis(System.currentTimeMillis() - (SystemClock.elapsedRealtime() - stopwatchData.get(0).getLastPauseTimeRealTimeInMillis()));

                    Toast.makeText(context, "STOPWATCH PAUSE TIME CHANGE", Toast.LENGTH_SHORT).show();
                }
            }
        }
        tickTrackDatabase.storeStopwatchData(stopwatchData);

    }
}