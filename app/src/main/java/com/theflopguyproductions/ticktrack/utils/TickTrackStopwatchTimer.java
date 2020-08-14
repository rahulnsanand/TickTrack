package com.theflopguyproductions.ticktrack.utils;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.widget.TextClock;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.LinkedList;

import androidx.annotation.Nullable;

import com.theflopguyproductions.ticktrack.stopwatch.StopwatchData;
import com.theflopguyproductions.ticktrack.stopwatch.StopwatchLapData;
import com.theflopguyproductions.ticktrack.ui.utils.TickTrackProgressBar;

import org.w3c.dom.Text;

public class TickTrackStopwatchTimer {
    private ArrayList<StopwatchLapData> stopwatchLapData;
    private ArrayList<StopwatchData> stopwatchDataArrayList;
    private TextView hourMinute, milliSecond;
    private long start, current, elapsedTime, lapTime, progressValue, currentValue, maxProgressDurationInMillis = 200;
    private boolean started, paused;
    private OnTickListener onTickListener;
    private long clockDelay;
    private Handler handler, storageHandler, progressHandler;
    private TickTrackDatabase tickTrackDatabase;
    private TickTrackProgressBar tickTrackProgressBar;

    private final Runnable runnable = this::run;
    private final Runnable storageRunnable = this::storageRunnable;
    private final Runnable progressRunnable = this::progressRunnable;

