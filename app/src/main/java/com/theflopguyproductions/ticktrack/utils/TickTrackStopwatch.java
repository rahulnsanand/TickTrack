package com.theflopguyproductions.ticktrack.utils;

import android.content.Context;
import android.os.Handler;
import android.os.SystemClock;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.theflopguyproductions.ticktrack.stopwatch.StopwatchData;
import com.theflopguyproductions.ticktrack.stopwatch.StopwatchLapData;
import com.theflopguyproductions.ticktrack.ui.utils.TickTrackProgressBar;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;

public class TickTrackStopwatch {

    private ArrayList<StopwatchData> stopwatchDataArrayList;
    private ArrayList<StopwatchLapData> stopwatchLapData;
    private TextView hourMinuteText, milliSecondText;
    private long stopwatchStartTime, stopwatchLastCurrentTimeUpdate, stopwatchLastRealtimeUpdate, stopwatchDurationElapsed, stopwatchCurrentLapDurationElapsed, lapEndTimeInMillis;
    private Handler stopwatchHandler, storageHandler, progressBarHandler;
    private TickTrackDatabase tickTrackDatabase;
    private TickTrackProgressBar progressBar;
    private Context context;

    public TickTrackStopwatch(Context context){
        this.context = context;
        tickTrackDatabase = new TickTrackDatabase(context);
        stopwatchDataArrayList = tickTrackDatabase.retrieveStopwatchData();
        storageHandler = new Handler();
        stopwatchHandler = new Handler();
        progressBarHandler = new Handler();

        if(!(stopwatchDataArrayList.size() > 0)){
            System.out.println("New Stopwatch Data Added");
            StopwatchData stopwatchData = new StopwatchData();
            stopwatchData.setPause(false);
            stopwatchData.setRunning(false);
            stopwatchData.setLastUpdatedValue(0);
            stopwatchData.setRecentLocalTimeInMillis(0);
            stopwatchData.setRecentRealTimeInMillis(0);
            stopwatchData.setStartTimeInMillis(0);
            stopwatchDataArrayList.add(0, stopwatchData);
            tickTrackDatabase.storeStopwatchData(stopwatchDataArrayList);
            stopwatchDataArrayList = tickTrackDatabase.retrieveStopwatchData();
        }


    }

    private final Runnable stopwatchRunnable = this::stopwatchRunnable;
    private final Runnable storageRunnable = this::storageRunnable;
    private final Runnable progressBarRunnable = this::progressBarRunnable;


    private void stopwatchRunnable(){
        if (!stopwatchDataArrayList.get(0).isRunning() || stopwatchDataArrayList.get(0).isPause()) {
            stopwatchHandler.removeCallbacks(stopwatchRunnable);
            return;
        }
        stopwatchDurationElapsed = SystemClock.elapsedRealtime() - stopwatchStartTime;
        stopwatchCurrentLapDurationElapsed = SystemClock.elapsedRealtime() - lapEndTimeInMillis;
        updateTextView();
        stopwatchHandler.postDelayed(stopwatchRunnable, 10);
    }

    private void updateTextView() {
        if (hourMinuteText != null) {
            String hourMinute = getFormattedHourMinute(stopwatchDurationElapsed, this.hourMinuteText);
            this.hourMinuteText.setText(hourMinute);
        }
        if(milliSecondText != null){
            String milliSecond = getFormattedMillisecond(stopwatchDurationElapsed);
            this.milliSecondText.setText(milliSecond);
        }
    }

    private void storageRunnable(){
        stopwatchDataArrayList.get(0).setRecentRealTimeInMillis(SystemClock.elapsedRealtime());
        stopwatchDataArrayList.get(0).setRecentLocalTimeInMillis(System.currentTimeMillis());
        tickTrackDatabase.storeStopwatchData(stopwatchDataArrayList);
        stopwatchDataArrayList = tickTrackDatabase.retrieveStopwatchData();
    }
    private void progressBarRunnable(){

    }

    public void start(){
        if (stopwatchDataArrayList.get(0).isRunning())
            throw new IllegalStateException("Already Started");
        else {

            stopwatchStartTime = SystemClock.elapsedRealtime();
            lapEndTimeInMillis = SystemClock.elapsedRealtime();
            stopwatchCurrentLapDurationElapsed = 0;

            stopwatchLapData.clear();

            stopwatchHandler.post(stopwatchRunnable);

            stopwatchDataArrayList.get(0).setRunning(true);
            stopwatchDataArrayList.get(0).setPause(false);
            tickTrackDatabase.storeStopwatchData(stopwatchDataArrayList);
            stopwatchDataArrayList = tickTrackDatabase.retrieveStopwatchData();
            storageHandler.post(storageRunnable);

        }
    }
    public void pause(){
        if(stopwatchDataArrayList.get(0).isPause()){
            throw new IllegalStateException("Already Paused");
        } else if(!stopwatchDataArrayList.get(0).isRunning()){
            throw new IllegalStateException("Not Started");
        } else {
            updateTextView();
            stopwatchHandler.removeCallbacks(stopwatchRunnable);
            storageHandler.removeCallbacks(storageRunnable);

            stopwatchDataArrayList.get(0).setRunning(true);
            stopwatchDataArrayList.get(0).setPause(true);
            tickTrackDatabase.storeStopwatchData(stopwatchDataArrayList);
            stopwatchDataArrayList = tickTrackDatabase.retrieveStopwatchData();
            //TODO HANDLER PROGERSSBAR SHIT

        }
    }
    public void resume(){

    }
    public void stop(){

    }
    public void lap(){

    }

    public void setGraphics(@Nullable TextView hourMinute, TextView milliSecond, TickTrackProgressBar tickTrackProgressBar) {
        this.hourMinuteText = hourMinute;
        this.milliSecondText = milliSecond;
        this.progressBar = tickTrackProgressBar;
    }

    static String getFormattedMillisecond(long elapsedTime) {
        int milliseconds = (int) ((elapsedTime % 1000) / 10);
        NumberFormat f = new DecimalFormat("00");
        return f.format(milliseconds);
    }

    static String getFormattedHourMinute(long elapsedTime, TextView hourMinute) {
        final StringBuilder displayTime = new StringBuilder();
        int seconds = (int) ((elapsedTime / 1000) % 60);
        int minutes = (int) (elapsedTime / (60 * 1000) % 60);
        int hours = (int) (elapsedTime / (60 * 60 * 1000));
        NumberFormat f = new DecimalFormat("00");
        if (minutes == 0) {
            displayTime.append(f.format(seconds));
            hourMinute.setTextSize(72);
        }
        else if (hours == 0) {
            displayTime.append(f.format(minutes)).append(":").append(f.format(seconds));
            hourMinute.setTextSize(54);
        }
        else {
            displayTime.append(hours).append(":").append(f.format(minutes)).append(":").append(f.format(seconds));
            hourMinute.setTextSize(38);
        }
        return displayTime.toString();
    }

}
