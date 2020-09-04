package com.theflopguyproductions.ticktrack.timer.data;

public class TimerBackupData {

    int timerHour, timerMinute, timerSecond, timerFlag, timerIntID;
    long timerTotalTimeInMillis;
    long timerLastEdited;
    String  timerID,timerLabel;

    public int getTimerIntID() {
        return timerIntID;
    }

    public void setTimerIntID(int timerIntID) {
        this.timerIntID = timerIntID;
    }

    public int getTimerHour() {
        return timerHour;
    }

    public void setTimerHour(int timerHour) {
        this.timerHour = timerHour;
    }

    public int getTimerMinute() {
        return timerMinute;
    }

    public void setTimerMinute(int timerMinute) {
        this.timerMinute = timerMinute;
    }

    public int getTimerSecond() {
        return timerSecond;
    }

    public void setTimerSecond(int timerSecond) {
        this.timerSecond = timerSecond;
    }

    public String getTimerID() {
        return timerID;
    }

    public void setTimerID(String timerID) {
        this.timerID = timerID;
    }

    public int getTimerFlag() {
        return timerFlag;
    }

    public void setTimerFlag(int timerFlag) {
        this.timerFlag = timerFlag;
    }

    public long getTimerTotalTimeInMillis() {
        return timerTotalTimeInMillis;
    }

    public void setTimerTotalTimeInMillis(long timerTotalTimeInMillis) {
        this.timerTotalTimeInMillis = timerTotalTimeInMillis;
    }

    public long getTimerLastEdited() {
        return timerLastEdited;
    }

    public void setTimerLastEdited(long timerLastEdited) {
        this.timerLastEdited = timerLastEdited;
    }

    public String getTimerLabel() {
        return timerLabel;
    }

    public void setTimerLabel(String timerLabel) {
        this.timerLabel = timerLabel;
    }
}
