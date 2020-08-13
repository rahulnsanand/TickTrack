package com.theflopguyproductions.ticktrack.utils;
import android.os.Handler;
import android.util.Log;
import android.widget.TextClock;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.LinkedList;

import androidx.annotation.Nullable;

import com.theflopguyproductions.ticktrack.stopwatch.StopwatchLapData;

import org.w3c.dom.Text;

public class TickTrackStopwatchTimer {
    private ArrayList<StopwatchLapData> stopwatchLapData;
    private TextView hourMinute, milliSecond;
    private long start, current, elapsedTime, lapTime;
    private boolean started, paused;
    private OnTickListener onTickListener;
    private long clockDelay;
    private Handler handler;
    private TickTrackDatabase tickTrackDatabase;

    private final Runnable runnable = this::run;

    public TickTrackStopwatchTimer(TickTrackDatabase tickTrackDatabase) {
        this.tickTrackDatabase = tickTrackDatabase;
        start = System.currentTimeMillis();
        current = System.currentTimeMillis();
        elapsedTime = 0;
        started = false;
        paused = false;
        stopwatchLapData = tickTrackDatabase.retrieveStopwatchLapData();
        hourMinute = null;
        milliSecond= null;
        lapTime = 0;
        onTickListener = null;
        clockDelay = 10;
        handler = new Handler();
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

    public void setTextView(@Nullable TextView hourMinute, TextView milliSecond) {
        this.hourMinute = hourMinute;
        this.milliSecond = milliSecond;
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
            start = System.currentTimeMillis();
            current = System.currentTimeMillis();
            lapTime = 0;
            elapsedTime = 0;
            stopwatchLapData.clear();
            handler.post(runnable);
        }
    }

    public void stop() {
        if (!started)
            throw new IllegalStateException("Not Started");
        else {
            updateElapsed(System.currentTimeMillis());
            started = false;
            paused = false;
            handler.removeCallbacks(runnable);
        }
        if(this.hourMinute!=null)
            this.hourMinute.setText("00");

        if(this.milliSecond!=null)
            this.milliSecond.setText("00");

        stopwatchLapData.clear();
        tickTrackDatabase.storeLapNumber(0);
        tickTrackDatabase.storeLapData(stopwatchLapData);
    }

    public void pause() {
        if (paused)
            throw new IllegalStateException("Already Paused");
        else if (!started)
            throw new IllegalStateException("Not Started");
        else {
            updateElapsed(System.currentTimeMillis());
            paused = true;
            handler.removeCallbacks(runnable);
        }
    }

    public void resume() {
        if (!paused)
            throw new IllegalStateException("Not Paused");
        else if (!started)
            throw new IllegalStateException("Not Started");
        else {
            paused = false;
            current = System.currentTimeMillis();
            handler.post(runnable);
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

    }

    private void updateElapsed(long time) {
        elapsedTime += time - current;
        lapTime += time - current;
        current = time;
    }


    private void run() {
        if (!started || paused) {
            handler.removeCallbacks(runnable);
            return;
        }

        updateElapsed(System.currentTimeMillis());
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

}