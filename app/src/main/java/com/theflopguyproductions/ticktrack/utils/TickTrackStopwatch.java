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
    private long stopwatchRetrievedStartTime, stopwatchDurationElapsed, differenceValue = 0, lastLapEndTime =0, lapDifferenceValue=0;
    private Handler stopwatchHandler, storageHandler, progressBarHandler;
    private TickTrackDatabase tickTrackDatabase;
    private TickTrackProgressBar progressBar;
    private Context context;

    public TickTrackStopwatch(Context context){
        this.context = context;
        tickTrackDatabase = new TickTrackDatabase(context);
        stopwatchDataArrayList = tickTrackDatabase.retrieveStopwatchData();
        stopwatchLapData = tickTrackDatabase.retrieveStopwatchLapData();
        storageHandler = new Handler();
        stopwatchHandler = new Handler();
        progressBarHandler = new Handler();

        if(!(stopwatchDataArrayList.size() > 0)){
            System.out.println("New Stopwatch Data Added");
            StopwatchData stopwatchData = new StopwatchData();
            stopwatchData.setPause(false);
            stopwatchData.setRunning(false);
            stopwatchData.setLastLapEndTimeInMillis(0);
            stopwatchData.setRecentLocalTimeInMillis(0);
            stopwatchData.setRecentRealTimeInMillis(0);
            stopwatchData.setLastUpdatedValueInMillis(0);
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
        long elapsedRealTime = SystemClock.elapsedRealtime()+differenceValue;
        stopwatchDurationElapsed = elapsedRealTime - stopwatchRetrievedStartTime;
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
        stopwatchDataArrayList.get(0).setLastUpdatedValueInMillis(stopwatchDurationElapsed);
        tickTrackDatabase.storeStopwatchData(stopwatchDataArrayList);
        stopwatchDataArrayList = tickTrackDatabase.retrieveStopwatchData();
        storageHandler.postDelayed(storageRunnable, 10);
    }
    private void progressBarRunnable(){

    }

    public void start(){
        if (stopwatchDataArrayList.get(0).isRunning())
            throw new IllegalStateException("Already Started");
        else {

            stopwatchRetrievedStartTime = SystemClock.elapsedRealtime();
            stopwatchDurationElapsed = 0;
            differenceValue = 0;
            lapDifferenceValue = 0;
            lastLapEndTime = 0;
            stopwatchHandler.post(stopwatchRunnable);

            stopwatchLapData.clear();

            stopwatchDataArrayList.get(0).setRunning(true);
            stopwatchDataArrayList.get(0).setPause(false);
            stopwatchDataArrayList.get(0).setLastUpdatedValueInMillis(0);
            stopwatchDataArrayList.get(0).setLastLapEndTimeInMillis(0);
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
            stopwatchDataArrayList.get(0).setLastUpdatedValueInMillis(stopwatchDurationElapsed);
            tickTrackDatabase.storeStopwatchData(stopwatchDataArrayList);
            stopwatchDataArrayList = tickTrackDatabase.retrieveStopwatchData();
            //TODO HANDLER PROGRESSBAR SHIT
        }
    }
    public void resume() {
        if(!stopwatchDataArrayList.get(0).isPause()){
            throw new IllegalStateException("Not Paused");
        } else if (!stopwatchDataArrayList.get(0).isRunning()){
            throw new IllegalStateException("Not Started");
        } else {
            stopwatchDurationElapsed = stopwatchDataArrayList.get(0).getLastUpdatedValueInMillis();
            lapDifferenceValue = stopwatchDataArrayList.get(0).getLastLapEndTimeInMillis();
            lastLapEndTime = stopwatchDataArrayList.get(0).getLastLapEndTimeInMillis();
            stopwatchRetrievedStartTime = SystemClock.elapsedRealtime();
            differenceValue = stopwatchDurationElapsed;
            stopwatchHandler.post(stopwatchRunnable);
            stopwatchDataArrayList.get(0).setPause(false);
            stopwatchDataArrayList.get(0).setRunning(true);
            tickTrackDatabase.storeStopwatchData(stopwatchDataArrayList);
            stopwatchDataArrayList = tickTrackDatabase.retrieveStopwatchData();

            //TODO HANDLE PROGRESS BAR SHIT HERE
        }
    }
    public void stop(){
        if(!stopwatchDataArrayList.get(0).isRunning()){
            throw new IllegalStateException("Not Started");
        } else {

            stopwatchHandler.removeCallbacks(stopwatchRunnable);
            storageHandler.removeCallbacks(storageRunnable);

            stopwatchDataArrayList.get(0).setRunning(false);
            stopwatchDataArrayList.get(0).setPause(false);
            stopwatchDataArrayList.get(0).setRecentLocalTimeInMillis(0);
            stopwatchDataArrayList.get(0).setRecentRealTimeInMillis(0);
            stopwatchDataArrayList.get(0).setLastLapEndTimeInMillis(0);
            stopwatchDataArrayList.get(0).setLastUpdatedValueInMillis(0);
            stopwatchDataArrayList.get(0).setNotification(false);
            tickTrackDatabase.storeStopwatchData(stopwatchDataArrayList);
            stopwatchDataArrayList = tickTrackDatabase.retrieveStopwatchData();

            if(this.hourMinuteText!=null)
                this.hourMinuteText.setText("00");

            if(this.milliSecondText!=null)
                this.milliSecondText.setText("00");

            stopwatchLapData.clear();
            tickTrackDatabase.storeLapNumber(0);
            tickTrackDatabase.storeLapData(stopwatchLapData);

            //TODO HANDLE PROGRESS BAR HERE
        }
    }
    public void lap(){
        if(!stopwatchDataArrayList.get(0).isRunning()){
            throw new IllegalStateException("Not Started");
        } else {

            long lapTime = stopwatchDurationElapsed - stopwatchDataArrayList.get(0).getLastLapEndTimeInMillis();
            stopwatchDataArrayList.get(0).setLastLapEndTimeInMillis(stopwatchDurationElapsed);

            int currentLapNumber = tickTrackDatabase.retrieveLapNumber();
            if(!(currentLapNumber >= 0)){
                currentLapNumber = 0;
            }

            StopwatchLapData stopwatchLapData = new StopwatchLapData();
            stopwatchLapData.setLapNumber(currentLapNumber+1);
            stopwatchLapData.setElapsedTimeInMillis(stopwatchDurationElapsed);
            stopwatchLapData.setLapTimeInMillis(lapTime);
            this.stopwatchLapData.add(stopwatchLapData);
            tickTrackDatabase.storeLapData(this.stopwatchLapData);
            tickTrackDatabase.storeLapNumber(currentLapNumber+1);
            lastLapEndTime = SystemClock.elapsedRealtime();

            //TODO HANDLE PROGRESS BAR
        }
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

    public void setupPauseValues() {
        stopwatchDurationElapsed = stopwatchDataArrayList.get(0).getLastUpdatedValueInMillis();
        updateTextView();
    }

    public void setupResumeValues(){
        if(!(SystemClock.elapsedRealtime() > stopwatchDataArrayList.get(0).getRecentRealTimeInMillis())){
            differenceValue = calculateDifferenceTime();
        } else {
            differenceValue = (SystemClock.elapsedRealtime() - stopwatchDataArrayList.get(0).getRecentRealTimeInMillis())+stopwatchDataArrayList.get(0).getLastUpdatedValueInMillis();
        }

        lastLapEndTime = stopwatchDataArrayList.get(0).getLastLapEndTimeInMillis();
        stopwatchRetrievedStartTime = SystemClock.elapsedRealtime();
//        differenceValue = stopwatchDataArrayList.get(0).getLastUpdatedValueInMillis();
        lapDifferenceValue = stopwatchDataArrayList.get(0).getLastLapEndTimeInMillis();
        stopwatchHandler.post(stopwatchRunnable);
        stopwatchDataArrayList.get(0).setPause(false);
        stopwatchDataArrayList.get(0).setRunning(true);
        tickTrackDatabase.storeStopwatchData(stopwatchDataArrayList);
        stopwatchDataArrayList = tickTrackDatabase.retrieveStopwatchData();
    }

    private long calculateDifferenceTime() {
        assert stopwatchDataArrayList != null;
        long lastUpdateValue = stopwatchDataArrayList.get(0).getLastUpdatedValueInMillis();
        long differenceValue = System.currentTimeMillis() - stopwatchDataArrayList.get(0).getRecentLocalTimeInMillis();

        return lastUpdateValue+differenceValue;
    }

    public void onStopCalled() {
        stopwatchHandler.removeCallbacks(stopwatchRunnable);
        storageHandler.removeCallbacks(storageRunnable);
    }
}