    public TickTrackStopwatchTimer(TickTrackDatabase tickTrackDatabase) {

        this.tickTrackDatabase = tickTrackDatabase;
        stopwatchDataArrayList = tickTrackDatabase.retrieveStopwatchData();

        if(!(stopwatchDataArrayList.size() > 0)){
            System.out.println("New Stopwatch Data Added");
            StopwatchData stopwatchData = new StopwatchData();
            stopwatchData.setPause(false);
            stopwatchData.setRunning(false);
            stopwatchData.setLastUpdatedValue(0);
            stopwatchData.setRecentLocalTimeInMillis(0);
            stopwatchData.setRecentRealTimeInMillis(0);
            stopwatchData.setStartTimeInMillis(0);
            stopwatchDataArrayList.add(stopwatchData);
            tickTrackDatabase.storeStopwatchData(stopwatchDataArrayList);
            stopwatchDataArrayList = tickTrackDatabase.retrieveStopwatchData();
        }

        started = stopwatchDataArrayList.get(0).isRunning();
        paused = stopwatchDataArrayList.get(0).isPause();

        if(started && !paused){
            if(SystemClock.elapsedRealtime()>stopwatchDataArrayList.get(0).getRecentRealTimeInMillis()){
                start = stopwatchDataArrayList.get(0).getRecentRealTimeInMillis();
                current = stopwatchDataArrayList.get(0).getRecentRealTimeInMillis();
            } else {
                start = stopwatchDataArrayList.get(0).getRecentLocalTimeInMillis();
                current = stopwatchDataArrayList.get(0).getRecentLocalTimeInMillis();
            }

            elapsedTime = stopwatchDataArrayList.get(0).getLastUpdatedValue();
            progressValue = stopwatchDataArrayList.get(0).getLastUpdatedValue();
        } else if (paused){
            if(SystemClock.elapsedRealtime()>stopwatchDataArrayList.get(0).getRecentRealTimeInMillis()){
                start = stopwatchDataArrayList.get(0).getRecentRealTimeInMillis();
                current = stopwatchDataArrayList.get(0).getRecentRealTimeInMillis();
            } else {
                start = stopwatchDataArrayList.get(0).getRecentLocalTimeInMillis();
                current = stopwatchDataArrayList.get(0).getRecentLocalTimeInMillis();
            }
            elapsedTime = stopwatchDataArrayList.get(0).getLastUpdatedValue();
            progressValue = stopwatchDataArrayList.get(0).getLastUpdatedValue();
        } else {
            start = SystemClock.elapsedRealtime();
            current = SystemClock.elapsedRealtime();
            elapsedTime = 0;
            progressValue = 0;
        }

        stopwatchLapData = tickTrackDatabase.retrieveStopwatchLapData();

        hourMinute = null;
        milliSecond= null;
        lapTime = 0;
        onTickListener = null;
        clockDelay = 10;
        handler = new Handler();
        storageHandler = new Handler();
        progressHandler = new Handler();
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

    public boolean isStarted() {
        return started;
    }

    public boolean isPaused() {
        return paused;
    }

    public long getElapsedTime() {
        return elapsedTime;
    }

    public long getStart() {
        return start;
    }

    public ArrayList<StopwatchLapData> getSplits() {
        return stopwatchLapData;
    }

    @SuppressWarnings("unused")
    public long getClockDelay() {
        return clockDelay;
    }

    public void setClockDelay(long clockDelay) {
        this.clockDelay = clockDelay;
    }

    public void setTextView(@Nullable TextView hourMinute, TextView milliSecond, TickTrackProgressBar tickTrackProgressBar) {
        this.hourMinute = hourMinute;
        this.milliSecond = milliSecond;
        this.tickTrackProgressBar = tickTrackProgressBar;
    }

    public void setOnTickListener(OnTickListener onTickListener) {
        this.onTickListener = onTickListener;
    }

    public void start() {
        if (started)
            throw new IllegalStateException("Already Started");
        else {
            started = true;
            paused = false;
            start = SystemClock.elapsedRealtime();
            current = SystemClock.elapsedRealtime();
            lapTime = 0;
            elapsedTime = 0;
            stopwatchLapData.clear();
            handler.post(runnable);

            storageHandler.post(storageRunnable);

            stopwatchDataArrayList = tickTrackDatabase.retrieveStopwatchData();
            stopwatchDataArrayList.get(0).setRunning(true);
            stopwatchDataArrayList.get(0).setPause(false);
            tickTrackDatabase.storeStopwatchData(stopwatchDataArrayList);

        }
    }

    public void stop() {
        if (!started)
            throw new IllegalStateException("Not Started");
        else {
            updateElapsed(SystemClock.elapsedRealtime());
            started = false;
            paused = false;
            handler.removeCallbacks(runnable);
            stopwatchDataArrayList.get(0).setRunning(false);
            stopwatchDataArrayList.get(0).setPause(false);
            tickTrackDatabase.storeStopwatchData(stopwatchDataArrayList);
        }
        if(this.hourMinute!=null)
            this.hourMinute.setText("00");

        if(this.milliSecond!=null)
            this.milliSecond.setText("00");

        stopwatchLapData.clear();
        tickTrackDatabase.storeLapNumber(0);
        tickTrackDatabase.storeLapData(stopwatchLapData);
        stopProgressBar();
    }

    public void pause() {
        if (paused)
            throw new IllegalStateException("Already Paused");
        else if (!started)
            throw new IllegalStateException("Not Started");
        else {
            updateElapsed(SystemClock.elapsedRealtime());
            paused = true;
            handler.removeCallbacks(runnable);
            if(progressHandler!=null)
                progressHandler.removeCallbacks(progressRunnable);

            stopwatchDataArrayList.get(0).setPause(true);
            stopwatchDataArrayList.get(0).setRunning(true);
            tickTrackDatabase.storeStopwatchData(stopwatchDataArrayList);
        }
    }

    public void resume() {
        if (!paused)
            throw new IllegalStateException("Not Paused");
        else if (!started)
            throw new IllegalStateException("Not Started");
        else {
            paused = false;
            current = SystemClock.elapsedRealtime();
            handler.post(runnable);
            if(!(currentValue > 0) && tickTrackDatabase.retrieveStopwatchLapData().size()>0){
                currentValue=0;
                startProgressBar();
            } else {
                startProgressBar(currentValue);
            }
        }
    }


    public void lap() {

        if (!started)
            throw new IllegalStateException("Not Started");

        int currentLapNumber = tickTrackDatabase.retrieveLapNumber();
        if(!(currentLapNumber >=0)){
            currentLapNumber = 0;
        }
        StopwatchLapData stopwatchLapData = new StopwatchLapData();
        stopwatchLapData.setLapNumber(currentLapNumber+1);
        stopwatchLapData.setElapsedTimeInMillis(elapsedTime);
        stopwatchLapData.setLapTimeInMillis(lapTime);
        this.stopwatchLapData.add(stopwatchLapData);
        this.tickTrackDatabase.storeLapData(this.stopwatchLapData);
        tickTrackDatabase.storeLapNumber(currentLapNumber+1);
        lapTime = 0;

        if(progressHandler!=null)
            progressHandler.removeCallbacks(progressRunnable);
        startProgressBar();
        currentValue=0;

    }

    private void updateElapsed(long time) {
        elapsedTime += time - current;
        lapTime += time - current;
        current = time;
    }

    private void storageRunnable() {
        stopwatchDataArrayList.get(0).setRecentRealTimeInMillis(SystemClock.elapsedRealtime());
        stopwatchDataArrayList.get(0).setRecentLocalTimeInMillis(System.currentTimeMillis());
        stopwatchDataArrayList.get(0).setLastUpdatedValue(elapsedTime);
        tickTrackDatabase.storeStopwatchData(stopwatchDataArrayList);
        storageHandler.post(storageRunnable);
    }

    private void progressRunnable() {
        if(currentValue<=maxProgressDurationInMillis){
            this.tickTrackProgressBar.setProgress(getCurrentStep(currentValue, maxProgressDurationInMillis));
            this.currentValue = this.currentValue + 1;
        } else {
            this.currentValue = 0;
        }
        progressHandler.post(progressRunnable);
    }

    private void startProgressBar() {
        this.tickTrackProgressBar.setInstantProgress(0);
        progressHandler.post(progressRunnable);
    }

    private void startProgressBar(long currentValue) {
        this.tickTrackProgressBar.setInstantProgress(getCurrentStep(currentValue, maxProgressDurationInMillis));
        progressHandler.post(progressRunnable);
    }
    private void stopProgressBar() {
        progressHandler.removeCallbacks(progressRunnable);
        this.tickTrackProgressBar.setProgress(0);
        currentValue = 0;
    }

    private float getCurrentStep(long currentValue, long maxLength){
        return ((currentValue-0f)/(maxLength-0f)) *(1f-0f)+0f;
    }

    private void run() {
        if (!started || paused) {
            handler.removeCallbacks(runnable);
            return;
        }

        updateElapsed(SystemClock.elapsedRealtime());
        handler.postDelayed(runnable, clockDelay);

        if (onTickListener != null)
            onTickListener.onTick(this);

        if (hourMinute != null) {
            String hourMinute = getFormattedHourMinute(elapsedTime, this.hourMinute);
            this.hourMinute.setText(hourMinute);
        }
        if(milliSecond != null){
            String milliSecond = getFormattedMillisecond(elapsedTime);
            this.milliSecond.setText(milliSecond);
        }
    }

    public interface OnTickListener {
        void onTick(TickTrackStopwatchTimer stopwatch);
    }

    public void weAreDying(){



    }

}