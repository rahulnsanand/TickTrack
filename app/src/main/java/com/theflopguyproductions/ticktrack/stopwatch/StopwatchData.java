package com.theflopguyproductions.ticktrack.stopwatch;

public class StopwatchData {

    boolean isRunning, isPause, isNotification;
    long lastLapEndTimeInMillis, lastPauseTimeInMillis, lastPauseTimeRealTimeInMillis, lastPauseValueInMillis;
    long progressValue, stopwatchTimerStartTimeInMillis, stopwatchTimerStartTimeInRealTimeMillis;

    public long getLastPauseValueInMillis() {
        return lastPauseValueInMillis;
    }

    public void setLastPauseValueInMillis(long lastPauseValueInMillis) {
        this.lastPauseValueInMillis = lastPauseValueInMillis;
    }

    public long getLastPauseTimeRealTimeInMillis() {
        return lastPauseTimeRealTimeInMillis;
    }

    public void setLastPauseTimeRealTimeInMillis(long lastPauseTimeRealTimeInMillis) {
        this.lastPauseTimeRealTimeInMillis = lastPauseTimeRealTimeInMillis;
    }

    public long getLastPauseTimeInMillis() {
        return lastPauseTimeInMillis;
    }

    public void setLastPauseTimeInMillis(long lastPauseTimeInMillis) {
        this.lastPauseTimeInMillis = lastPauseTimeInMillis;
    }

    public long getStopwatchTimerStartTimeInRealTimeMillis() {
        return stopwatchTimerStartTimeInRealTimeMillis;
    }

    public void setStopwatchTimerStartTimeInRealTimeMillis(long stopwatchTimerStartTimeInRealTimeMillis) {
        this.stopwatchTimerStartTimeInRealTimeMillis = stopwatchTimerStartTimeInRealTimeMillis;
    }

    public long getStopwatchTimerStartTimeInMillis() {
        return stopwatchTimerStartTimeInMillis;
    }

    public void setStopwatchTimerStartTimeInMillis(long stopwatchTimerStartTimeInMillis) {
        this.stopwatchTimerStartTimeInMillis = stopwatchTimerStartTimeInMillis;
    }

    public long getProgressValue() {
        return progressValue;
    }

    public void setProgressValue(long progressValue) {
        this.progressValue = progressValue;
    }

    public boolean isRunning() {
        return isRunning;
    }

    public void setRunning(boolean running) {
        isRunning = running;
    }

    public boolean isPause() {
        return isPause;
    }

    public void setPause(boolean pause) {
        isPause = pause;
    }

    public boolean isNotification() {
        return isNotification;
    }

    public void setNotification(boolean notification) {
        isNotification = notification;
    }

    public long getLastLapEndTimeInMillis() {
        return lastLapEndTimeInMillis;
    }

    public void setLastLapEndTimeInMillis(long lastLapEndTimeInMillis) {
        this.lastLapEndTimeInMillis = lastLapEndTimeInMillis;
    }

}
