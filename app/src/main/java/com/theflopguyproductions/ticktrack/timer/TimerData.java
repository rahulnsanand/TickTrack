package com.theflopguyproductions.ticktrack.timer;

import java.sql.Timestamp;

public class TimerData implements Comparable<TimerData> {


    int timerHour, timerMinute, timerSecond, timerIntegerID, timerFlag;
    int timerHourLeft, timerMinuteLeft, timerSecondLeft;
    float timerMilliSecondLeft;
    long timerEndTimeInMillis, timerTotalTimeInMillis, timerStopTime, timerStopSeconds;
    Timestamp timerCreateTimeStamp;
    String timerStringID, timerLabel;
    boolean timerOn, timerPause, timerReset, isNew, timerStop;

    public long getTimerStopSeconds() {
        return timerStopSeconds;
    }

    public void setTimerStopSeconds(long timerStopSeconds) {
        this.timerStopSeconds = timerStopSeconds;
    }

    public long getTimerStopTime() {
        return timerStopTime;
    }

    public void setTimerStopTime(long timerStopTime) {
        this.timerStopTime = timerStopTime;
    }

    public boolean isTimerStop() {
        return timerStop;
    }

    public void setTimerStop(boolean timerStop) {
        this.timerStop = timerStop;
    }

    public boolean isNew() {
        return isNew;
    }

    public void setNew(boolean aNew) {
        isNew = aNew;
    }

    public long getTimerTotalTimeInMillis() {
        return timerTotalTimeInMillis;
    }

    public void setTimerTotalTimeInMillis(long timerTotalTimeInMillis) {
        this.timerTotalTimeInMillis = timerTotalTimeInMillis;
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

    public int getTimerIntegerID() {
        return timerIntegerID;
    }

    public void setTimerIntegerID(int timerIntegerID) {
        this.timerIntegerID = timerIntegerID;
    }

    public int getTimerFlag() {
        return timerFlag;
    }

    public void setTimerFlag(int timerFlag) {
        this.timerFlag = timerFlag;
    }

    public int getTimerHourLeft() {
        return timerHourLeft;
    }

    public void setTimerHourLeft(int timerHourLeft) {
        this.timerHourLeft = timerHourLeft;
    }

    public int getTimerMinuteLeft() {
        return timerMinuteLeft;
    }

    public void setTimerMinuteLeft(int timerMinuteLeft) {
        this.timerMinuteLeft = timerMinuteLeft;
    }

    public int getTimerSecondLeft() {
        return timerSecondLeft;
    }

    public void setTimerSecondLeft(int timerSecondLeft) {
        this.timerSecondLeft = timerSecondLeft;
    }

    public float getTimerMilliSecondLeft() {
        return timerMilliSecondLeft;
    }

    public void setTimerMilliSecondLeft(float timerMilliSecondLeft) {
        this.timerMilliSecondLeft = timerMilliSecondLeft;
    }

    public long getTimerEndTimeInMillis() {
        return timerEndTimeInMillis;
    }

    public void setTimerEndTimeInMillis(long timerEndTimeInMillis) {
        this.timerEndTimeInMillis = timerEndTimeInMillis;
    }

    public Timestamp getTimerCreateTimeStamp() {
        return timerCreateTimeStamp;
    }

    public void setTimerCreateTimeStamp(Timestamp timerCreateTimeStamp) {
        this.timerCreateTimeStamp = timerCreateTimeStamp;
    }

    public String getTimerStringID() {
        return timerStringID;
    }

    public void setTimerStringID(String timerStringID) {
        this.timerStringID = timerStringID;
    }

    public String getTimerLabel() {
        return timerLabel;
    }

    public void setTimerLabel(String timerLabel) {
        this.timerLabel = timerLabel;
    }

    public boolean isTimerOn() {
        return timerOn;
    }

    public void setTimerOn(boolean timerOn) {
        this.timerOn = timerOn;
    }

    public boolean isTimerPause() {
        return timerPause;
    }

    public void setTimerPause(boolean timerPause) {
        this.timerPause = timerPause;
    }

    public boolean isTimerReset() {
        return timerReset;
    }

    public void setTimerReset(boolean timerReset) {
        this.timerReset = timerReset;
    }

    @Override
    public int compareTo(TimerData timerData) {
        return 0;
    }
}
