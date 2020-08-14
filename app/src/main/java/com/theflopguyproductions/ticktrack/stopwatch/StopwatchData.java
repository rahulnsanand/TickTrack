package com.theflopguyproductions.ticktrack.stopwatch;

public class StopwatchData {

    boolean isRunning, isPause, isNotification;
    long startTimeInMillis, recentRealTimeInMillis, recentLocalTimeInMillis, lastUpdatedValue;

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

    public long getStartTimeInMillis() {
        return startTimeInMillis;
    }

    public void setStartTimeInMillis(long startTimeInMillis) {
        this.startTimeInMillis = startTimeInMillis;
    }

    public long getRecentRealTimeInMillis() {
        return recentRealTimeInMillis;
    }

    public void setRecentRealTimeInMillis(long recentRealTimeInMillis) {
        this.recentRealTimeInMillis = recentRealTimeInMillis;
    }

    public long getRecentLocalTimeInMillis() {
        return recentLocalTimeInMillis;
    }

    public void setRecentLocalTimeInMillis(long recentLocalTimeInMillis) {
        this.recentLocalTimeInMillis = recentLocalTimeInMillis;
    }

    public long getLastUpdatedValue() {
        return lastUpdatedValue;
    }

    public void setLastUpdatedValue(long lastUpdatedValue) {
        this.lastUpdatedValue = lastUpdatedValue;
    }


}
