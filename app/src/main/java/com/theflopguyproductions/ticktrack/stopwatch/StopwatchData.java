package com.theflopguyproductions.ticktrack.stopwatch;

public class StopwatchData {

    int stopwatchID;
    boolean isRunning, isPause, isNotification;
    long startTimeInMillis;

    public int getStopwatchID() {
        return stopwatchID;
    }

    public void setStopwatchID(int stopwatchID) {
        this.stopwatchID = stopwatchID;
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

    public long getStartTimeInMillis() {
        return startTimeInMillis;
    }

    public void setStartTimeInMillis(long startTimeInMillis) {
        this.startTimeInMillis = startTimeInMillis;
    }
}
