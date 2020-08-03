package com.theflopguyproductions.ticktrack.timer.service;

public class TimerServiceData {

    long endTimeInMillis;
    int timerIDInteger;
    String timerIDString;

    public long getEndTimeInMillis() {
        return endTimeInMillis;
    }

    public void setEndTimeInMillis(long endTimeInMillis) {
        this.endTimeInMillis = endTimeInMillis;
    }

    public int getTimerIDInteger() {
        return timerIDInteger;
    }

    public void setTimerIDInteger(int timerIDInteger) {
        this.timerIDInteger = timerIDInteger;
    }

    public String getTimerIDString() {
        return timerIDString;
    }

    public void setTimerIDString(String timerIDString) {
        this.timerIDString = timerIDString;
    }
}
