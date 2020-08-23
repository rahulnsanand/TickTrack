package com.theflopguyproductions.ticktrack.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;

import com.theflopguyproductions.ticktrack.TimerManagementHelper;
import com.theflopguyproductions.ticktrack.stopwatch.StopwatchData;
import com.theflopguyproductions.ticktrack.utils.database.TickTrackDatabase;

import java.util.ArrayList;

public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        TimerManagementHelper timerManagementHelper = new TimerManagementHelper(context);

        timerManagementHelper.reestablishTimers();

        TickTrackDatabase tickTrackDatabase = new TickTrackDatabase(context);
        ArrayList<StopwatchData> stopwatchData = tickTrackDatabase.retrieveStopwatchData();
        if(stopwatchData.get(0).getStopwatchTimerStartTimeInMillis()!=-1){
            long timeElapsedOnBoot = System.currentTimeMillis() - stopwatchData.get(0).getStopwatchTimerStartTimeInMillis();
            stopwatchData.get(0).setStopwatchTimerStartTimeInRealTimeMillis(SystemClock.elapsedRealtime()-timeElapsedOnBoot);

            if(stopwatchData.get(0).isPause() && stopwatchData.get(0).isRunning()){
                long pauseElapsedOnBoot = System.currentTimeMillis() - stopwatchData.get(0).getLastPauseTimeInMillis();
                stopwatchData.get(0).setLastPauseTimeRealTimeInMillis(SystemClock.elapsedRealtime()-pauseElapsedOnBoot);
            }
            tickTrackDatabase.storeStopwatchData(stopwatchData);
        }
    }


}
